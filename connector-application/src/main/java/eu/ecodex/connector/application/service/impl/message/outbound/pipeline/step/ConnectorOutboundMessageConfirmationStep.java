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

import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents a processing step in the outbound message workflow that creates a submission
 * confirmation message for a given outbound message and switches its direction.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li> Ensures that the provided {@link ConnectorMessage} contains exactly one business
 *     evidence before processing.
 *     <li> Creates a confirmation message based on the submission evidence contained in the
 *     original message.
 *     <li> Modifies the created confirmation message by switching its direction
 * </ul>
 *
 * <p>Thread-safety:
 * - This class is intended to be stateless and thread-safe. The injected dependencies must also
 *   be thread-safe to ensure safe usage in concurrent environments.
 */
@Slf4j
@Component
public class ConnectorOutboundMessageConfirmationStep implements ConnectorMessageStep {
    private final ConnectorEvidenceMessageCreator evidenceMessageCreator;

    public ConnectorOutboundMessageConfirmationStep(
            ConnectorEvidenceMessageCreator evidenceMessageCreator) {
        this.evidenceMessageCreator = evidenceMessageCreator;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage submissionEvidenceMessage) {
        log.debug(
                "Processing outbound message with submission confirmation message "
                + "[{}]", submissionEvidenceMessage.identifier()
        );

        var transportedEvidences = submissionEvidenceMessage.transportedEvidences();
        if (transportedEvidences == null || transportedEvidences.isEmpty()) {
            throw new IllegalStateException("Message has no transported evidences!");
        }

        var submissionEvidence = submissionEvidenceMessage.transportedEvidences().getFirst();
        var confirmationMessage = this.evidenceMessageCreator.create(
                submissionEvidenceMessage, submissionEvidence
        );

        log.debug("Created confirmation message: [{}]", confirmationMessage.identifier());

        return confirmationMessage.switchDirection();
    }
}
