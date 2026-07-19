/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.routing;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingBusinessDomainItem;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingBusinessDomainProperties;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfigurationProvider;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.routing.ConnectorRoutingRulePattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the connector message routing.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector.routing")
public class ConnectorMessageRoutingProperties implements
    ConnectorMessageRoutingConfigurationProvider {
    private boolean enabled;
    private String defaultBackendName;
    private List<BackendRuleProperties> backendRules = new ArrayList<>();

    @Override
    public ConnectorMessageRoutingConfiguration getConfiguration() {
        // Accumulate all rules under the single default domain before building
        var partnerRoutingRules = new LinkedHashMap<ConnectorLinkPartnerName,
            ConnectorMessageRoutingRule>();

        for (var rule : backendRules) {
            var partnerName = new ConnectorLinkPartnerName(rule.getLinkName());
            var routingRule = ConnectorMessageRoutingRule.builder()
                                                         .linkName(rule.getLinkName())
                                                         .matchClause(
                                                             new ConnectorRoutingRulePattern(
                                                             rule.getMatchClause()))
                                                         .build();
            partnerRoutingRules.put(partnerName, routingRule);
        }

        var backendRouting = new ConnectorMessageRoutingBusinessDomainItem(
            defaultBackendName,
            Collections.unmodifiableMap(partnerRoutingRules)
        );

        var domainProperties = new ConnectorMessageRoutingBusinessDomainProperties(
            backendRouting,
            null
        );

        var businessDomainRouting = Map.of(
            ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID, domainProperties);

        return new ConnectorMessageRoutingConfiguration(enabled, businessDomainRouting);
    }
}
