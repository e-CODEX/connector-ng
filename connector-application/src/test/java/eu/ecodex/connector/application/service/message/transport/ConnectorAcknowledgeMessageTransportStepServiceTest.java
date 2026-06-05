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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.transport.ConnectorAcknowledgeMessageTransportStepService;
import eu.ecodex.connector.application.service.usecase.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageErrorRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorAcknowledgeMessageTransportStepServiceTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String TRANSPORT_STEP_ID = "step-001";
    private static final String BACKEND_ID = "backend-ref-001";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageErrorRepository messageErrorRepository;
    @InjectMocks
    private ConnectorAcknowledgeMessageTransportStepService service;

    @Test
    void should_throw_exception_when_transport_step_message_identifier_is_null() {
        assertThatThrownBy(() -> service.execute(null, submittedCommand()))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_transport_step_update_command_is_null() {
        assertThatThrownBy(() -> service.execute(MESSAGE_ID, null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_transport_step_message_identifier_and_update_command_are_null() {
        assertThatThrownBy(() -> service.execute(null, null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_transport_step_message_identifier_is_empty() {
        assertThatThrownBy(() -> service.execute("", submittedCommand()))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_transport_step_message_identifier_is_blank() {
        assertThatThrownBy(() -> service.execute(" ", submittedCommand()))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_command_status_is_failed_and_errors_is_null() {
        assertThatThrownBy(() -> service.execute(MESSAGE_ID, failedCommand(null)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_command_status_is_failed_and_errors_are_empty() {
        assertThatThrownBy(() -> service.execute(MESSAGE_ID, failedCommand(List.of())))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transportStepRepository, messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_no_transport_step_exists_for_the_message_identifier() {
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID)).thenReturn(null);

        assertThatThrownBy(() -> service.execute(MESSAGE_ID, submittedCommand()))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(messageRepository, messageErrorRepository);
    }

    @Test
    void should_throw_exception_when_existing_transport_step_status_is_submitted() {
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.SUBMITTED));

        assertThatThrownBy(() -> service.execute(MESSAGE_ID, submittedCommand()))
                .isInstanceOf(ConnectorMessageTransportStepException.class);

        verifyNoInteractions(messageRepository, messageErrorRepository);
        verify(transportStepRepository, never()).update(any(), any());
    }

    @Test
    void should_throw_exception_when_existing_transport_step_status_is_failed() {
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.FAILED));

        assertThatThrownBy(() -> service.execute(MESSAGE_ID, submittedCommand()))
                .isInstanceOf(ConnectorMessageTransportStepException.class);

        verify(transportStepRepository, never()).update(any(), any());
    }

    @Test
    void should_update_existing_transport_step_successfully() {
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID)).thenReturn(stepWithStatus(
                ConnectorMessageTransportStatus.DOWNLOADED));
        when(messageRepository.findByIdentifier(MESSAGE_ID)).thenReturn(message());

        service.execute(MESSAGE_ID, submittedCommand());

        verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
        verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step ->
                                                step.status() == ConnectorMessageTransportStatus.SUBMITTED
                )
        );
        verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 2)
        );
        verify(messageRepository).findByIdentifier(MESSAGE_ID);
        verify(messageRepository).setDeliveredToBackendAt(MESSAGE_ID);
        verify(messageRepository).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
        verifyNoInteractions(messageErrorRepository);
    }

    @Test
    void should_not_update_existing_transport_step_message_info_if_it_does_not_exist() {
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID)).thenReturn(stepWithStatus(
                ConnectorMessageTransportStatus.DOWNLOADED
        ));
        when(messageRepository.findByIdentifier(MESSAGE_ID)).thenReturn(null);

        assertThatNoException().isThrownBy(() -> service.execute(MESSAGE_ID, submittedCommand()));

        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(messageRepository, never()).updateBackendIdentifier(any(), any());
    }

    @Test
    void should_update_existing_transport_step_successfully_if_it_failed_to_be_delivered() {
        var errors = List.of(
                ConnectorMessageError.builder().label("timeout").build(),
                ConnectorMessageError.builder().label("connection refused").build()
        );
        when(transportStepRepository.findByMessageIdentifier(MESSAGE_ID)).thenReturn(stepWithStatus(
                ConnectorMessageTransportStatus.DOWNLOADED));

        service.execute(MESSAGE_ID, failedCommand(errors));

        verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
        verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step ->
                                                step.status() == ConnectorMessageTransportStatus.FAILED
                )
        );
        verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 2)
        );
        verify(messageErrorRepository).save(MESSAGE_ID, errors);
        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(messageRepository, never()).updateBackendIdentifier(any(), any());
    }

    private ConnectorMessage message() {
        return MessageTestFixtures.createValidInboundBusinessMessage()
                                  .toBuilder()
                                  .identifier(MESSAGE_ID)
                                  .build();
    }

    private UpdateMessageTransportCommand submittedCommand() {
        return UpdateMessageTransportCommand.builder()
                                            .status(ConnectorMessageTransportStatus.SUBMITTED)
                                            .remoteMessageIdentifier(BACKEND_ID)
                                            .errors(List.of())
                                            .build();
    }

    private UpdateMessageTransportCommand failedCommand(List<ConnectorMessageError> errors) {
        return UpdateMessageTransportCommand.builder()
                                            .status(ConnectorMessageTransportStatus.FAILED)
                                            .errors(errors)
                                            .build();
    }

    private ConnectorMessageTransportStep stepWithStatus(ConnectorMessageTransportStatus status) {
        return ConnectorMessageTransportStep.builder()
                                            .identifier(TRANSPORT_STEP_ID)
                                            .status(status)
                                            .numberOfAttempts(1)
                                            .transportedMessage(message())
                                            .build();
    }
}
