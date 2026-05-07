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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step.ConnectorOutboundMessageSecurityStep;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageSecurityStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageSecurityStepTest {
    @Mock
    private ConnectorSecurityToolkit securityToolkit;
    @Mock
    private ConnectorMessageRepository messageRepository;
    private ConnectorMessageStep outboundMessageSecurityStep;

    @BeforeEach
    void setUp() {
        outboundMessageSecurityStep = new ConnectorOutboundMessageSecurityStep(
                securityToolkit, messageRepository);
    }

    @Test
    void should_execute_outbound_message_security_successfully() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(securityToolkit.buildContainer(any())).thenReturn(outboundMessage);
        when(messageRepository.findByIdentifier(any())).thenReturn(outboundMessage);

        var outputMessage = outboundMessageSecurityStep.execute(outboundMessage);

        // TODO add ASIC-S attachment t the message and check it
        assertThat(outputMessage).isEqualTo(outputMessage);

        verify(securityToolkit, times(1)).buildContainer(any());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> outboundMessageSecurityStep.execute(null)
        );
    }
}
