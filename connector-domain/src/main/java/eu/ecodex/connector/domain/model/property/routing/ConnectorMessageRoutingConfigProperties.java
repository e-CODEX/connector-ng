/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.property.routing;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import java.util.Map;
import lombok.Builder;


/**
 * Encapsulates the configuration properties for message routing within a connector.
 *
 * <p>This record defines the high-level routing configuration for the connector, enabling or
 * disabling routing and associating specific configuration properties with individual business
 * domains. Each business domain is uniquely identified and has its own routing settings, which
 * include backend and gateway configurations.
 *
 * @param enabled         Indicates whether the message routing functionality is enabled. When set
 *                        to {@code false}, routing functionality for the connector is disabled
 *                        across all business domains.
 * @param businessDomains A mapping of business domain identifiers, represented by
 *                        {@code ConnectorBusinessDomainIdentifier}, to their associated
 *                        configuration properties, represented by
 *                        {@code ConnectorMessageRoutingBusinessDomainProperties}.
 */
@Builder(toBuilder = true)
public record ConnectorMessageRoutingConfigProperties(
        boolean enabled,
        Map<ConnectorBusinessDomainIdentifier, ConnectorMessageRoutingBusinessDomainProperties>
        businessDomains
) {
}
