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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageProcessingConfigProviderTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.service.ConnectorMessageServiceImpl;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageEbmsIdStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageEbmsIdStepTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    private ConnectorMessageStep outboundMessageEbmsIdCreationStep;

    @BeforeEach
    void setUp() {
        var messageService = new ConnectorMessageServiceImpl(
                messageRepository, messageProcessingConfigProvider
        );
        outboundMessageEbmsIdCreationStep = new ConnectorOutboundMessageEbmsIdStep(
                messageService,
                messageProcessingConfigProvider
        );
    }

    @Test
    void should_execute_outbound_message_and_set_ebms_id_successfully() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(MessageProcessingConfigProviderTestFixtures.getProcessingProperties());

        var outputMessage = outboundMessageEbmsIdCreationStep.execute(outboundMessage);

        assertThat(outboundMessage.as4Properties().ebmsMessageIdentifier()).isNull();
        assertThat(outputMessage.as4Properties().ebmsMessageIdentifier()).isNotEmpty();
        var processingProperties = messageProcessingConfigProvider.getProcessingProperties();
        assertThat(outputMessage.as4Properties().ebmsMessageIdentifier())
                .contains(processingProperties.ebmsIdSuffix());
    }

    @Test
    void should_execute_outbound_message_and_not_set_ebms_id_if_disabled() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(
                        MessageProcessingConfigProviderTestFixtures.getProcessingProperties()
                                .toBuilder()
                                .ebmsIdGeneratorEnabled(false)
                                .build()
                );

        var outputMessage = outboundMessageEbmsIdCreationStep.execute(outboundMessage);

        assertThat(outboundMessage.as4Properties().ebmsMessageIdentifier()).isNull();
        assertThat(outputMessage.as4Properties().ebmsMessageIdentifier()).isNull();
        assertThat(outputMessage).isEqualTo(outboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> outboundMessageEbmsIdCreationStep.execute(null)
        );
    }
}
