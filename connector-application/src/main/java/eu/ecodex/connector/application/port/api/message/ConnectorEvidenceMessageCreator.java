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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Responsible for creating a {@link ConnectorMessage} that contains or represents a
 * {@link ConnectorMessageEvidence} related to an existing business message.
 *
 * <p>This component transforms a processed business {@link ConnectorMessage}
 * and its associated {@link ConnectorMessageEvidence} into a new connector message that can be
 * sent, stored, or further processed as evidence of the original message handling.</p>
 */
public interface ConnectorEvidenceMessageCreator {
    /**
     * Creates a connector evidence message.
     *
     * @param businessMessage the original connector message that was processed
     * @param evidence        the evidence describing the outcome of the message processing
     *
     * @return a {@link ConnectorMessage} representing the evidence message
     */
    ConnectorMessage create(
        @Nonnull ConnectorMessage businessMessage, @Nonnull ConnectorMessageEvidence evidence);

    /**
     * Builds an outbound evidence message for a backend trigger (parties and recipients swapped
     * relative to the referenced business message).
     *
     * @param businessMessage referenced gateway-to-backend business message
     * @param evidence        generated signed evidence to transport
     * @param triggerMessage  original trigger submitted by the backend
     *
     * @return evidence message ready for gateway submission
     */
    ConnectorMessage createForTrigger(
        @Nonnull ConnectorMessage businessMessage,
        @Nonnull ConnectorMessageEvidence evidence,
        @Nonnull ConnectorMessage triggerMessage);
}
