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
import static org.mockito.Mockito.doNothing;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.port.api.message.ConnectorMessagePartiesVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageValidationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageValidationStepTest {
    @Mock
    private ConnectorMessagePartiesVerifier partiesVerifierService;

    @InjectMocks
    private ConnectorOutboundMessageValidationStep outboundMessageValidationStep;

    @Test
    void should_execute_outbound_message_validation_successfully() {
        var outboundMessage = MessageTestFixtures.createOutboundBusinessMessage();

        doNothing().when(partiesVerifierService).verify(any());

        var outputMessage = outboundMessageValidationStep.execute(outboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage).isEqualTo(outboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
            NullPointerException.class, () -> outboundMessageValidationStep.execute(null)
        );
    }
}
