/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.service.impl.message.ConnectorOutboundMessageProcessorService;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageVerifier;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageProcessorServiceTest {
    @Mock
    private ConnectorEventPublisher messageStagingEventPublisher;
    @Mock
    private ConnectorMessageIdGenerator messageIdGenerator;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider messageProcessingConfigurationProvider;
    @Mock
    private ConnectorMessageVerifier messageVerifier;
    @InjectMocks
    private ConnectorOutboundMessageProcessorService messageStagingService;

    @Test
    void should_submit_outbound_message_to_staging_queue_successfully() {
        var generatedIdentifier = "28c86f29-5953-42d5-8336-1a03f7e86951@eu.ecodex.connector";
        when(messageIdGenerator.generateIdentifier()).thenReturn(generatedIdentifier);
        when(messageProcessingConfigurationProvider.getConfiguration())
                .thenReturn(
                        ConnectorMessageProcessingConfiguration
                                .builder()
                                .outboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                                .build()
                );
        doNothing().when(messageVerifier).verify(any(), any());
        doNothing().when(messageStagingEventPublisher).publish(any());

        var outboundMessage = MessageTestFixtures.createValidOutboundStagingBusinessMessage();

        var message = messageStagingService.process(outboundMessage);

        assertThat(outboundMessage.identifier()).isNull();
        assertThat(message.identifier()).isNotNull();
        assertThat(message.identifier()).isEqualTo(generatedIdentifier);

    }

    @Test
    void should_throw_null_pointer_exception_when_staging_outbound_message_if_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> messageStagingService.process(null)
        );
    }
}
