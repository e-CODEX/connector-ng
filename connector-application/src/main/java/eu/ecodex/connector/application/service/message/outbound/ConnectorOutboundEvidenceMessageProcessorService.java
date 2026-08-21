/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageProcessor;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.ArrayList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation aligned with legacy {@code EvidenceMessageProcessor} and
 * {@code EvidenceTriggerStep}.
 */
@Slf4j
@Service
@Transactional
public class ConnectorOutboundEvidenceMessageProcessorService
    implements ConnectorOutboundEvidenceMessageProcessor {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;
    private final ConnectorLinkSubmitter linkSubmitter;
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    /**
     * Creates the processor with message, evidence, and link submission dependencies.
     *
     * @param messageRepository               persisted connector messages
     * @param evidenceCreator                 creates evidence records
     * @param evidenceVerifier                validates evidence applicability
     * @param evidenceMessageCreator          builds evidence messages for transport
     * @param linkSubmitter                   forwards messages to partners
     * @param processingConfigurationProvider message processing configuration
     */
    public ConnectorOutboundEvidenceMessageProcessorService(
        ConnectorMessageRepository messageRepository,
        ConnectorMessageEvidenceCreator evidenceCreator,
        ConnectorMessageEvidenceVerifier evidenceVerifier,
        ConnectorEvidenceMessageCreator evidenceMessageCreator,
        ConnectorLinkSubmitter linkSubmitter,
        ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.messageRepository = messageRepository;
        this.evidenceCreator = evidenceCreator;
        this.evidenceVerifier = evidenceVerifier;
        this.evidenceMessageCreator = evidenceMessageCreator;
        this.linkSubmitter = linkSubmitter;
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    @Override
    public void execute(@NonNull ConnectorTriggeredEvidenceMessage triggeredEvidenceMessage) {
        log.info(
            "Processing outbound evidence trigger message [{}]",
            triggeredEvidenceMessage.identifier()
        );

        try {
            var evidenceType = triggeredEvidenceMessage.evidenceType();

            var businessMessage = findReferencedBusinessMessage(triggeredEvidenceMessage);

            if (businessMessage.direction() != ConnectorMessageDirection.GATEWAY_TO_BACKEND) {
                throw new ConnectorEvidenceException(
                    "Evidence trigger is only supported for "
                        + "gateway-to-backend business messages"
                );
            }

            // TODO check the type of evidence to be generated (success or failure)
            var createdEvidence = evidenceCreator.createSuccess(evidenceType, businessMessage);

            applyEvidenceToBusinessMessage(businessMessage, createdEvidence);

            var evidenceForGatewayMessage = evidenceMessageCreator.createForTrigger(
                businessMessage, createdEvidence, triggeredEvidenceMessage
            );

            linkSubmitter.submit(evidenceForGatewayMessage);

            if (processingConfigurationProvider.getConfiguration()
                                               .sendGeneratedEvidencesToBackend()) {
                log.debug("Sending trigger created evidence message back to the backend");
                linkSubmitter.submit(evidenceForGatewayMessage.switchDirection());
            }

            log.info(
                "Successfully processed evidence trigger [{}] for business message [{}]",
                evidenceType,
                businessMessage.identifier()
            );
        } catch (Exception e) {
            log.error("Failed to process evidence trigger [{}]", triggeredEvidenceMessage, e);
            throw e;
        }
    }

    private ConnectorBusinessMessage findReferencedBusinessMessage(
        ConnectorTriggeredEvidenceMessage triggerMessage) {
        var referenceToMessageId = triggerMessage.referenceToIdentifier();

        if (!StringUtils.hasText(referenceToMessageId)) {
            referenceToMessageId = triggerMessage.referenceToBackendMessageIdentifier();
        }

        if (!StringUtils.hasText(referenceToMessageId)) {
            throw new ConnectorEvidenceException(
                "Evidence trigger must set refToMessageId to the referenced business message"
            );
        }

        var referencedBusinessMessage = messageRepository.findReferencedBusinessMessage(
            referenceToMessageId,
            triggerMessage.direction()
        );

        if (referencedBusinessMessage != null) {
            return referencedBusinessMessage;
        }

        throw new ConnectorMessageNotFoundException(
            "Referenced business message not found for ref [" + referenceToMessageId + "]"
        );
    }

    private void applyEvidenceToBusinessMessage(
        ConnectorBusinessMessage businessMessage,
        ConnectorMessageEvidence createdEvidence) {

        var reloaded = messageRepository.findByIdentifier(businessMessage.identifier());
        var accumulated = reloaded.evidences() != null
                          ? new ArrayList<>(reloaded.evidences())
                          : new ArrayList<ConnectorMessageEvidence>();
        accumulated.add(createdEvidence);

        var messageForVerification = reloaded.toBuilder()
                                             .transportedEvidences(accumulated)
                                             .build();

        try {
            evidenceVerifier.verify(createdEvidence.type(), messageForVerification);
        } catch (ConnectorEvidenceNotRelevantException e) {
            log.info(
                "Evidence [{}] ignored for business message [{}]: {}",
                createdEvidence.type(),
                businessMessage.identifier(),
                e.getMessage()
            );
        }
    }
}
