/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.property;

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import lombok.Builder;

/**
 * Represents the configurable properties of a business domain within the connector system. This
 * record defines metadata and configuration options associated with a specific business domain,
 * which is a logical grouping or categorization in the system.
 *
 * @param identifier  The identifier of the business domain. It is a required property used to
 *                    uniquely identify the domain within the connector system.
 * @param description A brief description of the business domain, providing additional context or
 *                    details about its purpose and scope.
 * @param enabled     A flag indicating whether the business domain is currently active and
 *                    operational within the connector configuration.
 * @param source      The source of the configuration for the business domain. This property defines
 *                    the origin of the configuration, such as database, implementation, or
 *                    environment.
 */
@Builder
public record ConnectorBusinessDomainProperties(
        String identifier,
        String description,
        boolean enabled,
        ConnectorConfigurationSource source
) {
    public ConnectorBusinessDomainProperties {
        source = ConnectorConfigurationSource.IMPLEMENTATION;
    }
}
