/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.utils;

import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingBusinessDomainItem;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingBusinessDomainProperties;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingConfigProperties;
import eu.ecodex.connector.domain.model.property.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.domain.routing.ConnectorRoutingRulePattern;
import java.util.Map;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "LineLength"})
public class MessageRoutingConfigProviderUtil {
    public static ConnectorMessageRoutingConfigProperties getRoutingProperties() {
        return routingProperties();
    }

    public static ConnectorMessageRoutingConfigProperties getDisabledRoutingProperties() {
        var routingProperties = routingProperties();
        return routingProperties.toBuilder().enabled(false).build();
    }

    public static ConnectorMessageRoutingConfigProperties getRoutingPropertiesWithNoDefaultBackendName() {
        var routingProperties = routingProperties();
        var backendRoutingRule = routingProperties
                .businessDomains()
                .get(BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier())
                .backend();

        var domainProperties = defaultBusinessDomainProperties()
                .toBuilder()
                .backend(backendRoutingRule.toBuilder().defaultName(null).build())
                .build();

        var businessDomain = Map.of(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier(),
                domainProperties
        );

        return routingProperties.toBuilder().businessDomains(businessDomain).build();
    }

    public static ConnectorMessageRoutingConfigProperties getRoutingPropertiesWithNoDefaultBackendRules() {
        var routingProperties = routingProperties();
        var backendRoutingRule = routingProperties.businessDomains().get(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        ).backend();

        var domainProperties = defaultBusinessDomainProperties()
                .toBuilder()
                .backend(backendRoutingRule.toBuilder().rules(null).build())
                .build();

        var businessDomain = Map.of(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier(),
                domainProperties
        );

        return routingProperties
                .toBuilder()
                .businessDomains(businessDomain)
                .build();
    }

    private static ConnectorMessageRoutingConfigProperties routingProperties() {

        var defaultBusinessDomainProperties = defaultBusinessDomainProperties();

        var businessDomain = Map.of(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier(),
                defaultBusinessDomainProperties
        );

        return ConnectorMessageRoutingConfigProperties
                .builder()
                .enabled(true)
                .businessDomains(businessDomain)
                .build();
    }

    private static ConnectorMessageRoutingBusinessDomainProperties defaultBusinessDomainProperties() {
        var backendRoutingRule = ConnectorMessageRoutingRule
                .builder()
                .linkName("backend_connector_test")
                .description("Test touting rule")
                .matchClause(new ConnectorRoutingRulePattern("equals(ServiceName, 'Connector-TEST')"))
                .build();
        var backendLinkName = ConnectorLinkPartnerName
                .builder()
                .name("backend_connector_test")
                .build();

        return ConnectorMessageRoutingBusinessDomainProperties
                .builder()
                .backend(
                        ConnectorMessageRoutingBusinessDomainItem
                                .builder()
                                .defaultName(ConnectorDefaults.DEFAULT_BACKEND_NAME)
                                .rules(Map.of(backendLinkName, backendRoutingRule))
                                .build()
                )
                .gateway(
                        ConnectorMessageRoutingBusinessDomainItem
                                .builder()
                                .defaultName(ConnectorDefaults.DEFAULT_GATEWAY_NAME)
                                .build()
                )
                .build();
    }
}
