/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.ConnectorDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageGatewayValidationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)

@DisplayName("ConnectorOutboundMessageGatewayNameStep")
public class ConnectorOutboundMessageGatewayNameStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorOutboundMessageGatewayNameStep outboundMessageGatewayNameValidationStep;

    @Nested
    @DisplayName("when executing successfully")
    class WhenExecutingSuccessfully {
        @Test
        void should_assign_the_default_gateway_name_when_it_is_missing() {
            var outboundMessage =
                MessageTestFixtures.createValidOutboundBusinessMessageWithoutGatewayName();
            when(messageRepository.updateGatewayName(any(), any()))
                .thenReturn(outboundMessage.toBuilder()
                                           .gatewayName(ConnectorDefaults.DEFAULT_GATEWAY_NAME)
                                           .build());

            var outputMessage =
                outboundMessageGatewayNameValidationStep.execute(outboundMessage);

            assertThat(outboundMessage.gatewayName()).isNull();
            assertThat(outputMessage.gatewayName()).isNotEmpty();
            assertThat(outputMessage.gatewayName()).isNotEqualTo(outboundMessage.gatewayName());
            assertThat(outputMessage.gatewayName())
                .isEqualTo(ConnectorDefaults.DEFAULT_GATEWAY_NAME);
        }

        @Test
        void should_keep_the_existing_gateway_name_when_it_is_already_set() {
            var outboundMessage = MessageTestFixtures.createOutboundBusinessMessage();

            var outputMessage =
                outboundMessageGatewayNameValidationStep.execute(outboundMessage);

            assertThat(outboundMessage.gatewayName()).isNotEmpty();
            assertThat(outputMessage.gatewayName()).isNotEmpty();
            assertThat(outputMessage.gatewayName()).isEqualTo(outboundMessage.gatewayName());
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> outboundMessageGatewayNameValidationStep.execute(null)
            );
        }

        @Test
        void should_fail_when_the_message_identifier_is_null() {
            var outboundMessage = MessageTestFixtures.createOutboundBusinessMessage()
                                                     .toBuilder()
                                                     .identifier(null)
                                                     .build();

            assertThrows(
                IllegalStateException.class,
                () -> outboundMessageGatewayNameValidationStep.execute(outboundMessage)
            );
        }
    }
}

