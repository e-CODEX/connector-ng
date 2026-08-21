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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.MessageRoutingConfigurationTestFixtures;
import eu.ecodex.connector.application.propertiesprovider.routing.ConnectorMessageRoutingConfigurationProvider;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageRouterService} implementation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorMessageRouterService")
public class ConnectorMessageRouterTest {
    private static final ConnectorBusinessDomainIdentifier DEFAULT_BUSINESS_DOMAIN =
        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();

    @Mock
    private ConnectorMessageRoutingConfigurationProvider routingConfigurationProvider;

    @InjectMocks
    private ConnectorMessageRouterService connectorMessageRouterService;

    @Nested
    @DisplayName("is routing enabled")
    class IsRoutingEnabled {
        @Test
        void should_return_true_when_routing_is_enabled() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

            var isRoutingEnabled =
                connectorMessageRouterService.isRoutingEnabled(DEFAULT_BUSINESS_DOMAIN);

            assertThat(isRoutingEnabled).isTrue();
            verify(routingConfigurationProvider).getConfiguration();
        }

        @Test
        void should_return_false_when_routing_is_disabled() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getDisabledRoutingProperties());

            var isRoutingEnabled =
                connectorMessageRouterService.isRoutingEnabled(DEFAULT_BUSINESS_DOMAIN);

            assertThat(isRoutingEnabled).isFalse();
            verify(routingConfigurationProvider).getConfiguration();
        }
    }

    @Nested
    @DisplayName("get default backend name")
    class GetDefaultBackendName {
        @Test
        void should_return_the_configured_backend_name() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

            var backendName =
                connectorMessageRouterService.getDefaultBackendName(DEFAULT_BUSINESS_DOMAIN);

            assertThat(backendName).isEqualTo(
                MessageRoutingConfigurationTestFixtures
                    .getConfiguration()
                    .businessDomainRouting()
                    .get(DEFAULT_BUSINESS_DOMAIN)
                    .backend()
                    .defaultName()
            );
        }

        @Test
        void should_return_the_default_when_no_backend_name_is_configured() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getRoutingPropertiesWithNoDefaultBackendName());

            var backendName =
                connectorMessageRouterService.getDefaultBackendName(DEFAULT_BUSINESS_DOMAIN);

            assertThat(backendName).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
        }
    }

    @Nested
    @DisplayName("get backend routing rule")
    class GetBackendRoutingRule {
        @Test
        void should_return_the_rules_when_they_are_set() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getConfiguration());

            var backendRoutingRule =
                connectorMessageRouterService.getBackendRoutingRule(DEFAULT_BUSINESS_DOMAIN);

            assertThat(backendRoutingRule).isNotNull();
        }

        @Test
        void should_return_an_empty_map_when_no_rules_are_set() {
            when(routingConfigurationProvider.getConfiguration())
                .thenReturn(MessageRoutingConfigurationTestFixtures.getRoutingPropertiesWithNoDefaultBackendRules());

            var backendRoutingRule =
                connectorMessageRouterService.getBackendRoutingRule(DEFAULT_BUSINESS_DOMAIN);

            assertThat(backendRoutingRule).isEmpty();
        }
    }
}
