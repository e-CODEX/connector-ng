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

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.message.transport.ConnectorRegisterMessageTransportStepService;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
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

    @Test
    void should_throw_exception_if_message_is_null() {
        assertThatThrownBy(() -> service.execute(null, ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
    }

    @Test
    void should_throw_exception_if_status_is_null() {
        assertThatThrownBy(() -> service.execute(message(), null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
    }

    @Test
    void should_throw_exception_if_message_identifier_is_null() {
        var message = ConnectorMessage.builder().identifier(null).build();

        assertThatThrownBy(() -> service.execute(
                message,
                ConnectorMessageTransportStatus.SUBMITTED
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message identifier must not be null");

        verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
    }

    @Test
    void should_throw_exception_if_status_for_registration_is_not_allowed() {
        var message = ConnectorMessage.builder().identifier(null).build();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(message, ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD)
        );

        verifyNoInteractions(transportStepRepository, processingConfigurationProvider);
    }

    @Test
    void should_register_new_message_transport_step_successfully() {
        when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration());
        when(transportStepRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.execute(message(), ConnectorMessageTransportStatus.SUBMITTED);

        assertThat(result.numberOfAttempts()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(ConnectorMessageTransportStatus.SUBMITTED);

        verify(transportStepRepository).save(
                argThat(
                        step ->
                                step.numberOfAttempts() == 1
                                        && step.status() == ConnectorMessageTransportStatus.SUBMITTED
                ));
    }

    private ConnectorMessage message() {
        return ConnectorMessage.builder()
                               .identifier(MESSAGE_ID)
                               .backendName(BACKEND_NAME)
                               .build();
    }

    private ConnectorMessageProcessingConfiguration configuration() {
        return ConnectorMessageProcessingConfiguration.builder()
                                                      .transportIdSuffix(TRANSPORT_SUFFIX)
                                                      .build();
    }
}
