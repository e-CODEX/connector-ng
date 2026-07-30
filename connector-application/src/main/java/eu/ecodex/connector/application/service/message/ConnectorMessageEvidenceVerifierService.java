/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.ConnectorErrorCode;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorMessageEvidenceVerifier} service.
 */
@Slf4j
@Component
public class ConnectorMessageEvidenceVerifierService implements ConnectorMessageEvidenceVerifier {
    private static final EnumSet<ConnectorEvidenceType> NEGATIVE_EVIDENCE_TYPES = EnumSet.of(
        ConnectorEvidenceType.SUBMISSION_REJECTION,
        ConnectorEvidenceType.NON_DELIVERY,
        ConnectorEvidenceType.NON_RETRIEVAL,
        ConnectorEvidenceType.RELAY_REMMD_REJECTION,
        ConnectorEvidenceType.RELAY_REMMD_FAILURE
    );
    private static final EnumSet<ConnectorEvidenceType> POSITIVE_EVIDENCE_TYPES = EnumSet.of(
        ConnectorEvidenceType.DELIVERY,
        ConnectorEvidenceType.RETRIEVAL
    );

    private final ConnectorMessageRepository messageRepository;

    public ConnectorMessageEvidenceVerifierService(ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void verify(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorMessage message) {
        log.debug("Processing message [{}] with evidence [{}]", message.identifier(), evidenceType);

        if (message.identifier() == null) {
            throw new IllegalStateException("Message identifier is required");
        }

        var transportedEvidences = message.transportedEvidences();

        if (transportedEvidences == null || transportedEvidences.isEmpty()) {
            throw new ConnectorEvidenceException(
                "Message [{" + message.identifier() + "}] has no evidences!"
            );
        }

        int highestPriority = getHighestReceivedEvidencePriority(transportedEvidences);

        if (evidenceType.getPriority() < highestPriority) {
            log.info(
                "Evidence [{}] will not influence the rejected or confirmed state of message "
                    + "[{}] because it has lower priority than the already received evidences. "
                    + "error_code=[{}]",
                evidenceType,
                message.identifier(),
                ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY.getCode()
            );

            throw new ConnectorEvidenceNotRelevantException(
                ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY
            );
        }

        if (NEGATIVE_EVIDENCE_TYPES.contains(evidenceType)) {
            log.warn("Message [{}] has been rejected by evidence [{}]", message, evidenceType);

            this.messageRepository.setAsRejected(message.identifier());

            return;
        }

        if (POSITIVE_EVIDENCE_TYPES.contains(evidenceType)) {
            var currentMessage = this.messageRepository.findByIdentifier(message.identifier());
            if (currentMessage != null && currentMessage.isRejected()) {
                log.warn(
                    "Message [{}] has already been rejected by an negative evidence! "
                        + "The positive evidence of type [{}] will be ignored!",
                    message, evidenceType
                );

                throw new ConnectorEvidenceNotRelevantException(
                    ConnectorErrorCode.EVIDENCE_IGNORED_MESSAGE_ALREADY_REJECTED
                );
            }

            this.messageRepository.setAsConfirmed(message.identifier());

            log.info("Message [{}] has been confirmed by evidence [{}]", message, evidenceType);
        }
    }

    private int getHighestReceivedEvidencePriority(List<ConnectorMessageEvidence> evidences) {
        return evidences.stream()
                        .map(evidence -> evidence.type().getPriority())
                        .max(Comparator.naturalOrder())
                        .orElse(0);
    }
}
