/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.service;

import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.annotation.Nonnull;

/**
 * Interface for managing evidences related to messages in the connector domain. The service
 * provides methods to create and process evidence of specific types for given messages.
 */
public interface ConnectorEvidenceService {
    /**
     * Creates a new instance of {@link ConnectorEvidence} of the specified evidence type and
     * associates it with the given connector message. This method is intended to generate evidence
     * representing a successful event or operation.
     *
     * @param evidenceType The type of evidence to create, representing the nature of the successful
     *                     operation. Must not be null.
     * @param message      The connector message associated with the evidence. Must not be null.
     *
     * @return A new {@link ConnectorEvidence} instance representing the success evidence for the
     *         given message.
     */
    ConnectorEvidence createSuccess(
            @Nonnull ConnectorEvidenceType evidenceType, @Nonnull ConnectorMessage message);

    /**
     * Creates a new instance of {@link ConnectorEvidence} of the specified evidence type,
     * associates it with the given connector message, and assigns a rejection reason if applicable.
     * This method is intended to generate evidence representing a failure event or operation.
     *
     * @param evidenceType The type of evidence to create, representing the nature of the failure
     *                     operation. Must not be null.
     * @param message      The connector message associated with the evidence. Must not be null.
     * @param reason       The reason for the message rejection, providing additional context for
     *                     the failure. Can be null if no specific rejection reason is required.
     *
     * @return A new {@link ConnectorEvidence} instance representing the failure evidence for the
     *         given message.
     */
    ConnectorEvidence createFailure(
            @Nonnull ConnectorEvidenceType evidenceType,
            @Nonnull ConnectorMessage message,
            ConnectorMessageRejectionReason reason);

    /**
     * Processes a connector message based on the specified evidence type. This method performs
     * operations related to the evidence's nature and the associated message.
     *
     * @param evidenceType The type of evidence to process. Represents the nature of the operation
     *                     to be executed. Must not be null.
     * @param message      The connector message associated with the processing operation. Must not
     *                     be null.
     */
    void processMessage(
            @Nonnull ConnectorEvidenceType evidenceType, @Nonnull ConnectorMessage message);

    /**
     * Determines whether evidence triggering is allowed for the given connector message. This
     * method evaluates the provided message to decide if evidence creation or associated operations
     * can be initiated.
     *
     * @param message The connector message to evaluate. Must not be null.
     */
    void isEvidenceTriggeringAllowed(@Nonnull ConnectorMessage message);
}
