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
 * Repository interface for managing AS4-specific properties associated with connector messages.
 *
 * <p>The AS4 properties are essential in ensuring message integrity, routing, and alignment with
 * the AS4 messaging protocol, enabling seamless communication between involved parties.
 */
public interface ConnectorMessageAS4PropertiesRepository {
    /**
     * Saves the provided connector business message's associated AS4 properties.
     *
     * @param message The connector business message to save.
     *
     * @return The saved connector message AS4 properties.
     */
    ConnectorMessageAS4Properties save(@Nonnull ConnectorBusinessMessage message);
}
