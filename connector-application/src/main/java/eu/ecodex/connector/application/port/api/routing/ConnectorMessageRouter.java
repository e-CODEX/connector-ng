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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.routing.ConnectorMessageRoutingRule;
import java.util.Map;

/**
 * Provides the routing configuration used to route connector messages to backends, per business
 * domain.
 */
public interface ConnectorMessageRouter {
    /**
     * Indicates whether message routing is enabled for the given business domain.
     *
     * @param businessDomainIdentifier identifier of the business domain to check; may be
     *                                 {@code null} only if the implementation supports it
     *
     * @return {@code true} if routing is enabled for the business domain, {@code false} otherwise
     */
    boolean isRoutingEnabled(ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Returns the name of the default backend for the given business domain.
     *
     * <p>The default backend is the one used when no routing rule applies, or when routing is
     * disabled.
     *
     * @param businessDomainIdentifier identifier of the business domain; may be {@code null} only
     *                                 if the implementation supports it
     *
     * @return the name of the default backend, or {@code null} if none is configured
     */
    String getDefaultBackendName(ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Returns the backend routing rules configured for the given business domain, indexed by the
     * name of the link partner (backend) they apply to.
     *
     * @param businessDomainIdentifier identifier of the business domain; may be {@code null} only
     *                                 if the implementation supports it
     *
     * @return a map of {@link ConnectorMessageRoutingRule} objects keyed by
     *     {@link ConnectorLinkPartnerName}; empty if no rules are configured
     */
    Map<ConnectorLinkPartnerName, ConnectorMessageRoutingRule> getBackendRoutingRule(
        ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
