/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType.BACKEND;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.model.ConnectorErrorCode;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link ConnectorEvidenceService} interface, responsible for managing and
 * processing evidences related to messages in the connector domain.
 *
 * <p>This service handles various operations, including creating evidences of specific types,
 * processing evidence-triggered states and validations, and managing the rejection or confirmation
 * status of messages based on evidence data. It ensures that evidence is processed according to
 * defined priorities and rules.
 */
@Slf4j
@DomainService
public class ConnectorEvidenceServiceImpl implements ConnectorEvidenceService {
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

    private final ConnectorEvidenceToolkit evidenceToolkit;
    private final ConnectorMessageService messageService;

    public ConnectorEvidenceServiceImpl(
            ConnectorEvidenceToolkit evidenceToolkit,
            ConnectorMessageService messageService) {
        this.evidenceToolkit = evidenceToolkit;
        this.messageService = messageService;
    }

    private int getHighestReceivedEvidencePriority(List<ConnectorMessageEvidence> evidences) {
        return evidences.stream()
                        .map(evidence -> evidence.type().getPriority())
                        .max(Comparator.naturalOrder())
                        .orElse(0);
    }

    private ConnectorMessageEvidence create(
            ConnectorEvidenceType evidenceType,
            ConnectorMessage message,
            ConnectorMessageRejectionReason rejectionReason) {
        return this.evidenceToolkit.create(message, evidenceType, rejectionReason);
    }

    @Override
    public ConnectorMessageEvidence createSuccess(
            @NonNull ConnectorEvidenceType evidenceType,
            @NonNull ConnectorMessage message) {
        return this.create(evidenceType, message, null);
    }

    @Override
    public ConnectorMessageEvidence createFailure(
            @NonNull ConnectorEvidenceType evidenceType,
            @NonNull ConnectorMessage message,
            ConnectorMessageRejectionReason reason) {
        return this.create(evidenceType, message, reason);
    }

    @Override
    public void processMessage(
            @NonNull ConnectorEvidenceType evidenceType,
            @NonNull ConnectorMessage message) {
        log.debug("processing message [{}] with evidence [{}]", message, evidenceType);

        var transportedEvidences = message.transportedEvidences();

        if (transportedEvidences == null) {
            throw new ConnectorEvidenceException("message has no evidences!");
        }

        int highestPriority = getHighestReceivedEvidencePriority(transportedEvidences);

        if (evidenceType.getPriority() < highestPriority) {
            log.info(
                    "evidence [{}] will not influence the rejected or confirmed state of message "
                    + "[{}] because it has lower priority than the already received evidences. "
                    + "error_code=[{}]",
                    evidenceType,
                    message,
                    ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY.getCode()
            );

            throw new ConnectorEvidenceNotRelevantException(
                    ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY
            );
        }

        if (NEGATIVE_EVIDENCE_TYPES.contains(evidenceType)) {
            log.warn("message [{}] has been rejected by evidence [{}]", message, evidenceType);

            this.messageService.setAsRejected(message);

            return;
        }

        if (POSITIVE_EVIDENCE_TYPES.contains(evidenceType)) {
            if (this.messageService.isRejected(message)) {
                log.warn(
                        "message [{}] has already been rejected by an negative evidence! "
                        + "The positive evidence of type [{}] will be ignored!",
                        message, evidenceType
                );

                throw new ConnectorEvidenceNotRelevantException(
                        ConnectorErrorCode.EVIDENCE_IGNORED_MESSAGE_ALREADY_REJECTED
                );
            }

            this.messageService.setAsConfirmed(message);

            log.info("message [{}] has been confirmed by evidence [{}]", message, evidenceType);
        }
    }

    @Override
    public void isEvidenceTriggeringAllowed(@NonNull ConnectorMessage message) {
        if (!this.messageService.isEvidenceMessage(message)) {
            throw new ConnectorEvidenceException("the message is not an evidence trigger message!");
        }

        var source = message.direction().getSource();

        if (source != BACKEND) {
            throw new ConnectorEvidenceException("only backend can generate trigger messages");
        }
    }
}
