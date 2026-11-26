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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
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
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRetryTransportStepServiceTest {
    private static final String TRANSPORT_ID = "6e3320bb-6724-4387-822c-a2914dba559a";

    @Mock
    private ConnectorLinkSubmitter linkSubmitter;
    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @InjectMocks
    private ConnectorRetryTransportStepService retryTransportStepService;

    @ParameterizedTest
    @EnumSource(
        value = ConnectorMessageTransportStatus.class,
        names = {"FAILED", "READY_FOR_PULL"}
    )
    void retry_should_submit_transported_message(ConnectorMessageTransportStatus status) {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(status, 0);
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);
        doNothing().when(linkSubmitter).submit(any());

        // When
        retryTransportStepService.execute(TRANSPORT_ID);

        // Then
        verify(linkSubmitter).submit(same(step.transportedMessage()));
        verify(transportStepRepository).findByIdentifier(TRANSPORT_ID);
        verifyNoMoreInteractions(transportStepRepository, linkSubmitter);
    }

    @Test
    void retry_should_submit_an_evidence_message() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithEvidenceMessage().toBuilder()
            .status(ConnectorMessageTransportStatus.FAILED)
            .build();
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);

        // When
        retryTransportStepService.execute(TRANSPORT_ID);

        // Then
        verify(linkSubmitter).submit(same(step.transportedMessage()));
        verifyNoMoreInteractions(linkSubmitter);
    }

    @ParameterizedTest
    @EnumSource(
        value = ConnectorMessageTransportStatus.class,
        names = {"FAILED", "READY_FOR_PULL"}
    )
    void retry_should_submit_a_message_going_from_the_gateway_to_the_backend(
        ConnectorMessageTransportStatus status) {
        // Given
        var base = TransportStepFixtures.createTransportStepWithBusinessMessage(status, 1);
        var inbound = ((ConnectorBusinessMessage) base.transportedMessage()).toBuilder()
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .build();
        var step = base.toBuilder().transportedMessage(inbound).build();
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);

        // When
        retryTransportStepService.execute(TRANSPORT_ID);

        // Then
        verify(linkSubmitter).submit(same(inbound));
    }

    @ParameterizedTest
    @EnumSource(
        value = ConnectorMessageTransportStatus.class,
        mode = EnumSource.Mode.EXCLUDE,
        names = {"FAILED", "READY_FOR_PULL"}
    )
    void retry_should_throw_retry_exception_when_not_retriable(
        ConnectorMessageTransportStatus status) {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(status, 1);
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);

        // When
        // Then
        assertThatThrownBy(() -> retryTransportStepService.execute(TRANSPORT_ID))
            .isInstanceOf(ConnectorMessageTransportStepRetryException.class)
            .hasMessageContaining(TRANSPORT_ID)
            .hasMessageContaining(status.name())
            .hasMessageContaining("not retryable");

        verifyNoInteractions(linkSubmitter);
    }

    @Test
    void retry_should_throw_retry_exception_with_the_cause_when_any_exception_occurred() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(
            ConnectorMessageTransportStatus.FAILED, 0);
        var failure = new IllegalArgumentException("gateway unreachable");
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);
        doThrow(failure).when(linkSubmitter).submit(any());

        // When
        // Then
        assertThatThrownBy(() -> retryTransportStepService.execute(TRANSPORT_ID))
            .isInstanceOf(ConnectorMessageTransportStepRetryException.class)
            .hasMessageContaining(TRANSPORT_ID)
            .hasCause(failure);

        verifyNoMoreInteractions(transportStepRepository, linkSubmitter);
    }

    @Test
    void retry_should_not_leak_the_failure_reason_in_the_exception_message() {
        // Given
        var step = TransportStepFixtures.createTransportStepWithBusinessMessage(
            ConnectorMessageTransportStatus.FAILED, 0);
        when(transportStepRepository.findByIdentifier(any())).thenReturn(step);
        doThrow(new IllegalStateException("jdbc:mysql://internal-host/db")).when(linkSubmitter)
            .submit(any());

        // When
        // Then
        assertThatThrownBy(() -> retryTransportStepService.execute(TRANSPORT_ID))
            .isInstanceOf(ConnectorMessageTransportStepRetryException.class)
            .hasMessageNotContaining("internal-host");
    }

    @Test
    void retry_should_throw_NotFound_exception_when_not_found() {
        // Given
        when(transportStepRepository.findByIdentifier(any())).thenReturn(null);

        // When
        // Then
        assertThatThrownBy(() -> retryTransportStepService.execute(TRANSPORT_ID))
            .isInstanceOf(ConnectorMessageTransportStepNotFoundException.class)
            .hasMessageContaining(TRANSPORT_ID);

        verifyNoInteractions(linkSubmitter);
    }

    @Test
    void retry_should_fail_when_the_identifier_is_null() {
        // When
        // Then
        assertThatThrownBy(() -> retryTransportStepService.execute(null))
            .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, linkSubmitter);
    }
}
