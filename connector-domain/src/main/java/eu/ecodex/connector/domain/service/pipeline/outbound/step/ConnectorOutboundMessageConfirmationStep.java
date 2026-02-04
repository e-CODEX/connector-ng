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
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a processing step in the outbound message workflow that creates a submission
 * confirmation message for a given outbound message and switches its direction.
 *
 * <p>This class processes an outbound {@link ConnectorMessage} that has submission evidence and
 * generates a confirmation message by delegating the creation of the message to the
 * {@link ConnectorMessageService}. The generated confirmation message is then modified to switch
 * its direction for further processing in the outbound workflow.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li> Ensures that the provided {@link ConnectorMessage} contains exactly one business
 *     evidence before processing.
 *     <li> Creates a confirmation message based on the submission evidence contained in the
 *     original message.
 *     <li> Modifies the created confirmation message by switching its direction using
 *     the {@link ConnectorMessageService}.
 * </ul>
 *
 * <p>Thread-safety:
 * - This class is intended to be stateless and thread-safe. The injected dependencies must also
 *   be thread-safe to ensure safe usage in concurrent environments.
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageConfirmationStep implements ConnectorMessageStep {
    private final ConnectorMessageService messageService;

    /**
     * Constructs a new instance of {@code ConnectorOutboundMessageConfirmationStep}.
     *
     * @param messageService the {@link ConnectorMessageService} instance responsible for creating
     *                       and managing connector messages, including generating confirmation
     *                       messages and switching their direction.
     */
    public ConnectorOutboundMessageConfirmationStep(
            ConnectorMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage messageWithSubmissionEvidence) {
        log.debug(
                "processing outbound message with submission confirmation message creation for: "
                + "[{}]", messageWithSubmissionEvidence
        );

        var transportedEvidences = messageWithSubmissionEvidence.transportedEvidences();
        if (transportedEvidences == null || transportedEvidences.isEmpty()) {
            throw new IllegalStateException("message has no transported evidences!");
        }

        var submissionEvidence = messageWithSubmissionEvidence.transportedEvidences().getFirst();
        var confirmationMessage = this.messageService.createEvidenceMessage(
                messageWithSubmissionEvidence, submissionEvidence
        );

        log.debug("created confirmation message: [{}]", confirmationMessage);

        return this.messageService.switchDirection(
                confirmationMessage
        );
    }
}
