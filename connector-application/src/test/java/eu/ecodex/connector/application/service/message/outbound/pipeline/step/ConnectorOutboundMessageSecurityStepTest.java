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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorSecurityToolkit;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageSecurityStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundMessageSecurityStep")
public class ConnectorOutboundMessageSecurityStepTest {
    @Mock
    private ConnectorSecurityToolkit securityToolkit;
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorOutboundMessageSecurityStep outboundMessageSecurityStep;

    @Nested
    @DisplayName("when executing successfully")
    class WhenExecutingSuccessfully {
        @Test
        void should_build_the_security_container() {
            var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage();
            when(securityToolkit.buildContainer(any())).thenReturn(outboundMessage);
            when(messageRepository.findByIdentifier(any())).thenReturn(outboundMessage);

            var outputMessage = outboundMessageSecurityStep.execute(outboundMessage);

            // TODO add ASIC-S attachment to the message and assert on it
            assertThat(outputMessage).isEqualTo(outboundMessage);
            verify(securityToolkit, times(1)).buildContainer(any());
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> outboundMessageSecurityStep.execute(null)
            );
        }
    }
}

