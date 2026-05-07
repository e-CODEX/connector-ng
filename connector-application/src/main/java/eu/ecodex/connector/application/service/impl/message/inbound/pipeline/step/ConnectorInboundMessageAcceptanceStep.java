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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link ConnectorMessageStep} responsible for generating acceptance evidence for an inbound
 * {@link ConnectorMessage}.
 *
 * <p>This step is executed when an inbound message has been successfully received and accepted by
 * the connector. It creates a {@link ConnectorEvidenceType#RELAY_REMMD_ACCEPTANCE} evidence,
 * attaches it to the message, verifies that the message satisfies the requirements for the evidence
 * type, and produces a confirmation {@link ConnectorMessage} containing the generated evidence.
 *
 * <p>The resulting confirmation message has its direction switched so it can be sent back to the
 * originating party as proof of successful reception and acceptance.
 */
@Slf4j
@Component
public class ConnectorInboundMessageAcceptanceStep implements ConnectorMessageStep {
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;

    /**
     * Creates a new inbound message acceptance step.
     *
     * @param evidenceMessageCreator component responsible for creating connector messages
     *                               containing evidences
     * @param evidenceCreator        component responsible for creating evidence objects describing
     *                               successful message handling
     * @param evidenceVerifier       component responsible for validating that the message satisfies
     *                               the requirements for the generated evidence type
     */
    public ConnectorInboundMessageAcceptanceStep(
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
                "Processing inbound message acceptance creation for: [{}]",
                inboundMessage.identifier()
        );

        var relayREMMDEvidence = this.evidenceCreator.createSuccess(
                ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                inboundMessage
        );

        var messageWithEvidence = inboundMessage
                .toBuilder()
                .evidences(List.of(relayREMMDEvidence))
                .transportedEvidences(List.of(relayREMMDEvidence))
                .build();

        this.evidenceVerifier.verify(relayREMMDEvidence.type(), messageWithEvidence);

        var confirmationMessage = this.evidenceMessageCreator.create(
                inboundMessage, relayREMMDEvidence
        );

        log.debug(
                "Created relay REMMD acceptance evidence message: [{}]",
                confirmationMessage.identifier()
        );

        return confirmationMessage.switchDirection();
    }
}
