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
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step.ConnectorOutboundMessageGatewayNameStep;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageGatewayValidationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageGatewayNameStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    private ConnectorMessageStep outboundMessageGatewayNameValidationStep;

    @BeforeEach
    void setUp() {
        outboundMessageGatewayNameValidationStep = new ConnectorOutboundMessageGatewayNameStep(
                messageRepository
        );
    }

    @Test
    void should_execute_outbound_message_gateway_name_validation_successfully() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessageWithoutGatewayName();

        when(messageRepository.updateGatewayName(any(), any()))
                .thenReturn(outboundMessage.toBuilder().gatewayName(ConnectorDefaults.DEFAULT_GATEWAY_NAME).build());

        var outputMessage = outboundMessageGatewayNameValidationStep.execute(outboundMessage);

        assertThat(outboundMessage.gatewayName()).isNull();
        assertThat(outputMessage.gatewayName()).isNotEmpty();
        assertThat(outputMessage.gatewayName()).isNotEqualTo(outboundMessage.gatewayName());
        assertThat(outputMessage.gatewayName()).isEqualTo(ConnectorDefaults.DEFAULT_GATEWAY_NAME);
    }

    @Test
    void should_execute_outbound_message_gateway_name_validation_successfully_by_sending_back_message_if_name_already_set() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        var outputMessage = outboundMessageGatewayNameValidationStep.execute(outboundMessage);

        assertThat(outboundMessage.gatewayName()).isNotEmpty();
        assertThat(outputMessage.gatewayName()).isNotEmpty();
        assertThat(outputMessage.gatewayName()).isEqualTo(outboundMessage.gatewayName());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> outboundMessageGatewayNameValidationStep.execute(null)
        );
    }
}
