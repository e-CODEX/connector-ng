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
 * Responsible for creating a {@link ConnectorBusinessMessage} that contains or represents a
 * {@link ConnectorMessageEvidence} related to an existing business message.
 *
 * <p>This component transforms a processed business {@link ConnectorBusinessMessage}
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
     * @return a {@link ConnectorBusinessMessage} representing the evidence message
     */
    ConnectorEvidenceMessage create(
        @Nonnull ConnectorBusinessMessage businessMessage,
        @Nonnull ConnectorMessageEvidence evidence);

    /**
     * Builds an outbound evidence message for a backend trigger (parties and recipients swapped
     * relative to the referenced business message).
     *
     * @param businessMessage  referenced a gateway-to-backend business message
     * @param evidence         generated signed evidence to transport
     * @param triggeredMessage original trigger submitted by the backend
     *
     * @return evidence message ready for gateway submission
     */
    ConnectorEvidenceMessage createForTrigger(
        @Nonnull ConnectorBusinessMessage businessMessage,
        @Nonnull ConnectorMessageEvidence evidence,
        @Nonnull ConnectorTriggeredEvidenceMessage triggeredMessage);
}
