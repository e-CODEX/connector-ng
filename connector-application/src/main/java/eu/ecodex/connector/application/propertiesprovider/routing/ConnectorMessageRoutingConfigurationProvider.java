/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.propertiesprovider.routing;

/**
 * Provider responsible for supplying the current {@link ConnectorMessageRoutingConfiguration}.
 *
 * <p>This component abstracts the source of routing configuration used by the connector message
 * routing mechanism. Implementations may retrieve the configuration from various sources such as
 * application properties, configuration files, a database, or a remote configuration service.
 *
 * <p>The provided configuration defines how connector messages are routed to
 * backend systems based on business domains and routing rules.</p>
 */
public interface ConnectorMessageRoutingConfigurationProvider {
    ConnectorMessageRoutingConfiguration getConfiguration();
}
