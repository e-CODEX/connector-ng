/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingBusinessDomainItem;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingBusinessDomainProperties;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingRule;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.routing.ConnectorRoutingRulePattern;
import java.util.Map;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "checkstyle:LineLength"})
public class MessageRoutingConfigurationTestFixtures {
    public static ConnectorMessageRoutingConfiguration getRoutingProperties() {
        return routingProperties();
    }

    public static ConnectorMessageRoutingConfiguration getConfiguration() {
        var defaultBusinessDomainProperties = defaultBusinessDomainProperties();

        var businessDomain = Map.of(
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier(),
            defaultBusinessDomainProperties
        );

        return ConnectorMessageRoutingConfiguration
            .builder()
            .enabled(true)
            .businessDomainRouting(businessDomain)
            .build();
    }

    private static ConnectorMessageRoutingBusinessDomainProperties defaultBusinessDomainProperties() {
        var backendRoutingRule = ConnectorMessageRoutingRule
            .builder()
            .linkName("backend_connector_test")
            .description("Test touting rule")
            .matchClause(
                new ConnectorRoutingRulePattern("equals(ServiceName, 'Connector-TEST')"))
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

    public static ConnectorMessageRoutingConfiguration getRoutingPropertiesWithNoDefaultBackendName() {
        var routingProperties = routingProperties();
        var backendRoutingRule = routingProperties
            .businessDomainRouting()
            .get(BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier())
            .backend();

        var domainProperties = defaultBusinessDomainProperties()
            .toBuilder()
            .backend(backendRoutingRule.toBuilder().defaultName(null).build())
            .build();

        var businessDomain = Map.of(
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier(),
            domainProperties
        );

        return routingProperties.toBuilder().businessDomainRouting(businessDomain).build();
    }

    private static ConnectorMessageRoutingConfiguration routingProperties() {

        var defaultBusinessDomainProperties = defaultBusinessDomainProperties();

        var businessDomain = Map.of(
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier(),
            defaultBusinessDomainProperties
        );

        return ConnectorMessageRoutingConfiguration
            .builder()
            .enabled(true)
            .businessDomainRouting(businessDomain)
            .build();
    }

    public static ConnectorMessageRoutingConfiguration getRoutingPropertiesWithNoDefaultBackendRules() {
        var routingProperties = routingProperties();
        var backendRoutingRule = routingProperties.businessDomainRouting().get(
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        ).backend();

        var domainProperties = defaultBusinessDomainProperties()
            .toBuilder()
            .backend(backendRoutingRule.toBuilder().rules(null).build())
            .build();

        var businessDomain = Map.of(
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier(),
            domainProperties
        );

        return routingProperties
            .toBuilder()
            .businessDomainRouting(businessDomain)
            .build();
    }

    public static ConnectorMessageRoutingConfiguration getDisabledRoutingProperties() {
        var routingProperties = routingProperties();
        return routingProperties.toBuilder().enabled(false).build();
    }
}
