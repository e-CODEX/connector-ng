/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.evidence.builder.ConnectorEvidenceBuilder;
import eu.ecodex.connector.infrastructure.evidence.exception.ConnectorEvidenceBuilderException;
import eu.ecodex.connector.infrastructure.evidence.model.ConnectorEvidenceMessageDetails;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import eu.ecodex.connector.infrastructure.util.HashValueBuilder;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import java.util.HexFormat;
import org.apache.commons.lang3.StringUtils;
import org.etsi.uri._02640.v2.EventReasonType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Domain implementation that produces signed ETSI REM evidence XML for a {@link ConnectorMessage}.
 *
 * <p>Maps each {@link ConnectorEvidenceType} to
 * the corresponding REM chain step, reuses prior evidences from {@code message.evidences()} where
 * required, and builds issuer metadata from {@link ConnectorEvidencesProperties}. Submission-level
 * evidence hashes the business payload (when present) using the configured digest.
 *
 * <p>Behavior is aligned with the legacy Domibus connector evidence toolkit.
 */
@Component
public class ConnectorEvidenceToolkitImpl implements ConnectorEvidenceToolkit {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final ConnectorEvidenceBuilder evidenceBuilder;
    private final HashValueBuilder hashValueBuilder;
    private final ConnectorEvidencesProperties evidencesProperties;

    /**
     * Creates the toolkit with evidence builder and storage dependencies.
     *
     * @param evidenceBuilder                 builds and signs REM XML for each evidence step
     * @param evidencePayloadHashValueBuilder digest for submission evidences over business payload
     * @param evidencesProperties             issuer gateway/postal address and signature settings
     */
    public ConnectorEvidenceToolkitImpl(
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider fileStorageProvider,
            ConnectorEvidenceBuilder evidenceBuilder,
            HashValueBuilder evidencePayloadHashValueBuilder,
            ConnectorEvidencesProperties evidencesProperties) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.evidenceBuilder = evidenceBuilder;
        this.hashValueBuilder = evidencePayloadHashValueBuilder;
        this.evidencesProperties = evidencesProperties;
    }

    @Override
    public ConnectorMessageEvidence create(
            @NonNull ConnectorMessage message,
            @NonNull ConnectorEvidenceType evidenceType,
            @Nullable ConnectorMessageRejectionReason rejectionReason) {
        byte[] content;
        try {
            content = generateEvidenceBytes(evidenceType, message, rejectionReason);

            return ConnectorMessageEvidence.builder()
                                           .type(evidenceType)
                                           .content(content)
                                           .build();
        } catch (ConnectorEvidenceBuilderException e) {
            throw new ConnectorEvidenceException("evidence could not be created", e);
        }
    }

    private byte[] generateEvidenceBytes(
            ConnectorEvidenceType type,
            ConnectorMessage message,
            ConnectorMessageRejectionReason rejectionReason)
            throws ConnectorEvidenceBuilderException {

        return switch (type) {
            case SUBMISSION_ACCEPTANCE -> createSubmissionAcceptance(message);
            case SUBMISSION_REJECTION -> createSubmissionRejection(rejectionReason, message);
            case DELIVERY -> createDeliveryEvidence(message);
            case NON_DELIVERY -> createNonDeliveryEvidence(rejectionReason, message);
            case RETRIEVAL -> createRetrievalEvidence(message);
            case NON_RETRIEVAL -> createNonRetrievalEvidence(rejectionReason, message);
            case RELAY_REMMD_ACCEPTANCE -> createRelayRemmdAcceptance(message);
            case RELAY_REMMD_REJECTION -> createRelayRemmdRejection(rejectionReason, message);
            case RELAY_REMMD_FAILURE -> createRelayRemmdFailure(rejectionReason, message);
        };
    }

    private byte[] createSubmissionAcceptance(ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        String hash = checkPayloadAndBuildHashHex(message);
        return createSubmissionAcceptanceRejection(true, null, message, hash);
    }

    private byte[] createSubmissionRejection(
            ConnectorMessageRejectionReason rejectionReason,
            ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        requireRejectionReason(rejectionReason);
        var event = mapRejectionReason(rejectionReason);
        String hash = checkPayloadAndBuildHashHex(message);
        return createSubmissionAcceptanceRejection(false, event, message, hash);
    }

    private EventReasonType mapRejectionReason(ConnectorMessageRejectionReason rejectionReason) {
        var event = new EventReasonType();
        event.setCode(rejectionReason.getErrorCode());
        var details = StringUtils.isNotBlank(rejectionReason.getReason())
                ? rejectionReason.getReason()
                : rejectionReason.name();
        event.setDetails(details);
        return event;
    }

    private byte[] requirePriorEvidence(
            ConnectorEvidenceType requiredType,
            ConnectorMessage message) {
        byte[] prev = findPriorEvidence(requiredType, message);
        if (prev == null) {
            throw new ConnectorEvidenceException("prior evidence content is required");
        }
        return prev;
    }

    private byte[] createRelayRemmdAcceptance(ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        return evidenceBuilder.createRelayREMMDAcceptanceRejection(
                true,
                null,
                buildEDeliveryDetails(),
                requirePriorEvidence(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message
                )
        );
    }

    private byte[] createRelayRemmdRejection(
            ConnectorMessageRejectionReason rejectionReason,
            ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        requireRejectionReason(rejectionReason);
        return evidenceBuilder.createRelayREMMDAcceptanceRejection(
                false,
                mapRejectionReason(rejectionReason),
                buildEDeliveryDetails(),
                requirePriorEvidence(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message)
        );
    }

    private byte[] createDeliveryEvidence(ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        return evidenceBuilder.createDeliveryNonDeliveryToRecipient(
                true,
                null,
                buildEDeliveryDetails(),
                requirePriorEvidence(
                        ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                        message
                )
        );
    }

    private byte[] createNonDeliveryEvidence(
            ConnectorMessageRejectionReason rejectionReason,
            ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        requireRejectionReason(rejectionReason);
        return evidenceBuilder.createDeliveryNonDeliveryToRecipient(
                false,
                mapRejectionReason(rejectionReason),
                buildEDeliveryDetails(),
                requirePriorEvidence(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, message)
        );
    }

    private byte[] createRetrievalEvidence(ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        return evidenceBuilder.createRetrievalNonRetrievalByRecipient(
                true,
                null,
                buildEDeliveryDetails(),
                requirePriorEvidence(
                        ConnectorEvidenceType.DELIVERY,
                        message
                )
        );
    }

    private byte[] createNonRetrievalEvidence(
            ConnectorMessageRejectionReason rejectionReason,
            ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        requireRejectionReason(rejectionReason);
        return evidenceBuilder.createRetrievalNonRetrievalByRecipient(
                false,
                mapRejectionReason(rejectionReason),
                buildEDeliveryDetails(),
                requirePriorEvidence(ConnectorEvidenceType.DELIVERY, message)
        );
    }

    private byte[] createRelayRemmdFailure(
            ConnectorMessageRejectionReason rejectionReason,
            ConnectorMessage message)
            throws ConnectorEvidenceBuilderException {
        requireRejectionReason(rejectionReason);
        return evidenceBuilder.createRelayREMMDFailure(
                mapRejectionReason(rejectionReason),
                buildEDeliveryDetails(),
                requirePriorEvidence(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message
                )
        );
    }

    private byte[] createSubmissionAcceptanceRejection(
            boolean isAcceptance,
            EventReasonType eventReason,
            ConnectorMessage message,
            String hashHex)
            throws ConnectorEvidenceBuilderException {
        var issuerDetails = buildEDeliveryDetails();
        var messageDetails = buildMessageDetails(message, hashHex);
        return evidenceBuilder.createSubmissionAcceptanceRejection(
                isAcceptance,
                eventReason,
                issuerDetails,
                messageDetails
        );
    }

    private void requireRejectionReason(ConnectorMessageRejectionReason rejectionReason) {
        if (rejectionReason == null) {
            throw new ConnectorEvidenceException("RejectionReason may not be null");
        }
    }

    private String checkPayloadAndBuildHashHex(ConnectorMessage message) {
        var businessContent = message.businessContent();

        if (businessContent == null || businessContent.xmlContent() == null) {
            return null;
        }

        var xmlAttachment = businessContent.xmlContent();
        var id = xmlAttachment.identifier();

        if (id == null || id.isBlank()) {
            return null;
        }

        try {
            byte[] xmlBytes = fileStorageProvider.findByIdentifier(id);
            if (xmlBytes == null || xmlBytes.length == 0) {
                return null;
            }

            return hashValueBuilder.buildHashValueAsString(xmlBytes);
        } catch (Exception e) {
            throw new ConnectorEvidenceException("could not build payload hash for evidence", e);
        }
    }

    private byte[] findPriorEvidence(ConnectorEvidenceType requiredType, ConnectorMessage message) {
        var evidences = message.evidences();

        if (evidences == null) {
            throw new ConnectorEvidenceException(missingPredecessorMessage(requiredType, message));
        }

        return evidences.stream()
                        .filter(e -> e.type() == requiredType && e.content() != null)
                        .map(ConnectorMessageEvidence::content)
                        .findFirst()
                        .orElseThrow(() -> new ConnectorEvidenceException(missingPredecessorMessage(
                                requiredType,
                                message
                        )));
    }

    private String missingPredecessorMessage(
            ConnectorEvidenceType requiredType,
            ConnectorMessage message) {
        return "message [%s] has no prior evidence of type [%s] required for the next step"
                .formatted(message.identifier(), requiredType);
    }

    private EDeliveryDetails buildEDeliveryDetails() {
        var detail = new EDeliveryDetail();
        var home = evidencesProperties.getIssuer().getAs4Party();

        var server = new EDeliveryDetail.Server();
        server.setGatewayName(home.getName());
        server.setGatewayAddress(home.getEndpointAddress());
        detail.setServer(server);

        var postal = evidencesProperties.getIssuer().getPostalAddress();
        var postalAddress = new EDeliveryDetail.PostalAdress();
        postalAddress.setStreetAddress(postal.getStreet());
        postalAddress.setLocality(postal.getLocality());
        postalAddress.setPostalCode(postal.getZipCode());
        postalAddress.setCountry(postal.getCountry());
        detail.setPostalAdress(postalAddress);

        return new EDeliveryDetails(detail);
    }

    private ConnectorEvidenceMessageDetails buildMessageDetails(
            ConnectorMessage message,
            String hashHex) {
        var messageDetails = new ConnectorEvidenceMessageDetails();
        messageDetails.setHashAlgorithm(hashValueBuilder.getAlgorithm());
        if (hashHex != null) {
            messageDetails.setHashValue(HexFormat.of().parseHex(hashHex));
        }

        String nationalMessageId = message.backendMessageIdentifier();
        if (nationalMessageId == null || nationalMessageId.isBlank()) {
            throw new ConnectorEvidenceException(
                    "nationalMessageId (backendMessageIdentifier) may not be blank for submission "
                            + "evidence"
            );
        }

        String senderAddress = message.as4Properties().originalSender();
        String recipientAddress = message.as4Properties().finalRecipient();

        if (recipientAddress == null || recipientAddress.isBlank()) {
            throw new ConnectorEvidenceException(
                    "finalRecipient may not be blank when building submission evidence");
        }
        if (senderAddress == null || senderAddress.isBlank()) {
            throw new ConnectorEvidenceException(
                    "originalSender may not be blank when building submission evidence");
        }

        messageDetails.setNationalMessageId(nationalMessageId);
        messageDetails.setRecipientAddress(recipientAddress);
        messageDetails.setSenderAddress(senderAddress);
        String ebms = message.as4Properties().ebmsMessageIdentifier();
        messageDetails.setEbmsMessageId(ebms);

        return messageDetails;
    }
}
