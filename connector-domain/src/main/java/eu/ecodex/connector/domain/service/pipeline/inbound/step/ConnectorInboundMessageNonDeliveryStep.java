/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a processing step in the connector message workflow that handles the
 * creation of non-delivery evidence for inbound messages.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessageNonDeliveryStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorEvidenceService evidenceService;

    /**
     * Constructs a new instance of the {@code ConnectorInboundMessageNonDeliveryStep} class.
     *
     * @param messageService  the service responsible for managing and persisting connector
     *                        messages, including adding evidences and switching message
     *                        directions.
     * @param evidenceService the service responsible for managing evidences related to messages,
     *                        including creating failure evidence and processing message evidences.
     */
    public ConnectorInboundMessageNonDeliveryStep(
            ConnectorMessageService messageService, ConnectorEvidenceService evidenceService) {
        this.messageService = messageService;
        this.evidenceService = evidenceService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug("processing inbound message non delivery creation for: [{}]", inboundMessage);

        var nonDeliveryEvidence = this.evidenceService.createFailure(
                ConnectorEvidenceType.NON_DELIVERY, inboundMessage,
                ConnectorMessageRejectionReason.OTHER
        );

        var messageWithEvidence = this.messageService.addEvidence(
                inboundMessage, nonDeliveryEvidence
        );
        this.evidenceService.processMessage(nonDeliveryEvidence.type(), messageWithEvidence);
        var evidenceMessage = this.messageService.createEvidenceMessage(
                messageWithEvidence, nonDeliveryEvidence
        );

        log.debug("created message with non delivery evidence: [{}]", evidenceMessage);

        return this.messageService.switchDirection(evidenceMessage);
    }
}
