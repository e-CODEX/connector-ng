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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.api.ConnectorMessageRoutingService;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageRoutingConfigProvider;
import eu.ecodex.connector.utils.BusinessDomainIdentifierUtil;
import eu.ecodex.connector.utils.MessageRoutingConfigProviderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageRoutingService} implementation.
 */
@SuppressWarnings({"checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageRoutingServiceTest {
    @Mock
    private ConnectorMessageRoutingConfigProvider connectorMessageRoutingConfigProvider;
    private ConnectorMessageRoutingService connectorMessageRoutingService;

    @BeforeEach
    void setUp() {
        connectorMessageRoutingService = new ConnectorMessageRoutingServiceImpl(
                connectorMessageRoutingConfigProvider
        );
    }

    @Test
    void should_return_true_if_routing_is_enabled() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderUtil.getRoutingProperties());

        var isRoutingEnabled = connectorMessageRoutingService.isRoutingEnabled(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(isRoutingEnabled).isTrue();

        verify(connectorMessageRoutingConfigProvider, times(1)).getRoutingProperties();
    }

    @Test
    void should_return_false_if_routing_is_disabled() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderUtil.getDisabledRoutingProperties());

        var isRoutingEnabled = connectorMessageRoutingService.isRoutingEnabled(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(isRoutingEnabled).isFalse();

        verify(connectorMessageRoutingConfigProvider, times(1)).getRoutingProperties();
    }

    @Test
    void should_return_backend_name_from_routing_properties() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderUtil.getRoutingProperties());

        var backendName = connectorMessageRoutingService.getDefaultBackendName(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendName).isEqualTo(
                MessageRoutingConfigProviderUtil
                        .getRoutingProperties()
                        .businessDomains()
                        .get(BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier())
                        .backend()
                        .defaultName()
        );
    }

    @Test
    void should_return_default_backend_name_if_no_routing_properties_are_available() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(
                        MessageRoutingConfigProviderUtil.getRoutingPropertiesWithNoDefaultBackendName()
                );

        var backendName = connectorMessageRoutingService.getDefaultBackendName(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendName).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
    }

    @Test
    void should_return_backend_routing_rules_if_set() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderUtil.getRoutingProperties());

        var backendRoutingRule = connectorMessageRoutingService.getBackendRoutingRule(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendRoutingRule).isNotNull();
    }

    @Test
    void should_return_empty_map_if_no_backend_routing_rules_are_set() {
        when(connectorMessageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderUtil.getRoutingPropertiesWithNoDefaultBackendRules());

        var backendRoutingRule = connectorMessageRoutingService.getBackendRoutingRule(
                BusinessDomainIdentifierUtil.createDefaultBusinessDomainIdentifier()
        );

        assertThat(backendRoutingRule).isEmpty();
    }
}
