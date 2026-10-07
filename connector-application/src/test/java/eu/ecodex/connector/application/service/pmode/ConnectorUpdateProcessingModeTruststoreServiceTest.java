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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.pmode.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorUpdateProcessingModeTruststoreService")
public class ConnectorUpdateProcessingModeTruststoreServiceTest {
    private static final String PMODE_UUID = "3f2b8c1e-7a4d-4e9b-9c0a-1d2e3f4a5b6c";

    @Mock
    private ConnectorProcessingModeRepository processingModeRepository;

    private ConnectorUpdateProcessingModeTruststoreService service;

    @BeforeEach
    void setUp() {
        service = new ConnectorUpdateProcessingModeTruststoreService(processingModeRepository);
    }

    @Nested
    @DisplayName("when the processing mode exists")
    class WhenProcessingModeExists {
        private ConnectorTruststore requestedTruststore;
        private ConnectorTruststore persistedTruststore;

        @BeforeEach
        void setUp() {
            requestedTruststore = asTruststore("requested");
            persistedTruststore = asTruststore("persisted");

            when(processingModeRepository.findByUuid(PMODE_UUID))
                .thenReturn(asProcessingMode(asTruststore("existing")));
            when(processingModeRepository.updateTruststore(PMODE_UUID, requestedTruststore))
                .thenReturn(asProcessingMode(persistedTruststore));
        }

        @Test
        void should_return_updated_truststore_when_processing_mode_exists() {
            var result = service.execute(PMODE_UUID, requestedTruststore);

            assertThat(result).isSameAs(persistedTruststore);
        }
    }

    @Nested
    @DisplayName("when the processing mode does not exist")
    class WhenProcessingModeDoesNotExist {
        @BeforeEach
        void setUp() {
            when(processingModeRepository.findByUuid(PMODE_UUID)).thenReturn(null);
        }

        @Test
        void should_fail_when_processing_mode_is_missing() {
            var truststore = asTruststore("requested");

            assertThatThrownBy(() -> service.execute(PMODE_UUID, truststore))
                .isInstanceOf(ConnectorProcessingModeNotFoundException.class)
                .hasMessage("Processing mode with UUID %s not found".formatted(PMODE_UUID));
        }
    }

    private ConnectorTruststore asTruststore(String alias) {
        return ConnectorTruststore.builder()
                                  .filename(alias)
                                  .build();
    }

    private ConnectorProcessingMode asProcessingMode(ConnectorTruststore truststore) {
        return ConnectorProcessingMode.builder()
                                      .uuid(PMODE_UUID)
                                      .truststore(truststore)
                                      .build();
    }
}
