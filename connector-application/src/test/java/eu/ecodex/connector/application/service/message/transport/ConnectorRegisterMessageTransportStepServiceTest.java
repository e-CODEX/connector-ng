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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRegisterMessageTransportStepService")
public class ConnectorRegisterMessageTransportStepServiceTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String BACKEND_NAME = "backend-a";
    private static final String TRANSPORT_SUFFIX = "gateway.example.com";

    @Mock
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    @InjectMocks
    private ConnectorRegisterMessageTransportStepService service;

    private ConnectorBusinessMessage message() {
        return BusinessMessageTestFixtures.createInboundMessage().toBuilder()
                                          .identifier(MESSAGE_ID)
                                          .backendName(BACKEND_NAME)
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
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThatThrownBy(() -> service.execute(
                null,
                ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD
            )).isInstanceOf(NullPointerException.class);

            verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
        }

        @Test
        void should_fail_when_the_status_is_null() {
            assertThatThrownBy(() -> service.execute(message(), null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
        }

        @Test
        void should_fail_when_the_status_is_not_allowed_for_registration() {
            var message = BusinessMessageTestFixtures.createInboundMessage();

            assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(message, ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD)
            );

            verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
        }
    }
}
