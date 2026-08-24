/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.transport;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorListTransportStepsServiceTest {
    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @InjectMocks
    private ConnectorListTransportStepsService listTransportStepsService;

    @Test
    void should_fail_when_the_page_request_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> listTransportStepsService.execute(null, null, null)
        );
    }

    @Test
    void should_return_paginated_transport_steps() {
        var pageResult = ConnectorPageResult.of(
            List.of(TransportStepFixtures.createTransportStep()),
            1,
            0,
            20

        );
        when(transportStepRepository.findAll(any(), any(), any())).thenReturn(pageResult);

        var request = ConnectorPageRequest.builder().page(0).size(20).build();
        var result = listTransportStepsService.execute(request, null, null);

        assertThat(result).isNotNull();
        assertThat(result.content().size()).isEqualTo(1);
    }
}
