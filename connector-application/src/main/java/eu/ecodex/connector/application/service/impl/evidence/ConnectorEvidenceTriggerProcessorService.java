/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.evidence;

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.usecase.evidence.ConnectorEvidenceTriggerProcessor;
import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.util.ConnectorBusinessDomainUtil;
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
public class ConnectorEvidenceTriggerProcessorService implements ConnectorEvidenceTriggerProcessor {
    private final ConnectorMessageService messageService;
    private final ConnectorEvidenceService evidenceService;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;
    private final ConnectorLinkSubmitter linkSubmitter;
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    public ConnectorEvidenceTriggerProcessorService(
            ConnectorMessageService messageService,
            ConnectorEvidenceService evidenceService,
            ConnectorMessageRepository messageRepository,
            ConnectorMessageEvidenceCreator evidenceCreator,
            ConnectorMessageEvidenceVerifier evidenceVerifier,
            ConnectorEvidenceMessageCreator evidenceMessageCreator,
            ConnectorLinkSubmitter linkSubmitter,
            ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.messageService = messageService;
        this.evidenceService = evidenceService;
        this.messageRepository = messageRepository;
        this.evidenceCreator = evidenceCreator;
        this.evidenceVerifier = evidenceVerifier;
        this.evidenceMessageCreator = evidenceMessageCreator;
        this.linkSubmitter = linkSubmitter;
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    @Override
    @Transactional
    public void process(@NonNull ConnectorMessage triggerMessage) {
        log.info("Processing evidence trigger message [{}]", triggerMessage.identifier());

        try {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(
                    triggerMessage.businessDomainIdentifier()
            );

            if (!messageService.isEvidenceTriggerMessage(triggerMessage)) {
                throw new ConnectorEvidenceException("the message is not an evidence trigger message");
            }
            evidenceService.isEvidenceTriggeringAllowed(triggerMessage);

            var triggerEvidence = triggerMessage.transportedEvidences().getFirst();
            var evidenceType = triggerEvidence.type();

            var businessMessage = findReferencedBusinessMessage(triggerMessage);
            if (businessMessage.direction() != ConnectorMessageDirection.GATEWAY_TO_BACKEND) {
                throw new ConnectorEvidenceException(
                        "evidence trigger is only supported for gateway-to-backend business messages"
                );
            }

            var createdEvidence = evidenceCreator.createSuccess(evidenceType, businessMessage);

            applyEvidenceToBusinessMessage(businessMessage, createdEvidence);

            var evidenceForGateway = evidenceMessageCreator.createForTrigger(
                    businessMessage, createdEvidence, triggerMessage
            );

            linkSubmitter.submit(evidenceForGateway);

            if (processingConfigurationProvider.getConfiguration().sendGeneratedEvidencesToBackend()) {
                linkSubmitter.submit(evidenceForGateway.switchDirection());
            }

            log.info(
                    "Successfully processed evidence trigger [{}] for business message [{}]",
                    evidenceType,
                    businessMessage.identifier()
            );
        } finally {
            ConnectorBusinessDomainUtil.setCurrentBusinessDomain(null);
        }
    }

    private ConnectorMessage findReferencedBusinessMessage(ConnectorMessage triggerMessage) {
        var referenceToMessageId = triggerMessage.as4Properties().referenceToIdentifier();
        if (!StringUtils.hasText(referenceToMessageId)) {
            referenceToMessageId = triggerMessage.referenceToBackendMessageIdentifier();
        }
        if (!StringUtils.hasText(referenceToMessageId)) {
            throw new ConnectorEvidenceException(
                    "evidence trigger must set refToMessageId to the referenced business message"
            );
        }

        var byEbms = messageRepository.findByEbmsMessageIdentifier(referenceToMessageId);
        if (byEbms != null) {
            return byEbms;
        }

        var byBackendId = messageRepository.findByBackendMessageIdentifier(referenceToMessageId);
        if (byBackendId != null) {
            return byBackendId;
        }

        var byIdentifier = messageRepository.findByIdentifier(referenceToMessageId);
        if (byIdentifier != null) {
            return byIdentifier;
        }

        throw new ConnectorMessageNotFoundException(
                "referenced business message not found for ref [" + referenceToMessageId + "]"
        );
    }

    private void applyEvidenceToBusinessMessage(
            ConnectorMessage businessMessage,
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
