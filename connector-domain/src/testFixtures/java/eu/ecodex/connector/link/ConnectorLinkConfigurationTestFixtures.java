/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link;

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.link.configuration.ConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.link.configuration.ConnectorLinkConfigurationName;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class ConnectorLinkConfigurationTestFixtures {
    public static ConnectorLinkConfiguration createConnectorLinkConfiguration() {
        return ConnectorLinkConfiguration
                .builder()
                .name(ConnectorLinkConfigurationName.builder().name("default_backend").build())
                .source(ConnectorConfigurationSource.IMPLEMENTATION)
                .build();
    }
}
