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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorListProcessingModeServiceTest {
    @Mock
    private ConnectorProcessingModeRepository processingModeRepository;

    @InjectMocks
    private ConnectorListProcessingModeService processingModeService;

    @Test
    void should_return_all_processing_modes() {
        when(processingModeRepository.findAll())
            .thenReturn(List.of(ProcessingModeTestFixtures.createWithBusinessDomain()));

        var foundProcessingModes = this.processingModeService.execute();

        assertThat(foundProcessingModes).isNotEmpty();
        assertThat(foundProcessingModes).hasSize(1);
    }
}
