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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import jakarta.annotation.Nonnull;

/**
 * Service interface for managing and persisting connector messages.
 */
public interface ConnectorMessageService {
    /**
     * Retrieves a {@link ConnectorMessage} based on the specified message and direction.
     *
     * @param message   the connector message to be matched; must not be null
     * @param direction the direction of the connector message to be matched; must not be null
     *
     * @return the {@link ConnectorMessage} that matches the given message and direction, or
     *         {@code null} if no match is found
     */
    ConnectorMessage findByIdentifierAndDirection(
            @Nonnull ConnectorMessage message, @Nonnull ConnectorMessageDirection direction);

    /**
     * Determines whether the given connector message is classified as an evidence message.
     *
     * @param message the connector message to be evaluated; must not be null
     *
     * @return {@code true} if the message is an evidence message, {@code false} otherwise
     */
    boolean isEvidenceMessage(@Nonnull ConnectorMessage message);

    /**
     * Determines whether the given connector message qualifies as an evidence trigger message.
     *
     * @param message the connector message to be evaluated; must not be null
     *
     * @return {@code true} if the message is classified as an evidence trigger message,
     *         {@code false} otherwise
     */
    boolean isEvidenceTriggerMessage(@Nonnull ConnectorMessage message);
}
