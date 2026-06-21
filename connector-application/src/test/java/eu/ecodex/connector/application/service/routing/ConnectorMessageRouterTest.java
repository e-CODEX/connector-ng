/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.routing;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.MessageRoutingConfigurationTestFixtures;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.routing.ConnectorMessageRouterService;
import eu.ecodex.connector.domain.ConnectorDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageRouterService} implementation.
 */
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageRouterTest {
    @Mock
    private ConnectorMessageRoutingConfigurationProvider routingConfigurationProvider;

    @InjectMocks
    private ConnectorMessageRouterService connectorMessageRouterService;

    @Test
    void should_return_true_if_routing_is_enabled() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

        var isRoutingEnabled = connectorMessageRouterService.isRoutingEnabled(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(isRoutingEnabled).isTrue();

        verify(routingConfigurationProvider, times(1)).getConfiguration();
    }

    @Test
    void should_return_false_if_routing_is_disabled() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getDisabledRoutingProperties());

        var isRoutingEnabled = connectorMessageRouterService.isRoutingEnabled(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(isRoutingEnabled).isFalse();

        verify(routingConfigurationProvider, times(1)).getConfiguration();
    }

    @Test
    void should_return_backend_name_from_routing_properties() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

        var backendName = connectorMessageRouterService.getDefaultBackendName(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendName).isEqualTo(
                MessageRoutingConfigurationTestFixtures
                        .getConfiguration()
                        .businessDomainRouting()
                        .get(BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier())
                        .backend()
                        .defaultName()
        );
    }

    @Test
    void should_return_default_backend_name_if_no_routing_properties_are_available() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getRoutingPropertiesWithNoDefaultBackendName());

        var backendName = connectorMessageRouterService.getDefaultBackendName(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendName).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
    }

    @Test
    void should_return_backend_routing_rules_if_set() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

        var backendRoutingRule = connectorMessageRouterService.getBackendRoutingRule(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendRoutingRule).isNotNull();
    }

    @Test
    void should_return_empty_map_if_no_backend_routing_rules_are_set() {
        when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getRoutingPropertiesWithNoDefaultBackendRules());

        var backendRoutingRule = connectorMessageRouterService.getBackendRoutingRule(
                BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendRoutingRule).isEmpty();
    }
}
