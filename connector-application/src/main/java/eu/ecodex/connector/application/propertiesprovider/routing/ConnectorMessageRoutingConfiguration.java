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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import java.util.Map;
import lombok.Builder;

/**
 * Represents the routing configuration for messages within the connector system.
 *
 * <p>This record defines whether message routing is enabled and includes domain-specific routing
 * properties. The configuration associates a set of business domains, identified by
 * {@link ConnectorBusinessDomainIdentifier}, with their corresponding routing properties,
 * represented by {@link ConnectorMessageRoutingBusinessDomainProperties}.
 *
 * <p>This abstraction allows for a clear separation of routing configurations across different
 * business contexts, ensuring modular and maintainable message routing logic.
 *
 * @param enabled         Indicates whether the message routing functionality is enabled.
 * @param businessDomains A mapping of {@link ConnectorBusinessDomainIdentifier} to their respective
 *                        {@link ConnectorMessageRoutingBusinessDomainProperties}. This map holds
 *                        the routing configurations for individual business domains.
 */
@Builder(toBuilder = true)
public record ConnectorMessageRoutingConfiguration(
        boolean enabled,
        Map<ConnectorBusinessDomainIdentifier, ConnectorMessageRoutingBusinessDomainProperties>
        businessDomains
) {
}
