/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorServiceService;
import eu.ecodex.connector.domain.exception.ConnectorServiceNotFoundException;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit test class for {@code ConnectorServiceService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorServiceServiceTest {
    @Mock
    private ConnectorServiceRepository serviceRepository;
    private ConnectorServiceService connectorService;

    @BeforeEach
    void setUp() {
        this.connectorService = new ConnectorServiceServiceImpl(serviceRepository);
    }

    // find service by name and business domain
    @Test
    void should_find_service_by_name_and_business_domain_successfully() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        var service = this.connectorService.findByNameAndBusinessDomain(
                "Connector-TEST", "default");
        assertThat(service).isNotNull();
        assertThat(service.name()).isEqualTo("Connector-TEST");
        assertThat(service.type()).isEqualTo("urn:e-codex:services:");
    }

    @Test
    void should_throw_exception_when_service_does_not_exist() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorServiceNotFoundException.class,
                () -> this.connectorService.findByNameAndBusinessDomain("Connector-TEST", "default")
        );
    }

    @Test
    void should_throw_exception_when_service_name_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.connectorService.findByNameAndBusinessDomain(null, "default")
        );
    }

    @Test
    void should_throw_exception_when_business_domain_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.connectorService.findByNameAndBusinessDomain("Connector-TEST", null)
        );
    }
}
