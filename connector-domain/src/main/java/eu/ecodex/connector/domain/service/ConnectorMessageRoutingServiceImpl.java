/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorMessageRoutingService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageRoutingConfigProvider;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the ConnectorMessageRoutingService.
 */
@Slf4j
@DomainService
public class ConnectorMessageRoutingServiceImpl implements ConnectorMessageRoutingService {
    private final ConnectorMessageRoutingConfigProvider connectorMessageRoutingConfigProvider;

    public ConnectorMessageRoutingServiceImpl(
            ConnectorMessageRoutingConfigProvider connectorMessageRoutingConfigProvider) {
        this.connectorMessageRoutingConfigProvider = connectorMessageRoutingConfigProvider;
    }

    @Override
    public boolean isRoutingEnabled(ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var routingConfig = this.connectorMessageRoutingConfigProvider.getRoutingProperties();
        log.debug("routing config: [{}]", routingConfig);

        return routingConfig.enabled();
    }

    @Override
    public String getDefaultBackendName(
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var routingProperties = this.connectorMessageRoutingConfigProvider.getRoutingProperties();
        var domainRoutingConfig = routingProperties.businessDomains().get(businessDomainIdentifier);

        return domainRoutingConfig.backend().defaultName() != null
                ? domainRoutingConfig.backend().defaultName()
                : ConnectorDefaults.DEFAULT_BACKEND_NAME;
    }

    @Override
    public Map<ConnectorLinkPartnerName, ConnectorMessageRoutingRule> getBackendRoutingRule(
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var routingProperties = this.connectorMessageRoutingConfigProvider.getRoutingProperties();
        var domainRoutingConfig = routingProperties.businessDomains().get(businessDomainIdentifier);

        return domainRoutingConfig.backend().rules() != null
                ? domainRoutingConfig.backend().rules()
                : Map.of();
    }
}
