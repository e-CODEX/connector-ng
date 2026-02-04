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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a processing step in the connector message workflow for handling the acceptance
 * creation of inbound messages. This step generates relay REMMD acceptance evidence for inbound
 * messages, associates it with the original message, processes the evidence, and creates a new
 * evidence message to confirm acceptance. The evidence message's direction is switched as part of
 * the processing.
 *
 * <p>The processing step uses the {@link ConnectorMessageService} and
 * {@link ConnectorEvidenceService} to manage messages and evidence, ensuring compliance with
 * business and technical requirements.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessageAcceptanceStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorEvidenceService evidenceService;

    /**
     * Constructs an instance of {@code ConnectorInboundMessageAcceptanceStep}.
     *
     * @param messageService  the service responsible for managing and persisting connector
     *                        messages. It provides methods for adding evidence to messages,
     *                        creating evidence messages, and switching message directions.
     * @param evidenceService the service responsible for creating and processing evidence related
     *                        to connector messages. It is used to generate acceptance evidence and
     *                        manage related business logic.
     */
    public ConnectorInboundMessageAcceptanceStep(
            ConnectorMessageService messageService, ConnectorEvidenceService evidenceService) {
        this.messageService = messageService;
        this.evidenceService = evidenceService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug("processing inbound message acceptance creation for: [{}]", inboundMessage);

        var relayREMMDEvidence = this.evidenceService.createSuccess(
                ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, inboundMessage
        );

        var messageWithEvidence = this.messageService.addEvidence(
                inboundMessage, relayREMMDEvidence
        );
        this.evidenceService.processMessage(relayREMMDEvidence.type(), messageWithEvidence);
        var evidenceMessage = this.messageService.createEvidenceMessage(
                messageWithEvidence, relayREMMDEvidence
        );

        log.debug("created message with relay REMMD acceptance evidence: [{}]", evidenceMessage);

        return this.messageService.switchDirection(evidenceMessage);
    }
}
