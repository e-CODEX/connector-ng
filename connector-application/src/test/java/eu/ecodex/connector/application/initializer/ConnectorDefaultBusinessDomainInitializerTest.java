/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.initializer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.ConnectorBusinessDomainPropertiesProviderTestFixtures;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorBusinessDomainPropertiesProvider;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
public class ConnectorDefaultBusinessDomainInitializerTest {
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;
    @Mock
    private ConnectorBusinessDomainPropertiesProvider domainPropertiesProvider;

    private ConnectorDefaultBusinessDomainInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new ConnectorDefaultBusinessDomainInitializer(
                businessDomainRepository, domainPropertiesProvider
        );
    }

    @Test
    void should_not_initialize_business_domain_when_already_exists() {
        when(businessDomainRepository.findAll())
                .thenReturn(List.of(BusinessDomainTestFixtures.createDefaultBusinessDomain()));

        initializer.run(mock(ApplicationArguments.class));

        verify(businessDomainRepository).findAll();
        verify(businessDomainRepository, never()).save(any());
        verify(domainPropertiesProvider, never()).getProperties();
    }

    @Test
    void should_initialize_business_domain_from_properties_when_not_exists() {
        when(businessDomainRepository.findAll()).thenReturn(Collections.emptyList());
        when(domainPropertiesProvider.getProperties())
                .thenReturn(
                        ConnectorBusinessDomainPropertiesProviderTestFixtures
                                .createDefaultProperties()
                );

        initializer.run(mock(ApplicationArguments.class));

        verify(domainPropertiesProvider, times(1)).getProperties();
        verify(businessDomainRepository, times(1)).save(any());
    }

    @Test
    void should_initialize_business_domain_from_connector_when_not_exists() {
        when(businessDomainRepository.findAll()).thenReturn(Collections.emptyList());
        when(domainPropertiesProvider.getProperties()).thenReturn(null);

        initializer.run(mock(ApplicationArguments.class));

        verify(businessDomainRepository).save(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN);
    }
}
