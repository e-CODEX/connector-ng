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
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.application.port.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRegisterBusinessDomainService")
public class ConnectorRegisterBusinessDomainServiceTest {
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;

    @InjectMocks
    private ConnectorRegisterBusinessDomainService registerBusinessDomainService;

    @Nested
    @DisplayName("when registration succeeds")
    class WhenRegistrationSucceeds {
        @Test
        void should_register_the_business_domain() {
            var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

            when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);
            when(businessDomainRepository.save(any()))
                .thenReturn(BusinessDomainTestFixtures.createdDefaultBusinessDomain());

            var createdBusinessDomain = registerBusinessDomainService.execute(businessDomain);

            assertThat(createdBusinessDomain).isNotNull();
            assertThat(createdBusinessDomain.identifier()).isEqualTo(businessDomain.identifier());
            assertThat(createdBusinessDomain.source())
                .isEqualTo(ConnectorConfigurationSource.IMPLEMENTATION);
            assertThat(createdBusinessDomain.uuid()).isNotEmpty();
            assertThat(createdBusinessDomain.createdAt()).isNotNull();
            assertThat(createdBusinessDomain.updatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("when registration fails")
    class WhenRegistrationFails {
        @Test
        void should_fail_when_the_identifier_already_exists() {
            var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();

            when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

            assertThrows(
                ConnectorBusinessDomainAlreadyExistsException.class,
                () -> registerBusinessDomainService.execute(businessDomain)
            );
        }

        @Test
        void should_fail_when_the_business_domain_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> registerBusinessDomainService.execute(null)
            );
        }
    }
}
