/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.property;

import eu.ecodex.connector.domain.model.property.ConnectorMessageProcessingProperties;

/**
 * Provides the connector message processing configuration.
 */
public interface ConnectorMessageProcessingConfigProvider {
    /**
     * Retrieves the configurable properties related to message processing within the connector. The
     * returned properties define various flags and parameters that influence the behavior of the
     * message processing logic, such as message ID generation, and verification modes for incoming
     * and outgoing messages.
     *
     * @return an instance of {@code ConnectorMessageProcessingProperties} containing the
     *         configuration for message processing.
     */
    ConnectorMessageProcessingProperties getProcessingProperties();
}
