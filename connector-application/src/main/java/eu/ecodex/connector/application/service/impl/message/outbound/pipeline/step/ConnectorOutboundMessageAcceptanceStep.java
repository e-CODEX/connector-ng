/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step;

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotBusinessException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents a processing step in the connector message workflow that is responsible for creating
 * submission acceptance evidences for outbound messages. It performs the following functions:
 * <ul>
 *     <li> Generates success evidence of type SUBMISSION_ACCEPTANCE for a given outbound message.
 *     <li> Attaches the generated evidence to the message.
 * </ul>
 *
 * <p>This class is designed to ensure evidences are systematically created and integrated into the
 * overall message lifecycle.
 */
@Slf4j
@Component
public class ConnectorOutboundMessageAcceptanceStep implements ConnectorMessageStep {
    private final ConnectorMessageEvidenceCreator evidenceCreator;
    private final ConnectorMessageEvidenceVerifier evidenceVerifier;

    public ConnectorOutboundMessageAcceptanceStep(
            ConnectorMessageEvidenceCreator evidenceCreator,
            ConnectorMessageEvidenceVerifier evidenceVerifier) {
        this.evidenceCreator = evidenceCreator;
        this.evidenceVerifier = evidenceVerifier;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug(
                "processing outbound message with submission acceptance evidence for: [{}]",
                outboundMessage
        );

        if (!outboundMessage.isBusinessMessage()) {
            throw new ConnectorMessageNotBusinessException("message must be a business message");
        }

        // persist and attach the submission evidence to the message
        var submissionEvidence = this.evidenceCreator.createSuccess(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, outboundMessage
        );

        var messageWithEvidence = outboundMessage
                .toBuilder()
                .evidences(List.of(submissionEvidence))
                .transportedEvidences(List.of(submissionEvidence))
                .build();

        this.evidenceVerifier.verify(submissionEvidence.type(), messageWithEvidence);

        log.debug(
                "created submission acceptance evidence: [{}] for message [{}]",
                submissionEvidence, outboundMessage
        );

        return messageWithEvidence;
    }
}
