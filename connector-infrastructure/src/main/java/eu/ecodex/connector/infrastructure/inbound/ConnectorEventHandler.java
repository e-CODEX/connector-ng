/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import jakarta.annotation.Nonnull;


/**
 * Represents a handler for processing connector-specific events. This interface serves as a
 * contract for handling messages that implement the {@link ConnectorPublishable} marker interface.
 *
 * <p>Implementations of this interface are expected to provide the logic for processing
 * various types of connector-related messages. This could involve operations such as validation,
 * transformation, routing, or triggering specific workflows within the system.
 *
 * @param <T> The type of the message to process, which must extend {@link ConnectorPublishable}.
 */
public interface ConnectorEventHandler<T extends ConnectorPublishable> {
    /**
     * Handles the given {@link ConnectorPublishable} to facilitate processing, validation, or other
     * operations required by the connector system.
     *
     * <p>This method is designed to process incoming messages represented by
     * {@link ConnectorPublishable}. Implementations of this method should define specific
     * behaviours for handling these messages, including tasks such as validation, transformation,
     * or routing within the system.
     *
     * @param message The {@link ConnectorPublishable} to be processed. Must not be null.
     */
    void handle(@Nonnull T message);
}
