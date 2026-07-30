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

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.api.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageErrorRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)

@DisplayName("ConnectorAckMessageTransportStepService")
public class ConnectorAckMessageTransportStepServiceTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String TRANSPORT_STEP_ID = "step-001";
    private static final String BACKEND_ID = "backend-ref-001";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @Mock
    private ConnectorMessageErrorRepository messageErrorRepository;

    @InjectMocks
    private ConnectorAckMessageTransportStepService service;

    private ConnectorMessage businessMessage() {
        return MessageTestFixtures.createInboundBusinessMessage()
                                  .toBuilder()
                                  .identifier(MESSAGE_ID)
                                  .build();
    }

    private ConnectorMessage evidenceMessage() {
        return MessageTestFixtures.createEvidenceMessage()
                                  .toBuilder()
                                  .identifier(MESSAGE_ID)
                                  .build();
    }

    private UpdateMessageTransportCommand deliveredCommand() {
        return UpdateMessageTransportCommand.builder()
                                            .status(ConnectorMessageTransportStatus.DELIVERED)
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

    private ConnectorMessageTransportStep stepWithStatus(
        ConnectorMessageTransportStatus status,
        int numberOfAttempts) {
        return ConnectorMessageTransportStep.builder()
                                            .identifier(TRANSPORT_STEP_ID)
                                            .status(status)
                                            .numberOfAttempts(numberOfAttempts)
                                            .transportedMessage(businessMessage())
                                            .build();
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_identifier_is_null() {
            assertThatThrownBy(() -> service.execute(null, deliveredCommand()))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_the_command_is_null() {
            assertThatThrownBy(() -> service.execute(MESSAGE_ID, null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_both_arguments_are_null() {
            assertThatThrownBy(() -> service.execute(null, null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_the_message_identifier_is_empty() {
            assertThatThrownBy(() -> service.execute("", deliveredCommand()))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_the_message_identifier_is_blank() {
            assertThatThrownBy(() -> service.execute(" ", deliveredCommand()))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_the_command_failed_with_null_errors() {
            assertThatThrownBy(() -> service.execute(MESSAGE_ID, failedCommand(null)))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }

        @Test
        void should_fail_when_the_command_failed_with_empty_errors() {
            assertThatThrownBy(() -> service.execute(MESSAGE_ID, failedCommand(List.of())))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                transportStepRepository,
                messageRepository,
                messageErrorRepository
            );
        }
    }

    @Nested
    @DisplayName("when the transport step is not found")
    class WhenStepNotFound {
        @Test
        void should_fail_when_no_transport_step_exists_for_the_message() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(null);

            assertThatThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()))
                .isInstanceOf(ConnectorMessageTransportStepNotFoundException.class);

            verifyNoInteractions(messageRepository, messageErrorRepository);
        }
    }

    @Nested
    @DisplayName("when the step is already in a terminal status")
    class WhenStepIsTerminal {
        @Test
        void should_skip_the_update_when_the_step_is_already_delivered() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.DELIVERED, 1));

            assertThatNoException()
                .isThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()));

            verifyNoInteractions(messageRepository, messageErrorRepository);
            verify(transportStepRepository, never()).update(any(), any());
        }

        @Test
        void should_skip_the_update_when_the_step_has_already_failed() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.FAILED, 1));

            assertThatNoException()
                .isThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()));

            verify(transportStepRepository, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("when the transported message is inconsistent")
    class WhenTransportedMessageIsInconsistent {
        @Test
        void should_fail_when_the_transported_message_identifier_is_null() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(
                    stepWithStatus(ConnectorMessageTransportStatus.SUBMITTED, 1)
                        .toBuilder()
                        .transportedMessage(evidenceMessage().toBuilder().identifier(null).build())
                        .build());

            assertThatThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()))
                .isInstanceOf(IllegalStateException.class);

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageRepository, never()).findByIdentifier(MESSAGE_ID);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository, never()).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
            verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());

            verifyNoInteractions(messageErrorRepository);
        }

        @Test
        void should_fail_when_the_transported_evidence_uuid_is_null() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(
                    stepWithStatus(ConnectorMessageTransportStatus.SUBMITTED, 1)
                        .toBuilder()
                        .transportedMessage(
                            evidenceMessage().toBuilder()
                                             .transportedEvidences(
                                                 List.of(
                                                     EvidenceTestFixtures.createDeliveryEvidence()
                                                                         .toBuilder()
                                                                         .uuid(null)
                                                                         .build()
                                                 )
                                             )
                                             .build()
                        )
                        .build());

            assertThatThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()))
                .isInstanceOf(IllegalStateException.class);

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageRepository, never()).findByIdentifier(MESSAGE_ID);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository, never()).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
            verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());

            verifyNoInteractions(messageErrorRepository);
        }
    }

    @Nested
    @DisplayName("when the delivery succeeds")
    class WhenDeliverySucceeds {
        @Test
        void should_update_the_business_message_step_and_the_message() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.DOWNLOADED, 1));
            when(messageRepository.findByIdentifier(MESSAGE_ID)).thenReturn(businessMessage());

            service.execute(MESSAGE_ID, deliveredCommand());

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageRepository).findByIdentifier(MESSAGE_ID);
            verify(messageRepository).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
            verifyNoInteractions(messageErrorRepository);
        }

        @Test
        void should_update_the_step_without_incrementing_the_attempts() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD, 0));

            service.execute(
                MESSAGE_ID,
                deliveredCommand().toBuilder()
                                  .status(ConnectorMessageTransportStatus.DOWNLOADED)
                                  .build()
            );

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.DOWNLOADED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 0)
            );
            verify(messageRepository, never()).findByIdentifier(MESSAGE_ID);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository, never()).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
            verifyNoInteractions(messageErrorRepository);
        }

        @Test
        void should_update_the_evidence_message_step_and_flag_the_evidence() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(
                    stepWithStatus(ConnectorMessageTransportStatus.SUBMITTED, 1)
                        .toBuilder()
                        .transportedMessage(evidenceMessage())
                        .build());

            service.execute(MESSAGE_ID, deliveredCommand());

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageRepository, never()).findByIdentifier(MESSAGE_ID);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository, never()).updateBackendIdentifier(MESSAGE_ID, BACKEND_ID);
            verify(evidenceRepository).setDeliveredToLinkPartnerAt(any());

            verifyNoInteractions(messageErrorRepository);
        }

        @Test
        void should_not_update_the_message_info_when_the_message_does_not_exist() {
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.DOWNLOADED, 1));
            when(messageRepository.findByIdentifier(MESSAGE_ID)).thenReturn(null);

            assertThatNoException()
                .isThrownBy(() -> service.execute(MESSAGE_ID, deliveredCommand()));

            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        }
    }

    @Nested
    @DisplayName("when the delivery failed")
    class WhenDeliveryFailed {
        @Test
        void should_update_the_step_and_save_the_errors() {
            var errors = List.of(
                ConnectorMessageError.builder().label("timeout").build(),
                ConnectorMessageError.builder().label("connection refused").build()
            );
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID))
                .thenReturn(stepWithStatus(ConnectorMessageTransportStatus.DOWNLOADED, 1));

            service.execute(MESSAGE_ID, failedCommand(errors));

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.FAILED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageErrorRepository).save(MESSAGE_ID, errors);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        }

        @Test
        void should_fail_to_update_the_step_and_save_the_errors_if_transported_message_id_is_null() {
            var errors = List.of(
                ConnectorMessageError.builder().label("timeout").build(),
                ConnectorMessageError.builder().label("connection refused").build()
            );
            when(transportStepRepository.findByMessageIdentifierOrRemoteSystemId(MESSAGE_ID)).thenReturn(
                stepWithStatus(ConnectorMessageTransportStatus.DOWNLOADED, 1)
                    .toBuilder()
                    .transportedMessage(businessMessage().toBuilder().identifier(null).build())
                    .build()
            );

            assertThatThrownBy(() -> service.execute(MESSAGE_ID, failedCommand(errors)))
                .isInstanceOf(IllegalStateException.class);

            verify(transportStepRepository).update(eq(TRANSPORT_STEP_ID), any());
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID),
                argThat(step -> step.status() == ConnectorMessageTransportStatus.FAILED)
            );
            verify(transportStepRepository).update(
                eq(TRANSPORT_STEP_ID), argThat(step -> step.numberOfAttempts() == 1)
            );
            verify(messageErrorRepository, never()).save(MESSAGE_ID, errors);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        }
    }
}
