/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageRoutingConfigProviderTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.service.ConnectorMessageRoutingServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorMessageServiceImpl;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageRoutingConfigProvider;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageBackendValidationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageBackendValidationStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;
    @Mock
    private ConnectorMessageRoutingConfigProvider messageRoutingConfigProvider;

    private ConnectorMessageStep inboundMessageBackendNameValidationStep;

    @BeforeEach
    void setUp() {
        var messageService = new ConnectorMessageServiceImpl(
                messageRepository, messageProcessingConfigProvider
        );
        var messageRoutingService = new ConnectorMessageRoutingServiceImpl(
                messageRoutingConfigProvider
        );
        inboundMessageBackendNameValidationStep = new ConnectorInboundMessageBackendValidationStep(
                messageService, messageRoutingService
        );
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_returning_same_message_if_backend_name_is_already_set() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessage();

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNotNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(inboundMessage.backendName());

        verify(messageRepository, times(0)).findByConversationIdentifier(any());
        verify(messageRoutingConfigProvider, times(0)).getRoutingProperties();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_parent_conversation_message() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendName();
        var parentMessage = MessageTestFixtures.createValidOutboundBusinessMessage()
                                       .toBuilder()
                                       .backendName("backend_client_link")
                                       .build();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.singletonList(parentMessage));

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo("backend_client_link");
        assertThat(outputMessage.backendName()).isEqualTo(parentMessage.backendName());

        verify(messageRepository, times(1)).findByConversationIdentifier(any());
        verify(messageRoutingConfigProvider, times(0)).getRoutingProperties();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_routing_rule() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendNameAndConversationIdentifier();

        when(messageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderTestFixtures.getRoutingProperties());

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_routing_rule_default() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(MessageRoutingConfigProviderTestFixtures.getRoutingProperties());

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo("backend_connector_test");
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_default_backend_name_from_routing_rule() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(
                        MessageRoutingConfigProviderTestFixtures.getRoutingPropertiesWithNoDefaultBackendRules()
                );

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_to_default_backend_if_routing_is_disabled() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingConfigProvider.getRoutingProperties())
                .thenReturn(
                        MessageRoutingConfigProviderTestFixtures.getDisabledRoutingProperties()
                );

        var outputMessage = inboundMessageBackendNameValidationStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> inboundMessageBackendNameValidationStep.execute(null)
        );
    }
}
