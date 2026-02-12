/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.businessdomain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.application.service.impl.businessdomain.ConnectorRegisterBusinessDomainService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainException;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorRegisterBusinessDomainServiceTest {
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;
    @InjectMocks
    private ConnectorRegisterBusinessDomainService connectorBusinessDomainService;

    @Test
    void should_register_a_business_domain_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any()))
                .thenReturn(null);
        when(businessDomainRepository.save(any()))
                .thenReturn(businessDomain);

        var createdBusinessDomain = connectorBusinessDomainService.execute(businessDomain);

        assertThat(createdBusinessDomain).isNotNull();
        assertThat(createdBusinessDomain.identifier()).isEqualTo(businessDomain.identifier());
        AssertionsForInterfaceTypes.assertThat(createdBusinessDomain.source())
                                   .isEqualTo(ConnectorConfigurationSource.IMPLEMENTATION);
    }

    @Test
    void should_throw_exception_when_saving_business_domain_with_already_existing_identifier() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any()))
                .thenReturn(businessDomain);

        assertThrows(
                ConnectorBusinessDomainException.class,
                () -> connectorBusinessDomainService.execute(businessDomain)
        );
    }

    @Test
    void should_throw_exception_if_business_domain_is_null_when_saving() {
        assertThrows(
                NullPointerException.class,
                () -> connectorBusinessDomainService.execute(null)
        );
    }
}
