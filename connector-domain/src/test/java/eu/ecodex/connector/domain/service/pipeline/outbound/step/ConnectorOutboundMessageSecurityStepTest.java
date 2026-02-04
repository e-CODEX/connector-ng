/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageSecurityStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageSecurityStepTest {
    @Mock
    private ConnectorSecurityToolkit securityToolkit;
    private ConnectorMessageStep outboundMessageSecurityStep;

    @BeforeEach
    void setUp() {
        outboundMessageSecurityStep = new ConnectorOutboundMessageSecurityStep(securityToolkit);
    }

    @Test
    void should_execute_outbound_message_security_successfully() {
        var outboundMessage = MessageUtil.createValidOutboundBusinessMessage();

        when(securityToolkit.buildContainer(any())).thenReturn(outboundMessage);

        var outputMessage = outboundMessageSecurityStep.execute(outboundMessage);

        // TODO add ASIC-S attachment t the message and check it
        assertThat(outputMessage).isEqualTo(outputMessage);

        verify(securityToolkit, times(1)).buildContainer(outboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> outboundMessageSecurityStep.execute(null)
        );
    }
}
