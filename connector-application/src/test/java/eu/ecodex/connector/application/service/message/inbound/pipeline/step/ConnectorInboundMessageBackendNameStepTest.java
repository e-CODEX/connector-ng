/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound.pipeline.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.MessageRoutingConfigurationTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.inbound.pipeline.step.ConnectorInboundMessageBackendNameStep;
import eu.ecodex.connector.application.service.usecase.routing.ConnectorMessageRouter;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageBackendValidationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageBackendNameStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageRouter messageRoutingService;

    @InjectMocks
    private ConnectorInboundMessageBackendNameStep backendNameStep;

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_returning_same_message_if_backend_name_is_already_set() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessage();

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNotNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(inboundMessage.backendName());
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();

        verify(messageRepository, times(0)).findByConversationIdentifier(any());
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_parent_conversation_message() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessageWithoutBackendName();
        var parentMessage = MessageTestFixtures.createOutboundBusinessMessage()
                                               .toBuilder()
                                               .backendName("backend_client_link")
                                               .build();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.singletonList(parentMessage));
        when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName("backend_client_link").build()
        );

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo("backend_client_link");
        assertThat(outputMessage.backendName()).isEqualTo(parentMessage.backendName());
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();

        verify(messageRepository, times(1)).findByConversationIdentifier(any());
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_routing_rule() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessageWithoutBackendNameAndConversationIdentifier();

        when(messageRoutingService.getDefaultBackendName(any())).thenReturn(
                ConnectorDefaults.DEFAULT_BACKEND_NAME);
        when(messageRoutingService.isRoutingEnabled(any())).thenReturn(true);

        var rules = MessageRoutingConfigurationTestFixtures
                .getRoutingProperties()
                .businessDomainRouting()
                .get(BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier())
                .backend().rules();

        when(messageRoutingService.getBackendRoutingRule(any())).thenReturn(rules);
        when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName("backend_client_link").build()
        );

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_from_routing_rule_default() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingService.getDefaultBackendName(any())).thenReturn(
                ConnectorDefaults.DEFAULT_BACKEND_NAME);
        when(messageRoutingService.isRoutingEnabled(any())).thenReturn(true);

        var rules = MessageRoutingConfigurationTestFixtures
                .getRoutingProperties()
                .businessDomainRouting()
                .get(BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier())
                .backend().rules();

        when(messageRoutingService.getBackendRoutingRule(any())).thenReturn(rules);
        when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName("backend_connector_test").build()
        );

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo("backend_connector_test");
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_default_backend_name_from_routing_rule() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingService.getDefaultBackendName(any())).thenReturn(
                ConnectorDefaults.DEFAULT_BACKEND_NAME);
        when(messageRoutingService.isRoutingEnabled(any())).thenReturn(true);
        when(messageRoutingService.getBackendRoutingRule(any())).thenReturn(Map.of());
        when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName(
                        ConnectorDefaults.DEFAULT_BACKEND_NAME).build()
        );

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();
    }

    @Test
    void should_execute_inbound_message_backend_name_validation_successfully_by_setting_name_to_default_backend_if_routing_is_disabled() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessageWithoutBackendName();

        when(messageRepository.findByConversationIdentifier(any())).thenReturn(
                Collections.emptyList());
        when(messageRoutingService.getDefaultBackendName(any())).thenReturn(
                ConnectorDefaults.DEFAULT_BACKEND_NAME);
        when(messageRoutingService.isRoutingEnabled(any())).thenReturn(false);
        when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName(
                        ConnectorDefaults.DEFAULT_BACKEND_NAME).build()
        );

        var outputMessage = backendNameStep.execute(inboundMessage);

        assertThat(inboundMessage.backendName()).isNull();
        assertThat(outputMessage.backendName()).isNotEmpty();
        assertThat(outputMessage.backendName()).isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
        assertThat(outputMessage.transportedEvidences()).isNotEmpty();
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> backendNameStep.execute(null)
        );
    }
}
