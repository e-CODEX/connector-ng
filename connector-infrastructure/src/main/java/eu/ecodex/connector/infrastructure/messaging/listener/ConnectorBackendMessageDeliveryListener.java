/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener;

import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.transition.DomibusConnectorActionType;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDocumentAESType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import eu.ecodex.connector.infrastructure.outbound.soap.BackendServiceClient;
import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class ConnectorBackendMessageDeliveryListener implements ConnectorEventHandler {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final BackendServiceClient backendServiceClient;

    public ConnectorBackendMessageDeliveryListener(
            ConnectorMessageRepository messageRepository,
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider fileStorageProvider,
            BackendServiceClient backendServiceClient) {
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.backendServiceClient = backendServiceClient;
    }

    @Transactional
    @JmsListener(destination = "${connector.queues.backend-delivery-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        var identifier = message.identifier();

        if (identifier == null) {
            throw new IllegalArgumentException("Message identifier cannot be null");
        }

        // TODO handle pull mode

        var inboundMessage = this.messageRepository.findByIdentifier(message.identifier());

        log.info("Submitting message [{}] to the backend system", identifier);

        var deliveryWebService = backendServiceClient.createClient(inboundMessage.backendName());

        try {
            var backendMessage = convertMessage(inboundMessage);

            var acknowledgment = deliveryWebService.deliverMessage(backendMessage);

            if (acknowledgment.isResult()) {
                messageRepository.setDeliveredToBackendAt(identifier);
            } else {
                log.error("Failed to deliver message [{}] to the backend system", identifier);
                messageRepository.setAsRejected(identifier);
            }
        } catch (Exception e) {
            log.error("Failed to deliver message [{}] to the backend system", identifier, e);
        }
    }

    private DomibusConnectorMessageType convertMessage(ConnectorMessage message) {
        var details = convertDetails(message);
        var messageType = new DomibusConnectorMessageType();
        messageType.setMessageDetails(details);

        if (message.businessContent() != null) {
            messageType.setMessageContent(toContent(message.businessContent()));
        }

        messageType.getMessageAttachments().addAll(
                toAttachments(message.identifier())
        );

        messageType.getMessageConfirmations().addAll(
                toConfirmations(message)
        );

        return messageType;
    }

    private DomibusConnectorMessageDetailsType convertDetails(ConnectorMessage message) {
        var as4Properties = message.as4Properties();

        var details = new DomibusConnectorMessageDetailsType();
        details.setBackendMessageId(message.backendMessageIdentifier());
        details.setConversationId(as4Properties.conversationIdentifier());
        details.setEbmsMessageId(as4Properties.ebmsMessageIdentifier());
        details.setRefToMessageId(as4Properties.referenceToIdentifier());
        details.setOriginalSender(as4Properties.originalSender());
        details.setFinalRecipient(as4Properties.finalRecipient());
        details.setService(toService(as4Properties));
        details.setAction(toAction(as4Properties));
        details.setFromParty(getParty(as4Properties.fromParty()));
        details.setToParty(getParty(as4Properties.toParty()));

        if (message.isEvidenceMessage()) {
            details.setBackendMessageId(message.referenceToBackendMessageIdentifier());

            if (message.direction().getTarget() == ConnectorMessageDirectionType.BACKEND
                    && StringUtils.hasLength(message.referenceToBackendMessageIdentifier())) {
                details.setRefToMessageId(message.referenceToBackendMessageIdentifier());
            }
        }

        return details;
    }

    private DomibusConnectorServiceType toService(ConnectorMessageAS4Properties as4Properties) {
        if (as4Properties.service() == null) {
            throw new IllegalStateException("Service is null");
        }

        var service = new DomibusConnectorServiceType();
        service.setService(as4Properties.service().name());
        service.setServiceType(as4Properties.service().type());

        return service;
    }

    private DomibusConnectorActionType toAction(ConnectorMessageAS4Properties as4Properties) {
        if (as4Properties.action() == null) {
            throw new IllegalStateException();
        }

        if (as4Properties.action().name() == null) {
            throw new IllegalStateException();
        }

        var action = new DomibusConnectorActionType();
        action.setAction(as4Properties.action().name());

        return action;
    }

    private DomibusConnectorPartyType getParty(ConnectorParty party) {
        if (party == null) {
            throw new IllegalStateException("Party cannot be null");
        }

        var part = new DomibusConnectorPartyType();
        part.setPartyId(party.identifier());
        part.setPartyIdType(party.identifierType());
        part.setRole(party.role());

        return part;
    }

    private DomibusConnectorMessageContentType toContent(ConnectorMessageBusinessContent content) {
        if (content == null) {
            return null;
        }

        var data = this.fileStorageProvider.findByIdentifier(content.xmlContent().identifier());
        var contentType = new DomibusConnectorMessageContentType();
        contentType.setXmlContent(
                new StreamSource(
                        new ByteArrayInputStream(Arrays.copyOf(data, data.length))
                )
        );
        contentType.setDocument(toDocument(content.businessDocument()));

        return contentType;
    }

    private DomibusConnectorMessageDocumentType toDocument(
            ConnectorMessageBusinessDocument document) {
        if (document == null) {
            return null;
        }

        var data = this.fileStorageProvider.findByIdentifier(document.attachment().identifier());

        var documentType = new DomibusConnectorMessageDocumentType();
        documentType.setDocumentName(document.attachment().name());
        documentType.setDocument(
                new DataHandler(
                        new ByteArrayDataSource(
                                Arrays.copyOf(data, data.length),
                                document.attachment().contentType()
                        )
                )
        );

        if (document.aesType() != null) {
            documentType.setAesType(
                    DomibusConnectorDocumentAESType.fromValue(document.aesType().name())
            );
        }

        if (document.detachedSignature() != null) {
            var detachedSignature = new DomibusConnectorDetachedSignatureType();
            detachedSignature.setMimeType(
                    DomibusConnectorDetachedSignatureMimeType.fromValue(document.detachedSignature().mimeType().name())
            );
            detachedSignature.setDetachedSignatureName(document.detachedSignature().name());
            detachedSignature.setDetachedSignature(document.detachedSignature().signature());
            documentType.setDetachedSignature(detachedSignature);
        }

        return documentType;
    }

    private List<DomibusConnectorMessageAttachmentType> toAttachments(String messageIdentifier) {
        var attachments = this.attachmentRepository.findByMessageIdentifierAndTypes(
                messageIdentifier,
                List.of(
                        ConnectorAttachmentType.ATTACHMENT,
                        ConnectorAttachmentType.PDF_TOKEN,
                        ConnectorAttachmentType.XML_TOKEN
                )
        );

        return attachments.stream().map((attachment) -> {
            var data = this.fileStorageProvider.findByIdentifier(attachment.identifier());
            var attachmentType = new DomibusConnectorMessageAttachmentType();
            attachmentType.setIdentifier(attachment.identifier());
            attachmentType.setName(attachment.name());
            attachmentType.setMimeType(attachment.contentType());
            attachmentType.setDescription(attachment.description());
            attachmentType.setAttachment(
                    new DataHandler(
                            new ByteArrayDataSource(
                                    Arrays.copyOf(data, data.length), attachment.contentType()
                            )
                    )
            );

            return attachmentType;
        }).toList();
    }

    private List<DomibusConnectorMessageConfirmationType> toConfirmations(
            ConnectorMessage message) {
        if (message.evidences() == null || message.evidences().isEmpty()) {
            return new ArrayList<>();
        }

        return message.evidences().stream().map(evidence -> {
            if (evidence.attachment() == null) {
                throw new IllegalStateException(
                        "Evidence attachment is null for evidence " + evidence.type()
                );
            }

            var data = this.fileStorageProvider.findByIdentifier(evidence.attachment().identifier());

            var confirmation = new DomibusConnectorMessageConfirmationType();
            confirmation.setConfirmationType(
                    DomibusConnectorConfirmationType.fromValue(evidence.type().name())
            );
            confirmation.setConfirmation(
                    new StreamSource(
                            new ByteArrayInputStream(Arrays.copyOf(data, data.length))
                    )
            );

            return confirmation;
        }).toList();
    }
}
