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

import static eu.ecodex.connector.TransportStepFixtures.TRANSPORT_STEP_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessageStatus;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.processing.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRegisterMessageTransportStepService")
public class ConnectorRegisterMessageTransportStepServiceTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String BACKEND_NAME = "backend-a";
    private static final String GATEWAY_NAME = "gateway-a";
    private static final String TRANSPORT_SUFFIX = "gateway.example.com";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorRegisterMessageTransportStepService service;

    private ConnectorBusinessMessage message() {
        return BusinessMessageTestFixtures.createInboundMessage().toBuilder()
            .identifier(MESSAGE_ID)
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();
    }

    private ConnectorEvidenceMessage evidenceMessage() {
        return EvidenceMessageTestFixtures.createDeliveryEvidenceMessage().toBuilder()
            .identifier(MESSAGE_ID)
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .backendName(BACKEND_NAME)
            .gatewayName(GATEWAY_NAME)
            .build();
    }

    private ConnectorMessageProcessingConfiguration configuration() {
        return ConnectorMessageProcessingConfiguration.builder()
            .transportIdSuffix(TRANSPORT_SUFFIX)
            .build();
    }

    @Nested
    @DisplayName("when registration succeeds")
    class WhenRegistrationSucceeds {
        @Test
        void should_register_the_transport_step() {
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result.numberOfAttempts()).isEqualTo(1);
            assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.SUBMITTED);

            verify(transportStepRepository).save(
                argThat(step ->
                    step.numberOfAttempts() == 1
                        && step.status() == ConnectorMessageTransportStatus.SUBMITTED
                )
            );

            verify(transportStepRepository, never()).update(any(), any(), anyInt());
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"FAILED", "READY_FOR_PULL"}
        )
        void should_update_existing_transport_step_status_and_nb_of_attempts(
            ConnectorMessageTransportStatus status) {
            when(transportStepRepository.findByTransportedMessageIdentifier(any())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, 0)
            );
            when(transportStepRepository.update(any(), any(), anyInt())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, 1));

            var result = service.execute(message(), status);

            assertThat(result.numberOfAttempts()).isEqualTo(1);
            assertThat(result.status()).isEqualTo(status);

            verify(transportStepRepository).update(TRANSPORT_STEP_ID, status, 1);
            verify(transportStepRepository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_update_the_business_message_status_when_a_new_attempt_is_recorded(
            ConnectorMessageTransportStatus status) {
            when(transportStepRepository.findByTransportedMessageIdentifier(any())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(
                    ConnectorMessageTransportStatus.FAILED, 2)
            );
            when(transportStepRepository.update(any(), any(), anyInt())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, 3));

            service.execute(message(), status);

            verify(transportStepRepository).update(TRANSPORT_STEP_ID, status, 3);
            verify(messageRepository).updateStatus(
                MESSAGE_ID, ConnectorBusinessMessageStatus.fromTransportStep(status.name()));
        }

        @Test
        void should_not_update_any_message_status_for_an_evidence_message_attempt() {
            when(transportStepRepository.findByTransportedMessageIdentifier(any())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(
                    ConnectorMessageTransportStatus.FAILED, 0)
            );
            when(transportStepRepository.update(any(), any(), anyInt())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(
                    ConnectorMessageTransportStatus.SUBMITTED, 1));

            service.execute(evidenceMessage(), ConnectorMessageTransportStatus.SUBMITTED);

            verifyNoInteractions(messageRepository);
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_register_the_transport_step_for_every_allowed_status(
            ConnectorMessageTransportStatus status) {
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(message(), status);

            assertThat(result.status()).isEqualTo(status);
            assertThat(result.numberOfAttempts()).isEqualTo(1);
        }

        @Test
        void should_build_the_identifier_from_a_random_uuid_the_suffix_and_the_link_partner() {
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            var identifier = result.identifier();
            assertThat(identifier).endsWith("@" + TRANSPORT_SUFFIX + "_" + BACKEND_NAME);
        }

        @Test
        void should_generate_a_different_identifier_for_each_registration() {
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var first = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);
            var second = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(first.identifier()).isNotEqualTo(second.identifier());
        }

        @Test
        void should_return_the_step_returned_by_the_repository() {
            var saved = TransportStepFixtures.createTransportStepWithBusinessMessage()
                .toBuilder()
                .transportedMessage(message())
                .build();
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenReturn(saved);

            var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result).isSameAs(saved);
        }
    }

    @Nested
    @DisplayName("when resolving the link partner")
    class WhenResolvingTheLinkPartner {
        @Test
        void should_use_the_backend_name_when_the_message_goes_from_the_gateway_to_the_backend() {
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result.linkPartnerName()).isEqualTo(BACKEND_NAME);
        }

        @Test
        void should_use_the_gateway_name_when_the_message_goes_from_the_backend_to_the_gateway() {
            var outbound = message().toBuilder()
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .build();
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(outbound, ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result.linkPartnerName()).isEqualTo(GATEWAY_NAME);
            assertThat(result.identifier()).endsWith("_" + GATEWAY_NAME);
        }
    }

    @Nested
    @DisplayName("when the transported message is a business message")
    class WhenTransportedMessageIsABusinessMessage {
        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_update_the_message_status_from_the_transport_status(
            ConnectorMessageTransportStatus status) {
            var expectedStatus = ConnectorBusinessMessageStatus.fromTransportStep(status.name());
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(message(), status);

            verify(messageRepository).updateStatus(MESSAGE_ID, expectedStatus);
            assertThat(result.transportedMessage())
                .isInstanceOfSatisfying(
                    ConnectorBusinessMessage.class,
                    transported -> assertThat(transported.status()).isEqualTo(expectedStatus)
                );
        }
    }

    @Nested
    @DisplayName("when the transported message is an evidence message")
    class WhenTransportedMessageIsAnEvidenceMessage {
        @Test
        void should_register_the_step_without_updating_any_message_status() {
            var evidence = evidenceMessage();
            when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
            when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = service.execute(evidence, ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result.transportedMessage()).isSameAs(evidence);
            assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.SUBMITTED);
            verifyNoInteractions(messageRepository);
        }
    }

    @Nested
    @DisplayName("when the message already has a transport step")
    class WhenTheMessageAlreadyHasATransportStep {
        private void existing(ConnectorMessageTransportStatus status, int attempts) {
            when(transportStepRepository.findByTransportedMessageIdentifier(any())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, attempts)
            );
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_record_a_new_attempt_when_the_step_is_retryable(
            ConnectorMessageTransportStatus status) {
            existing(ConnectorMessageTransportStatus.READY_FOR_PULL, 2);
            when(transportStepRepository.update(any(), any(), anyInt())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, 3));

            service.execute(message(), status);

            verify(transportStepRepository).update(TRANSPORT_STEP_ID, status, 3);
            verify(transportStepRepository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_record_the_outcome_without_a_new_attempt_when_the_step_is_submitted(
            ConnectorMessageTransportStatus status) {
            existing(ConnectorMessageTransportStatus.SUBMITTED, 2);
            when(transportStepRepository.update(any(), any(), anyInt())).thenReturn(
                TransportStepFixtures.createTransportStepWithBusinessMessage(status, 2));

            var result = service.execute(message(), status);

            assertThat(result.status()).isEqualTo(status);
            verify(transportStepRepository).update(TRANSPORT_STEP_ID, status, 2);
            verify(transportStepRepository, never()).save(any());
            verify(messageRepository).updateStatus(
                MESSAGE_ID, ConnectorBusinessMessageStatus.fromTransportStep(status.name()));
        }

        @Test
        void should_change_nothing_when_the_submitted_step_is_submitted_again() {
            existing(ConnectorMessageTransportStatus.SUBMITTED, 2);

            var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

            assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.SUBMITTED);
            assertThat(result.numberOfAttempts()).isEqualTo(2);
            verify(transportStepRepository, never()).update(any(), any(), anyInt());
            verify(transportStepRepository, never()).save(any());
            verifyNoInteractions(messageRepository);
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_leave_a_delivered_step_as_it_is(ConnectorMessageTransportStatus status) {
            existing(ConnectorMessageTransportStatus.DELIVERED, 2);

            var result = service.execute(message(), status);

            assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.DELIVERED);
            assertThat(result.numberOfAttempts()).isEqualTo(2);
            verify(transportStepRepository, never()).update(any(), any(), anyInt());
            verify(transportStepRepository, never()).save(any());
            verifyNoInteractions(messageRepository);
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_leave_a_pulled_step_as_it_is(ConnectorMessageTransportStatus status) {
            existing(ConnectorMessageTransportStatus.PULLED, 1);

            var result = service.execute(message(), status);

            assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.PULLED);
            verify(transportStepRepository, never()).update(any(), any(), anyInt());
            verify(transportStepRepository, never()).save(any());
            verifyNoInteractions(messageRepository);
        }

        @Test
        void should_never_create_a_second_step_for_a_message() {
            for (var existingStatus : ConnectorMessageTransportStatus.values()) {
                existing(existingStatus, 1);
                lenient().when(transportStepRepository.update(any(), any(), anyInt()))
                    .thenReturn(TransportStepFixtures.createTransportStepWithBusinessMessage());

                service.execute(message(), ConnectorMessageTransportStatus.FAILED);
            }

            verify(transportStepRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThatThrownBy(() -> service.execute(
                null,
                ConnectorMessageTransportStatus.READY_FOR_PULL
            )).isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                transportStepRepository,
                processingConfigurationProvider,
                messageRepository
            );
        }

        @Test
        void should_fail_when_the_status_is_null() {
            assertThatThrownBy(() -> service.execute(message(), null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                transportStepRepository,
                processingConfigurationProvider,
                messageRepository
            );
        }

        @ParameterizedTest
        @EnumSource(
            value = ConnectorMessageTransportStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"SUBMITTED", "DELIVERED", "FAILED", "READY_FOR_PULL"}
        )
        void should_fail_when_the_status_is_not_allowed_for_registration(
            ConnectorMessageTransportStatus status) {
            var message = message();

            assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(message, status)
            );

            verifyNoInteractions(
                transportStepRepository,
                processingConfigurationProvider,
                messageRepository
            );
        }
    }
}
