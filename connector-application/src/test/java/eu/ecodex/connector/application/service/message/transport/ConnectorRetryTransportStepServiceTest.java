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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepRetryException;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRetryTransportStepServiceTest {
    private static final String TRANSPORT_ID = "6e3320bb-6724-4387-822c-a2914dba559a";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorLinkSubmitter linkSubmitter;

    @InjectMocks
    private ConnectorRetryTransportStepService retryTransportStepService;

    @Test
    void retry_should_submit_transported_message() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(
            ConnectorMessageTransportStatus.FAILED, 0);
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);
        doNothing().when(linkSubmitter).submit(any());

        // When
        retryTransportStepService.execute(TRANSPORT_ID);

        // Then
        var messageCaptor = ArgumentCaptor.forClass(ConnectorMessage.class);
        verify(linkSubmitter).submit(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isNotNull();
        verify(transportStepRepository).findByIdentifier(TRANSPORT_ID);
        verifyNoMoreInteractions(transportStepRepository, linkSubmitter);
    }

    @Test
    void retry_should_throw_retry_exception_when_not_retriable() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage();
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);

        // When
        // Then
        assertThrows(ConnectorMessageTransportStepRetryException.class, () ->
            retryTransportStepService.execute(TRANSPORT_ID));

        verifyNoInteractions(linkSubmitter);
    }

    @Test
    void retry_should_throw_retry_exception_when_any_exception_occurred() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(
            ConnectorMessageTransportStatus.FAILED, 0);
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);
        doThrow(IllegalArgumentException.class).when(linkSubmitter).submit(any());

        // When
        // Then
        assertThrows(ConnectorMessageTransportStepRetryException.class, () ->
            retryTransportStepService.execute(TRANSPORT_ID));

        verifyNoMoreInteractions(transportStepRepository, linkSubmitter);
    }

    @Test
    void retry_should_throw_NotFound_exception_when_not_found() {
        // Given
        when(transportStepRepository.findByIdentifier(any())).thenReturn(null);

        // When
        // Then
        assertThrows(ConnectorMessageTransportStepNotFoundException.class, () ->
            retryTransportStepService.execute(TRANSPORT_ID));

        verifyNoInteractions(linkSubmitter);
    }
}