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

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import lombok.Builder;

/**
 * Represents the configuration of a connector link within the system.
 *
 * <p>This record encapsulates the key properties required to define and manage the behavior
 * and structure of a connector link. It serves as a configurable entity that ties together the
 * link's name, implementation details, properties, and source.
 *
 * @param name           The name of the connector link configuration.
 * @param implementation The fully qualified class name of the implementation associated with the
 *                       link.
 * @param properties     Providing additional details for the link setup and its runtime behavior.
 * @param source         The origin of the configuration for the link, represented using the
 *                       {@link ConnectorConfigurationSource}.
 */
@Builder
public record ConnectorLinkConfiguration(
        ConnectorLinkConfigurationName name,
        String implementation,
        ConnectorLinkConfigurationProperties properties,
        ConnectorConfigurationSource source
) {
}
