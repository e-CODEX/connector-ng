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

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import java.util.Map;
import lombok.Builder;


/**
 * Represents a routing configuration for a specific connector business domain.
 *
 * <p>This record encapsulates details for managing the routing rules specific to a business domain
 * as well as its default name. It allows mapping between link partners and their associated routing
 * rules.
 *
 * @param defaultName The human-readable name representing the default configuration of the business
 *                    domain.
 * @param rules       A mapping of link partner names to their corresponding routing rules, enabling
 *                    flexible routing logic tailored to different partners within the business
 *                    domain.
 */
@Builder(toBuilder = true)
public record ConnectorMessageRoutingBusinessDomainItem(
        String defaultName,
        Map<ConnectorLinkPartnerName, ConnectorMessageRoutingRule> rules
) {
}
