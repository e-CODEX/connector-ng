/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Component
public class ConnectorOutboundMessageRejectionStep
    implements ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> {
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;

    /**
     * Creates a new rejection processing step.
     *
     * @param evidenceMessageCreator component responsible for creating connector evidence messages
     * @param evidenceCreator        component responsible for creating evidence objects describing
     *                               message processing outcomes
     * @param evidenceVerifier       component responsible for validating that the message meets the
     *                               requirements for the generated evidence type
     */
    public ConnectorOutboundMessageRejectionStep(
        ConnectorEvidenceMessageCreator evidenceMessageCreator,
        ConnectorMessageEvidenceCreator evidenceCreator,
        ConnectorMessageEvidenceVerifier evidenceVerifier) {
        this.evidenceMessageCreator = evidenceMessageCreator;
        this.evidenceCreator = evidenceCreator;
        this.evidenceVerifier = evidenceVerifier;
    }

    @Override
    public ConnectorEvidenceMessage execute(@NonNull ConnectorBusinessMessage outboundMessage) {
        log.debug(
            "Processing outbound message [{}] rejection creation",
            outboundMessage.identifier()
        );

        // persist and attach the rejection evidence to the message
        var rejectionEvidence = this.evidenceCreator.createFailure(
            ConnectorEvidenceType.SUBMISSION_REJECTION,
            outboundMessage,
            ConnectorMessageRejectionReason.OTHER
        );

        var messageWithEvidence = outboundMessage
            .toBuilder()
            .evidences(List.of(rejectionEvidence))
            .transportedEvidences(List.of(rejectionEvidence))
            .build();

        this.evidenceVerifier.verify(rejectionEvidence.type(), messageWithEvidence);

        // persist and attach the rejection evidence to the message
        var rejectionMessage = this.evidenceMessageCreator.create(
            outboundMessage, rejectionEvidence
        );

        log.debug("Created rejection evidence message [{}]", rejectionMessage.identifier());

        return rejectionMessage.switchDirection();
    }
}
