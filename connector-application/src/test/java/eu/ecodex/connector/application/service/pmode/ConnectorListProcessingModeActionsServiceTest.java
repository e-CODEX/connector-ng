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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorActionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorListProcessingModeActionsServiceTest {
    @Mock
    private ConnectorActionRepository actionRepository;

    @InjectMocks
    private ConnectorListProcessingModeActionsService listProcessingModeActionsService;

    @Test
    void should_fail_when_the_processing_mode_identifier_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> this.listProcessingModeActionsService.execute(null)
        );
    }

    @Test
    void should_return_all_the_actions() {
        when(actionRepository.findAllByBusinessDomainIdentifier(any()))
            .thenReturn(List.of(ActionTestFixtures.createAction()));

        var actions = this.listProcessingModeActionsService.execute("default_business_domain");

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(1);

        verify(actionRepository).findAllByBusinessDomainIdentifier(any());
    }
}
