/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.link.configuration;

import lombok.Builder;

/**
 * Represents the configuration name for a link within the connector system.
 *
 * <p>This record encapsulates the name used to identify a specific link configuration, providing
 * a clear and reusable way to manage configuration names in the domain. It is designed to ensure
 * consistency and simplicity when dealing with link-related configurations.
 *
 * @param name The name of the link configuration.
 */
@Builder
public record ConnectorLinkConfigurationName(
        String name
) {
}
