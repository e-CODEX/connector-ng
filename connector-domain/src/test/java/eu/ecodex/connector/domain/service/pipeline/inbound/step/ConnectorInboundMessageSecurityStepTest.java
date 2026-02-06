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
import static org.mockito.Mockito.doNothing;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageSecurityStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageSecurityStepTest {
    @Mock
    private ConnectorSecurityToolkit securityToolkit;
    private ConnectorMessageStep inboundMessageSecurityStep;

    @BeforeEach
    void setUp() {
        inboundMessageSecurityStep = new ConnectorInboundMessageSecurityStep(securityToolkit);
    }

    @Test
    void should_execute_inbound_message_security_check_successfully() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessage();

        doNothing().when(securityToolkit).validateMessage(any());

        var outputMessage = inboundMessageSecurityStep.execute(inboundMessage);

        assertThat(outputMessage).isEqualTo(inboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> inboundMessageSecurityStep.execute(null)
        );
    }
}
