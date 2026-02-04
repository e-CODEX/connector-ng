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

import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingConfigProperties;

/**
 * Provides the configuration for connector message routing.
 *
 * <p>This interface is responsible for supplying the routing configuration properties
 * required to manage message routing functionality in the connector. Implementations of this
 * interface should provide access to settings such as whether routing is enabled and default
 * backend or gateway names.
 */
public interface ConnectorMessageRoutingConfigProvider {
    /**
     * Retrieves the configuration properties associated with connector message routing. These
     * properties determine whether routing is enabled and define routing settings per business
     * domain, such as backend and gateway configurations.
     *
     * @return an instance of {@code ConnectorMessageRoutingConfigProperties} containing the message
     *         routing configuration.
     */
    ConnectorMessageRoutingConfigProperties getRoutingProperties();
}
