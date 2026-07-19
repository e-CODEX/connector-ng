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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveProcessingModeServiceTest {
    @Mock
    private ConnectorProcessingModeRepository processingModeRepository;

    @InjectMocks
    private ConnectorRetrieveProcessingModeService retrieveProcessingModeService;

    @Test
    void should_throw_exception_if_processing_mode_uuid_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> retrieveProcessingModeService.execute(null)
        );
    }

    @Test
    void should_throw_exception_if_processing_mode_does_not_exist() {
        assertThrows(
            ConnectorProcessingModeNotFoundException.class,
            () -> retrieveProcessingModeService.execute("non-existing-uuid")
        );
    }

    @Test
    void should_retrieve_processing_mode_by_its_uuid_successfully() {
        when(processingModeRepository.findByUuid(any())).thenReturn(
            ProcessingModeTestFixtures.createWithBusinessDomain()
        );

        var foundProcessingMode = retrieveProcessingModeService.execute(
            "7b79a71b-ce4c-4e18-9f82-7fa072a29e7e");

        assertThat(foundProcessingMode).isNotNull();
        assertThat(foundProcessingMode.uuid()).isEqualTo("7b79a71b-ce4c-4e18-9f82-7fa072a29e7e");
        assertThat(foundProcessingMode.services()).isNotNull();
        assertThat(foundProcessingMode.actions()).isNotNull();
        assertThat(foundProcessingMode.parties()).isNotNull();
    }
}
