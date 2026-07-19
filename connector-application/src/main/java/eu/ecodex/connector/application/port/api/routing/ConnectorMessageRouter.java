/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.routing;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import java.util.Map;

/**
 * Defines an interface to manage and retrieve message routing configurations within the connector
 * system for specific business domains.
 */
public interface ConnectorMessageRouter {
    /**
     * Checks if routing is enabled for the specified business domain within the connector system.
     *
     * <p>This method determines whether the message routing functionality is active for the given
     * {@code businessDomainIdentifier}. If routing is enabled, the system routes messages according
     * to the associated configuration; otherwise, routing is inactive for the domain.
     *
     * @param businessDomainIdentifier The uuid representing the business domain for which routing
     *                                 status needs to be determined. Must not be null.
     *
     * @return {@code true} if routing is enabled for the specified business domain; {@code false}
     *     otherwise.
     */
    boolean isRoutingEnabled(ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    String getDefaultBackendName(ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    Map<ConnectorLinkPartnerName, ConnectorMessageRoutingRule> getBackendRoutingRule(
        ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
