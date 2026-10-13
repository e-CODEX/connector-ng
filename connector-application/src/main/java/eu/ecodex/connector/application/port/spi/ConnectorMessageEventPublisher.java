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
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for publishing {@link ConnectorPublishable} messages within the connector
 * system, facilitating downstream processing or distribution.
 *
 * <p>Implementations are responsible for broadcasting or delivering the messages to their
 * intended consumers or systems, in accordance with the connector's messaging flow.
 *
 * @param <T> the type of {@link ConnectorPublishable} message that can be published
 */
public interface ConnectorMessageEventPublisher<T extends ConnectorPublishable> {
    /**
     * Publishes the given message for downstream processing or distribution.
     *
     * <p>The method returns normally if the message was handed over for publication. If
     * publication fails, the failure is reported by an exception.
     *
     * @param message the message to publish (must not be null)
     */
    void publish(@Nonnull T message);
}
