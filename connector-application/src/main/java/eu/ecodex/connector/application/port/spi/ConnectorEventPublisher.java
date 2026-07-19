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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Represents a publisher for broadcasting {@link ConnectorMessage} events within the connector
 * system.
 *
 * <p>This interface is responsible for defining the contract for publishing
 * {@link ConnectorMessage} instances to downstream components or systems. It facilitates the
 * communication and propagation of message-related events, allowing for decoupling between the
 * producers and consumers of such events.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li> Publishing {@link ConnectorMessage} events to relevant components or systems.
 *     <li> Supporting integration or event-driven architectures within the messaging system.
 * </ul>
 *
 * <p>Thread Safety:
 * Implementations of this interface should ensure thread safety if instances are used in a
 * multithreaded environment.
 */
public interface ConnectorEventPublisher {
    /**
     * Publishes a {@link ConnectorMessage} for downstream processing or distribution within the
     * connector system.
     *
     * <p>Implementations of this method are responsible for broadcasting the provided
     * message to relevant consumers or systems, ensuring that the message is delivered to its
     * intended recipients in accordance with the connector's messaging flow.
     *
     * @param message The {@link ConnectorMessage} to be published. Must not be null.
     */
    void publish(@Nonnull ConnectorMessage message);
}
