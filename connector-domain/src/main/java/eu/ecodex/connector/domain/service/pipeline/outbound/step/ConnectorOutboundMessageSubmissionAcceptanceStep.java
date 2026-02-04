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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * ConnectorOutboundMessageSubmissionAcceptanceStep is a processing step in the connector
 * message workflow that is responsible for creating submission acceptance evidences for outbound
 * messages. It performs the following functions:
 *
 * <ul>
 *     <li> Generates success evidence of type SUBMISSION_ACCEPTANCE for a given outbound message.
 *     <li> Attaches the generated evidence to the message.
 *     <li> Processes the updated message and evidence through the evidence service to trigger
 *     further handling or actions.
 * </ul>
 *
 * <p>This class is designed to ensure evidences are systematically created and integrated into the
 * overall message lifecycle.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li> Uses the {@link ConnectorEvidenceService} to create and process evidences.
 *     <li> Employs the {@link ConnectorMessageService} to modify and persist message data after
 *     evidence addition.
 * </ul>
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageSubmissionAcceptanceStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;
    private final ConnectorEvidenceService evidenceService;

    /**
     * Constructs a new instance of
     * {@code ConnectorOutboundMessageSubmissionAcceptanceStep}.
     *
     * @param messageService the service used for updating and persisting messages after
     *                       evidence creation.
     *
     * @param evidenceService the service used for creating and processing evidences of outbound
     *                        messages.
     */
    public ConnectorOutboundMessageSubmissionAcceptanceStep(
            ConnectorMessageService messageService,
            ConnectorEvidenceService evidenceService
            ) {
        this.evidenceService = evidenceService;
        this.messageService = messageService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug(
                "processing outbound message with submission acceptance evidence for: [{}]",
                outboundMessage
        );

        var submissionEvidence = this.evidenceService.createSuccess(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, outboundMessage
        );
        //  add submission evidence to the message
        var messageWithSubmissionEvidence = this.messageService.addEvidence(
                outboundMessage, submissionEvidence
        );
        this.evidenceService.processMessage(
                submissionEvidence.type(), messageWithSubmissionEvidence
        );

        log.debug(
                "created message with submission acceptance evidence: [{}]",
                messageWithSubmissionEvidence
        );

        return messageWithSubmissionEvidence;
    }
}
