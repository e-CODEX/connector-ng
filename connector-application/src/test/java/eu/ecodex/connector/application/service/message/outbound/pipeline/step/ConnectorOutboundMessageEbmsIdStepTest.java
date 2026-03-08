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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageEbmsIdGenerator;
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step.ConnectorOutboundMessageEbmsIdStep;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageEbmsIdStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageEbmsIdStepTest {
    @Mock
    private ConnectorMessageEbmsIdGenerator messageEbmsIdGenerator;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    private ConnectorMessageStep outboundMessageEbmsIdCreationStep;

    @BeforeEach
    void setUp() {
        outboundMessageEbmsIdCreationStep = new ConnectorOutboundMessageEbmsIdStep(
                messageEbmsIdGenerator,
                messageRepository,
                processingConfigurationProvider
        );
    }

    @Test
    void should_execute_outbound_message_and_set_ebms_id_successfully() {
        var configuration = ConnectorMessageProcessingConfiguration
                .builder()
                .ebmsIdGeneratorEnabled(true)
                .ebmsIdSuffix("connector.ecodex.eu")
                .build();
        var ebmsIdentifier = String.format("%s@%s", UUID.randomUUID(), configuration);
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4Properties = outboundMessage.as4Properties();
        as4Properties = as4Properties.toBuilder().ebmsMessageIdentifier(ebmsIdentifier).build();

        when(messageEbmsIdGenerator.generateIdentifier()).thenReturn(ebmsIdentifier);
        when(messageRepository.updateEbmsIdentifier(any(), any()))
                .thenReturn(outboundMessage.toBuilder().as4Properties(as4Properties).build());
        when(processingConfigurationProvider.getConfiguration()).thenReturn(configuration);

        var outputMessage = outboundMessageEbmsIdCreationStep.execute(outboundMessage);

        assertThat(outboundMessage.as4Properties().ebmsMessageIdentifier()).isNull();
        assertThat(outputMessage.as4Properties().ebmsMessageIdentifier()).isNotEmpty();

        assertThat(outputMessage.as4Properties().ebmsMessageIdentifier())
                .contains(configuration.ebmsIdSuffix());
    }

    @Test
    void should_execute_outbound_message_and_not_set_ebms_id_if_disabled() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(processingConfigurationProvider.getConfiguration())
                .thenReturn(
                        ConnectorMessageProcessingConfiguration
                                .builder()
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
