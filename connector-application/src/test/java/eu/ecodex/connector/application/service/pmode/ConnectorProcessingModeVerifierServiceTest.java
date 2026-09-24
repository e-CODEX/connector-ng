/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.pmode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeInvalidTruststoreException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorProcessingModeVerifierService")
public class ConnectorProcessingModeVerifierServiceTest {
    private static final ConnectorBusinessDomainIdentifier BUSINESS_DOMAIN_IDENTIFIER =
        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();

    @Mock
    private ConnectorProcessingModeRepository processingModeRepository;

    @InjectMocks
    protected ConnectorProcessingModeVerifierService processingModeVerifierService;

    @Nested
    @DisplayName("when verification succeeds")
    class WhenVerificationSucceeds {
        @Test
        void should_verify_the_processing_mode() {
            var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain();
            when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(
                processingMode);

            assertThatNoException().isThrownBy(
                () -> processingModeVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER)
            );
        }
    }

    @Nested
    @DisplayName("when verification fails")
    class WhenVerificationFails {
        @Test
        void should_fail_when_the_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> processingModeVerifierService.execute(null)
            );
        }

        @Test
        void should_fail_when_the_processing_mode_does_not_exist() {
            when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(null);

            assertThrows(
                ConnectorProcessingModeNotFoundException.class,
                () -> processingModeVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER)
            );
        }

        @Test
        void should_fail_when_the_processing_mode_truststore_is_invalid() {
            var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain()
                                                           .toBuilder()
                                                           .truststore(ConnectorTruststore.builder()
                                                                                          .build())
                                                           .build();
            when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(
                processingMode);

            assertThrows(
                ConnectorProcessingModeInvalidTruststoreException.class,
                () -> processingModeVerifierService.execute(BUSINESS_DOMAIN_IDENTIFIER)
            );
        }
    }
}
