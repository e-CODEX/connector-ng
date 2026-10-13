/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.message;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import jakarta.annotation.Nonnull;

/**
 * Repository (port) for persisting the AS4-specific properties associated with connector messages.
 *
 * <p>The AS4 properties support message integrity, routing and alignment with the AS4 messaging
 * protocol, enabling communication between the parties involved.
 */
public interface ConnectorMessageAS4PropertiesRepository {
    /**
     * Saves the AS4 properties carried by the given business message.
     *
     * @param message the {@link ConnectorBusinessMessage} whose AS4 properties are saved (must not
     *                be null)
     *
     * @return the saved {@link ConnectorMessageAS4Properties}
     */
    ConnectorMessageAS4Properties save(@Nonnull ConnectorBusinessMessage message);
}
