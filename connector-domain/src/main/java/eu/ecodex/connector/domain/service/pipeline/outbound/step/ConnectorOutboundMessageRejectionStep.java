/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound.step;

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
 * Represents a processing step responsible for handling outbound message rejection creation in the
 * connector message workflow. This class generates rejection-related evidence and transforms
 * outbound messages to include that evidence, ensuring proper handling of message rejection
 * scenarios.
 *
 * <p>Key responsibilities of this class include:
 * <ul>
 *     <li> Generating failure evidence of type {@link ConnectorEvidenceType#SUBMISSION_REJECTION}
 *     with a specified rejection reason.
 *     <li> Creating a new evidence message based on the generated evidence and the original
 *     outgoing message.
 *     <li> Switching the direction of the resulting rejection message to indicate its updated
 *     state.
 * </ul>
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageRejectionStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorEvidenceService evidenceService;

    /**
     * Constructs a new instance of {@code ConnectorOutboundMessageRejectionStep}.
     *
     * @param messageService  the {@link ConnectorMessageService} instance responsible for managing
     *                        and persisting connector messages, including handling direction
     *                        changes for rejection messages.
     * @param evidenceService the {@link ConnectorEvidenceService} instance responsible for creating
     *                        evidences, such as failure evidence for rejected messages, to be
     *                        associated with the connector messages.
     */
    public ConnectorOutboundMessageRejectionStep(
            ConnectorMessageService messageService, ConnectorEvidenceService evidenceService) {
        this.messageService = messageService;
        this.evidenceService = evidenceService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug("processing outbound message rejection creation for: [{}]", outboundMessage);

        var rejectionEvidence = this.evidenceService.createFailure(
                ConnectorEvidenceType.SUBMISSION_REJECTION,
                outboundMessage,
                ConnectorMessageRejectionReason.OTHER
        );
        var rejectionMessage = this.messageService.createEvidenceMessage(
                outboundMessage, rejectionEvidence
        );

        log.debug("created message with rejection evidence: [{}]", rejectionMessage);

        return this.messageService.switchDirection(rejectionMessage);
    }
}
