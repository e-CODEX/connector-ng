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

import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for receiving outbound evidence messages.
 */
@Service
@Transactional
public class ConnectorOutboundEvidenceMessageReceiverService
    implements ConnectorOutboundEvidenceMessageReceiver {
    private final ConnectorMessageIdGenerator messageIdGeneratorService;
    private final ConnectorTriggeredEvidenceMessageVerifier triggeredEvidenceMessageVerifier;
    private final ConnectorMessageEventPublisher<ConnectorTriggeredEvidenceMessage>
        evidenceTriggerPublisher;

    /**
     * Constructor for the ConnectorOutboundEvidenceMessageReceiverService.
     *
     * @param messageIdGeneratorService        the service for generating message IDs
     * @param triggeredEvidenceMessageVerifier the service for verifying triggered evidence
     *                                         messages
     * @param evidenceTriggerPublisher         the publisher for evidence trigger messages
     */
    public ConnectorOutboundEvidenceMessageReceiverService(
        ConnectorMessageIdGenerator messageIdGeneratorService,
        ConnectorTriggeredEvidenceMessageVerifier triggeredEvidenceMessageVerifier,
        @Qualifier("connectorJmsOutboundEvidenceTriggerPublisher")
        ConnectorMessageEventPublisher<ConnectorTriggeredEvidenceMessage> evidenceTriggerPublisher
    ) {
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.triggeredEvidenceMessageVerifier = triggeredEvidenceMessageVerifier;
        this.evidenceTriggerPublisher = evidenceTriggerPublisher;
    }

    @Override
    public ConnectorTriggeredEvidenceMessage execute(
        @NonNull ConnectorOutboundEvidenceMessageCommand command) {
        var triggeredEvidenceMessage = ConnectorTriggeredEvidenceMessage
            .builder()
            .identifier(messageIdGeneratorService.execute())
            .backendName(command.backendName())
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .evidenceType(command.evidenceType())
            .referenceToIdentifier(command.referenceToMessageIdentifier())
            .build();

        triggeredEvidenceMessageVerifier.verify(triggeredEvidenceMessage);

        evidenceTriggerPublisher.publish(triggeredEvidenceMessage);

        return triggeredEvidenceMessage;
    }
}
