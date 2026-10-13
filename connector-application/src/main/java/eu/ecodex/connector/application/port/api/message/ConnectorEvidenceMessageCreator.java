/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Responsible for creating a {@link ConnectorEvidenceMessage} that carries a
 * {@link ConnectorMessageEvidence} related to an existing {@link ConnectorBusinessMessage}.
 *
 * <p>This component transforms a processed business message and its associated evidence into a
 * new evidence message that can be sent, stored, or further processed as proof of the original
 * message handling.
 */
public interface ConnectorEvidenceMessageCreator {
    /**
     * Creates an evidence message for the given business message.
     *
     * @param businessMessage the original business message that was processed (must not be null)
     * @param evidence        the evidence describing the outcome of the message processing (must
     *                        not be null)
     *
     * @return a {@link ConnectorEvidenceMessage} carrying the given evidence
     */
    ConnectorEvidenceMessage create(
        @Nonnull ConnectorBusinessMessage businessMessage,
        @Nonnull ConnectorMessageEvidence evidence);

    /**
     * Builds an outbound evidence message in response to an evidence trigger submitted by a
     * backend.
     *
     * <p>The parties and recipients are swapped relative to the referenced business message.
     *
     * @param businessMessage  the gateway-to-backend business message the evidence refers to (must
     *                         not be null)
     * @param evidence         the generated, signed evidence to transport (must not be null)
     * @param triggeredMessage the original trigger submitted by the backend (must not be null)
     *
     * @return an {@link ConnectorEvidenceMessage} ready for submission to the gateway
     */
    ConnectorEvidenceMessage createForTrigger(
        @Nonnull ConnectorBusinessMessage businessMessage,
        @Nonnull ConnectorMessageEvidence evidence,
        @Nonnull ConnectorTriggeredEvidenceMessage triggeredMessage);
}
