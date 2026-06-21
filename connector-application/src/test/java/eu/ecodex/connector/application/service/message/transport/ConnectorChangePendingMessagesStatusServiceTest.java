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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.service.impl.message.transport.ConnectorChangePendingMessagesStatusService;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorChangePendingMessagesStatusServiceTest {
    private static final String BACKEND_NAME = "backend_alice";
    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @InjectMocks
    private ConnectorChangePendingMessagesStatusService changePendingMessagesStatusService;

    @Test
    void should_change_pending_messages_status_successfully() {
        var pendingMessagesIds = List.of("1", "2");
        when(transportStepRepository.findPendingTransportSteps(BACKEND_NAME))
                .thenReturn(pendingMessagesIds);
        doNothing().when(transportStepRepository)
                   .updateStatus(pendingMessagesIds, ConnectorMessageTransportStatus.DOWNLOADED);

        changePendingMessagesStatusService.execute(
                BACKEND_NAME,
                ConnectorMessageTransportStatus.DOWNLOADED
        );

        verify(transportStepRepository).findPendingTransportSteps(BACKEND_NAME);
        verify(transportStepRepository).updateStatus(
                pendingMessagesIds,
                ConnectorMessageTransportStatus.DOWNLOADED
        );
    }
}
