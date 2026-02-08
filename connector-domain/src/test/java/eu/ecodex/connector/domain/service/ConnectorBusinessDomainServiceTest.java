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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainException;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorBusinessDomainService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorBusinessDomainServiceTest {
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;
    private ConnectorBusinessDomainService connectorBusinessDomainService;

    @BeforeEach
    void setUp() {
        this.connectorBusinessDomainService = new ConnectorBusinessDomainServiceImpl(
                businessDomainRepository
        );
    }

    // save business domain
    @Test
    void should_register_a_business_domain_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any()))
                .thenReturn(null);
        when(businessDomainRepository.save(any()))
                .thenReturn(businessDomain);

        var createdBusinessDomain = connectorBusinessDomainService.register(businessDomain);

        assertThat(createdBusinessDomain).isNotNull();
        assertThat(createdBusinessDomain.identifier()).isEqualTo(businessDomain.identifier());
        assertThat(createdBusinessDomain.source())
                .isEqualTo(ConnectorConfigurationSource.IMPLEMENTATION);
    }

    @Test
    void should_throw_exception_when_saving_business_domain_with_already_existing_identifier() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any()))
                .thenReturn(businessDomain);

        assertThrows(
                ConnectorBusinessDomainException.class,
                () -> connectorBusinessDomainService.register(businessDomain)
        );
    }

    @Test
    void should_throw_exception_if_business_domain_is_null_when_saving() {
        assertThrows(
                NullPointerException.class,
                () -> connectorBusinessDomainService.register(null)
        );
    }

    // find by uuid
    @Test
    void should_find_business_domain_by_identifier_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

        var foundBusinessDomain = connectorBusinessDomainService.findByIdentifier(businessDomain.identifier());

        assertThat(foundBusinessDomain).isNotNull();
        assertThat(foundBusinessDomain.identifier()).isEqualTo(businessDomain.identifier());
    }

    @Test
    void should_throw_exception_if_business_domain_is_not_found_when_finding_by_identifier() {
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);

        assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> connectorBusinessDomainService.findByIdentifier(
                        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier()
                )
        );
    }

    @Test
    void should_throw_exception_if_identifier_is_null_when_finding_business_domain_by_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> connectorBusinessDomainService.findByIdentifier(null)
        );
    }

    @Test
    void should_find_all_business_domains_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findAll()).thenReturn(List.of(businessDomain));

        var foundBusinessDomains = connectorBusinessDomainService.findAll();

        assertThat(foundBusinessDomains).isNotNull();
        assertThat(foundBusinessDomains).hasSize(1);
    }
}
