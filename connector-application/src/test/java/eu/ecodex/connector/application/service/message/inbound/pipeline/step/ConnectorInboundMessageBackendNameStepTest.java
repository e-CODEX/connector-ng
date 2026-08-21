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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageRoutingConfigurationTestFixtures;
import eu.ecodex.connector.application.port.api.routing.ConnectorMessageRouter;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.ConnectorDefaults;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("ConnectorInboundMessageBackendNameStep")
public class ConnectorInboundMessageBackendNameStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageRouter messageRoutingService;

    @InjectMocks
    private ConnectorInboundMessageBackendNameStep backendNameStep;

    @Nested
    @DisplayName("when resolving the backend name")
    class WhenResolvingTheBackendName {
        @Test
        void should_keep_the_existing_backend_name_when_it_is_already_set() {
            var inboundMessage = BusinessMessageTestFixtures.createInboundMessage();

            var outputMessage = backendNameStep.execute(inboundMessage);

            assertThat(inboundMessage.backendName()).isNotNull();
            assertThat(outputMessage.backendName()).isNotEmpty();
            assertThat(outputMessage.backendName()).isEqualTo(inboundMessage.backendName());
            assertThat(outputMessage.transportedEvidences()).isNotEmpty();

            verify(messageRepository, never()).findByConversationIdentifier(any());
        }

        @Test
        void should_resolve_it_from_the_parent_conversation_message() {
            var inboundMessage =
                BusinessMessageTestFixtures.businessMessageWithoutBackendName();
            var parentMessage = BusinessMessageTestFixtures.createOutboundMessage()
                                                           .toBuilder()
                                                           .backendName("backend_client_link")
                                                           .build();

            when(messageRepository.findByConversationIdentifier(any()))
                .thenReturn(Collections.singletonList(parentMessage));
            when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder().backendName("backend_client_link").build()
            );

            var outputMessage = backendNameStep.execute(inboundMessage);

            assertThat(inboundMessage.backendName()).isNull();
            assertThat(outputMessage.backendName()).isNotEmpty();
            assertThat(outputMessage.backendName()).isEqualTo("backend_client_link");
            assertThat(outputMessage.backendName()).isEqualTo(parentMessage.backendName());
            assertThat(outputMessage.transportedEvidences()).isNotEmpty();

            verify(messageRepository).findByConversationIdentifier(any());
        }

        @Test
        void should_resolve_it_from_the_routing_rule_when_there_is_no_conversation_identifier() {
            var inboundMessage = BusinessMessageTestFixtures
                .createInboundMessageWithoutBackendNameAndConversationIdentifier();

            when(messageRoutingService.getDefaultBackendName(any()))
                .thenReturn(ConnectorDefaults.DEFAULT_BACKEND_NAME);
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
        void should_resolve_it_from_the_routing_rule_when_the_parent_conversation_is_empty() {
            var inboundMessage =
                BusinessMessageTestFixtures.businessMessageWithoutBackendName();

            when(messageRepository.findByConversationIdentifier(any()))
                .thenReturn(Collections.emptyList());
            when(messageRoutingService.getDefaultBackendName(any()))
                .thenReturn(ConnectorDefaults.DEFAULT_BACKEND_NAME);
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
        void should_fall_back_to_the_default_when_no_routing_rule_matches() {
            var inboundMessage =
                BusinessMessageTestFixtures.businessMessageWithoutBackendName();

            when(messageRepository.findByConversationIdentifier(any()))
                .thenReturn(Collections.emptyList());
            when(messageRoutingService.getDefaultBackendName(any()))
                .thenReturn(ConnectorDefaults.DEFAULT_BACKEND_NAME);
            when(messageRoutingService.isRoutingEnabled(any())).thenReturn(true);
            when(messageRoutingService.getBackendRoutingRule(any())).thenReturn(Map.of());
            when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder()
                              .backendName(ConnectorDefaults.DEFAULT_BACKEND_NAME)
                              .build()
            );

            var outputMessage = backendNameStep.execute(inboundMessage);

            assertThat(inboundMessage.backendName()).isNull();
            assertThat(outputMessage.backendName()).isNotEmpty();
            assertThat(outputMessage.backendName())
                .isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
            assertThat(outputMessage.transportedEvidences()).isNotEmpty();
        }

        @Test
        void should_fall_back_to_the_default_when_routing_is_disabled() {
            var inboundMessage =
                BusinessMessageTestFixtures.businessMessageWithoutBackendName();

            when(messageRepository.findByConversationIdentifier(any()))
                .thenReturn(Collections.emptyList());
            when(messageRoutingService.getDefaultBackendName(any()))
                .thenReturn(ConnectorDefaults.DEFAULT_BACKEND_NAME);
            when(messageRoutingService.isRoutingEnabled(any())).thenReturn(false);
            when(messageRepository.updateBackendName(any(), any())).thenReturn(
                inboundMessage.toBuilder()
                              .backendName(ConnectorDefaults.DEFAULT_BACKEND_NAME)
                              .build()
            );

            var outputMessage = backendNameStep.execute(inboundMessage);

            assertThat(inboundMessage.backendName()).isNull();
            assertThat(outputMessage.backendName()).isNotEmpty();
            assertThat(outputMessage.backendName())
                .isEqualTo(ConnectorDefaults.DEFAULT_BACKEND_NAME);
            assertThat(outputMessage.transportedEvidences()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> backendNameStep.execute(null)
            );
        }
    }
}

