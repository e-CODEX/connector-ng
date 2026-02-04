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

import lombok.Builder;


/**
 * Represents the routing properties for business domain-specific configurations in the connector.
 *
 * <p>This record encapsulates backend and gateway routing configurations for a specific business
 * domain. Each configuration is represented by a {@link ConnectorMessageRoutingBusinessDomainItem},
 * which includes details such as a default name and associated routing rules.
 *
 * @param backend The routing configuration for backend components
 * @param gateway The routing configuration for gateway components
 */
@Builder(toBuilder = true)
public record ConnectorMessageRoutingBusinessDomainProperties(
        ConnectorMessageRoutingBusinessDomainItem backend,
        ConnectorMessageRoutingBusinessDomainItem gateway
) {
}
