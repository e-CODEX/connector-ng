/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * An interface defining the contract for publishing {@link ConnectorMessage} instances within a
 * connector system, facilitating downstream processing or distribution.
 *
 * <p>Classes implementing this interface are responsible for providing mechanisms
 * to broadcast or deliver the messages to their intended consumers or systems. Implementations
 * should ensure adherence to the connector's messaging flow requirements while delivering messages
 * reliably.
 *
 * @param <T> The type of {@link ConnectorPublishable} message that can be published.
 */
public interface ConnectorMessageEventPublisher<T extends ConnectorPublishable> {
    /**
     * Publishes a {@link ConnectorPublishable} message for downstream processing or distribution
     * within the connector system.
     *
     * <p>Implementations of this method are responsible for broadcasting the provided
     * message to relevant consumers or systems, ensuring that the message is delivered to its
     * intended recipients in accordance with the connector's messaging flow.
     *
     * @param message The {@link ConnectorPublishable} message to be published. Must not be null.
     */
    void publish(@Nonnull T message);
}
