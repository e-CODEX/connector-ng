/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.inbound.pipeline.step;

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link ConnectorMessageStep} responsible for generating non-delivery evidence for an inbound
 * {@link ConnectorMessage}.
 *
 * <p>This step is executed when an inbound message cannot be delivered to the
 * intended backend system. It creates a {@link ConnectorEvidenceType#NON_DELIVERY} evidence
 * describing the failure, attaches it to the message, verifies that the message satisfies the
 * requirements for this evidence type, and produces a corresponding evidence
 * {@link ConnectorMessage}.
 *
 * <p>The generated evidence message has its direction switched so it can be returned to the
 * originating party as proof that the message could not be delivered.
 */
@Slf4j
@Component
public class ConnectorInboundMessageNonDeliveryStep implements ConnectorMessageStep {
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;

    /**
     * Creates a new inbound message non-delivery step.
     *
     * @param evidenceMessageCreator component responsible for creating connector messages
     *                               containing evidences
     * @param evidenceCreator        component responsible for creating evidence objects describing
     *                               message processing outcomes
     * @param evidenceVerifier       component responsible for validating that the message satisfies
     *                               the requirements for the generated evidence type
     */
    public ConnectorInboundMessageNonDeliveryStep(
            ConnectorEvidenceMessageCreator evidenceMessageCreator,
            ConnectorMessageEvidenceCreator evidenceCreator,
            ConnectorMessageEvidenceVerifier evidenceVerifier) {
        this.evidenceMessageCreator = evidenceMessageCreator;
        this.evidenceCreator = evidenceCreator;
        this.evidenceVerifier = evidenceVerifier;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug(
                "Processing inbound message [{}] non delivery creation",
                inboundMessage.identifier()
        );

        var nonDeliveryEvidence = this.evidenceCreator.createFailure(
                ConnectorEvidenceType.NON_DELIVERY,
                inboundMessage,
                ConnectorMessageRejectionReason.OTHER
        );

        var messageWithEvidence = inboundMessage
                .toBuilder()
                .evidences(List.of(nonDeliveryEvidence))
                .transportedEvidences(List.of(nonDeliveryEvidence))
                .build();

        this.evidenceVerifier.verify(nonDeliveryEvidence.type(), messageWithEvidence);

        // persist and attach the rejection evidence to the message
        var nonDeliveryMessage = this.evidenceMessageCreator.create(
                inboundMessage, nonDeliveryEvidence
        );

        log.debug(
                "Created non delivery evidence for the message : [{}]",
                nonDeliveryMessage.identifier()
        );

        return nonDeliveryMessage.switchDirection();
    }
}
