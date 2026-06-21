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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.application.service.impl.businessdomain.ConnectorBusinessDomainVerifierService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorBusinessDomainVerifierServiceTest {
    private static final ConnectorBusinessDomainIdentifier BUSINESS_DOMAIN_IDENTIFIER =
            BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();

    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;

    @InjectMocks
    protected ConnectorBusinessDomainVerifierService businessDomainVerifierService;

    @Test
    void should_throw_exception_if_business_domain_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> businessDomainVerifierService.execute(null)
        );
    }

    @Test
    void should_throw_exception_if_business_domain_does_not_exist() {
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> businessDomainVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER)
        );
    }

    @Test
    void should_throw_exception_if_business_domain_is_disabled() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                                       .toBuilder()
                                                       .enabled(false)
                                                       .build();
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

        assertThrows(
                ConnectorBusinessDomainNotEnabledException.class,
                () -> businessDomainVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER)
        );
    }

    @Test
    void should_verify_business_domain_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

        businessDomainVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER);

        assertThatNoException().isThrownBy(() -> businessDomainVerifierService.execute(
                BUSINESS_DOMAIN_IDENTIFIER));
    }
}
