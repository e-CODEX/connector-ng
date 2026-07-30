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

import eu.ecodex.connector.application.exception.ConnectorMessageException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorVerifyTriggeredEvidence;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorOutboundMessageReceiver} service.
 */
@Slf4j
@Service
public class ConnectorOutboundMessageReceiverService implements ConnectorOutboundMessageReceiver {
    private final ConnectorMessageProcessingConfigurationProvider configurationProvider;
    private final ConnectorMessageVerifier messageVerifierService;
    private final ConnectorEventPublisher stagingEventPublisher;
    private final ConnectorEventPublisher evidenceTriggerPublisher;
    private final ConnectorMessageIdGenerator messageIdGeneratorService;
    private final ConnectorBusinessDomainVerifier businessDomainVerifierService;
    private final ConnectorVerifyTriggeredEvidence verifyTriggeredEvidenceService;

    /**
     * Constructs a new {@code ConnectorOutboundMessageReceiverService}.
     *
     * @param configurationProvider          provider of the current
     *                                       {@link ConnectorMessageProcessingConfiguration}
     * @param messageVerifierService         verifier used to validate outbound messages
     * @param stagingEventPublisher          event publisher used to stage messages for further
     *                                       processing; qualified as
     *                                       "connectorOutboundMessageStagingEventPublisher"
     * @param messageIdGeneratorService      generator used to assign unique identifiers to outbound
     *                                       messages param businessDomainVerifier verifier used to
     *                                       validate business domains of outbound messages
     * @param businessDomainVerifierService  verifier used to validate business domains of outbound
     *                                       messages
     * @param verifyTriggeredEvidenceService service used to verify triggered evidence messages
     */
    public ConnectorOutboundMessageReceiverService(
        ConnectorMessageProcessingConfigurationProvider configurationProvider,
        ConnectorMessageVerifier messageVerifierService,
        @Qualifier("connectorJmsOutboundMessageStagingPublisher")
        ConnectorEventPublisher stagingEventPublisher,
        @Qualifier("connectorJmsOutboundEvidenceTriggerPublisher")
        ConnectorEventPublisher evidenceTriggerPublisher,
        ConnectorMessageIdGenerator messageIdGeneratorService,
        ConnectorBusinessDomainVerifier businessDomainVerifierService,
        ConnectorVerifyTriggeredEvidence verifyTriggeredEvidenceService) {
        this.configurationProvider = configurationProvider;
        this.messageVerifierService = messageVerifierService;
        this.stagingEventPublisher = stagingEventPublisher;
        this.evidenceTriggerPublisher = evidenceTriggerPublisher;
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.businessDomainVerifierService = businessDomainVerifierService;
        this.verifyTriggeredEvidenceService = verifyTriggeredEvidenceService;
    }

    @Override
    @Transactional
    public ConnectorMessage execute(@NonNull final ConnectorMessage message) {
        var messageWithId = this.assignIdentifier(message);

        if (messageWithId.isBusinessMessage()) {
            if (message.businessDomainIdentifier() == null) {
                throw new IllegalStateException(
                    "The message does not contain a business domain identifier. "
                        + "It will therefore be rejected."
                );
            }
            businessDomainVerifierService.execute(message.businessDomainIdentifier());
            var configuration = this.configurationProvider.getConfiguration();
            this.messageVerifierService.verify(
                messageWithId, configuration.outboundMessageVerificationMode());
            stagingEventPublisher.publish(messageWithId);
        } else if (messageWithId.isEvidenceTriggerMessage()) {
            verifyTriggeredEvidenceService.verify(messageWithId);
            evidenceTriggerPublisher.publish(messageWithId);
        } else {
            throw new ConnectorMessageException(
                "The message is neither business message nor an evidence message. "
                    + "It will therefore be rejected."
            );
        }

        return messageWithId;
    }

    private ConnectorMessage assignIdentifier(ConnectorMessage message) {
        log.debug("Assigning identifier to message [{}]", message.identifier());
        var identifier = this.messageIdGeneratorService.generateIdentifier();

        return message.toBuilder().identifier(identifier).build();
    }
}
