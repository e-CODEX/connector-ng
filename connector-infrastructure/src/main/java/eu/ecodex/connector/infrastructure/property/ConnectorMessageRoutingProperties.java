/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfigurationProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the connector message routing.
 */
@Getter
@Setter
@Configuration
public class ConnectorMessageRoutingProperties implements
        ConnectorMessageRoutingConfigurationProvider {
    @Override
    public ConnectorMessageRoutingConfiguration getConfiguration() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
