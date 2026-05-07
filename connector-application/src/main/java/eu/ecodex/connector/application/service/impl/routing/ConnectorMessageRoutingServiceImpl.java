/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.routing;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfigurationProvider;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.application.service.usecase.routing.ConnectorMessageRoutingService;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of the ConnectorMessageRoutingService.
 */
@Slf4j
@Component
public class ConnectorMessageRoutingServiceImpl implements ConnectorMessageRoutingService {
    private final ConnectorMessageRoutingConfigurationProvider routingConfigurationProvider;

    public ConnectorMessageRoutingServiceImpl(
            ConnectorMessageRoutingConfigurationProvider routingConfigurationProvider) {
        this.routingConfigurationProvider = routingConfigurationProvider;
    }


    @Override
    public boolean isRoutingEnabled(ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var configuration = this.routingConfigurationProvider.getConfiguration();
        log.debug("Routing config: [{}]", configuration);

        return configuration.enabled();
    }

    @Override
    public String getDefaultBackendName(
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var configuration = this.routingConfigurationProvider.getConfiguration();
        var domainRoutingConfig = configuration.businessDomains().get(businessDomainIdentifier);

        return domainRoutingConfig.backend().defaultName() != null
                ? domainRoutingConfig.backend().defaultName()
                : ConnectorDefaults.DEFAULT_BACKEND_NAME;
    }

    @Override
    public Map<ConnectorLinkPartnerName, ConnectorMessageRoutingRule> getBackendRoutingRule(
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var configuration = this.routingConfigurationProvider.getConfiguration();
        var domainRoutingConfig = configuration.businessDomains().get(businessDomainIdentifier);

        return domainRoutingConfig.backend().rules() != null
                ? domainRoutingConfig.backend().rules()
                : Map.of();
    }
}
