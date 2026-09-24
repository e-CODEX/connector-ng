/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.AS4PropertiesTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.MessageContentTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeInvalidTruststoreException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorBusinessMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.pmode.ConnectorProcessingModeVerifier;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundBusinessMessageReceiverService")
public class ConnectorOutboundBusinessMessageReceiverServiceTest {
    private static final String MESSAGE_ID =
        "28c86f29-5953-42d5-8336-1a03f7e86951@eu.ecodex.connector";

    @Mock
    private ConnectorBusinessDomainVerifier businessDomainVerifierService;
    @Mock
    private ConnectorProcessingModeVerifier processingModeVerifierService;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider configurationProvider;
    @Mock
    private ConnectorBusinessMessageVerifier messageVerifierService;
    @Mock
    private ConnectorMessageIdGenerator messageIdGeneratorService;
    @Mock
    private ConnectorMessageEventPublisher<ConnectorBusinessMessage> stagingEventPublisher;

    private ConnectorOutboundBusinessMessageReceiver outboundBusinessMessageReceiver;

    @BeforeEach
    void setUp() {
        outboundBusinessMessageReceiver = new ConnectorOutboundBusinessMessageReceiverService(
            configurationProvider,
            messageVerifierService,
            stagingEventPublisher,
            messageIdGeneratorService,
            businessDomainVerifierService,
            processingModeVerifierService
        );
    }

    private ConnectorOutboundBusinessMessageCommand createBusinessMessageCommand() {
        return ConnectorOutboundBusinessMessageCommand
            .builder()
            .businessDomainIdentifier(
                BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                          .identifier()
            )
            .businessContent(MessageContentTestFixtures.createContent())
            .backendMessageIdentifier(
                "85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu")
            .backendName("default_backend")
            .as4Properties(AS4PropertiesTestFixtures.defaultAS4Properties().build())
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .build();
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_command_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> outboundBusinessMessageReceiver.execute(null)
            );

            verifyNoInteractions(
                businessDomainVerifierService,
                processingModeVerifierService,
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                stagingEventPublisher
            );
        }
    }

    @Nested
    @DisplayName("when receiving a business message")
    class WhenReceivingABusinessMessage {
        @Test
        void should_fail_when_the_business_domain_is_not_found() {
            doThrow(ConnectorBusinessDomainNotFoundException.class)
                .when(businessDomainVerifierService).execute(any());

            var outboundMessageCommand = createBusinessMessageCommand();

            assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> outboundBusinessMessageReceiver.execute(outboundMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                processingModeVerifierService,
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                stagingEventPublisher
            );
        }

        @Test
        void should_fail_when_the_business_domain_is_not_enabled() {
            doThrow(ConnectorBusinessDomainNotEnabledException.class)
                .when(businessDomainVerifierService).execute(any());

            var outboundMessageCommand = createBusinessMessageCommand();

            assertThrows(
                ConnectorBusinessDomainNotEnabledException.class,
                () -> outboundBusinessMessageReceiver.execute(outboundMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                processingModeVerifierService,
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                stagingEventPublisher
            );
        }

        @Test
        void should_fail_when_the_processing_mode_is_not_found() {
            doThrow(ConnectorProcessingModeNotFoundException.class)
                .when(processingModeVerifierService).execute(any());

            var outboundMessageCommand = createBusinessMessageCommand();

            assertThrows(
                ConnectorProcessingModeNotFoundException.class,
                () -> outboundBusinessMessageReceiver.execute(outboundMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(processingModeVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                stagingEventPublisher
            );
        }

        @Test
        void should_fail_when_the_processing_mode_truststore_is_invalid() {
            doThrow(ConnectorProcessingModeInvalidTruststoreException.class)
                .when(processingModeVerifierService).execute(any());

            var outboundMessageCommand = createBusinessMessageCommand();

            assertThrows(
                ConnectorProcessingModeInvalidTruststoreException.class,
                () -> outboundBusinessMessageReceiver.execute(outboundMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(processingModeVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                stagingEventPublisher
            );
        }

        @Test
        void should_fail_when_the_message_verification_fails() {
            doNothing().when(businessDomainVerifierService).execute(any());
            doNothing().when(processingModeVerifierService).execute(any());
            when(messageIdGeneratorService.execute()).thenReturn(MESSAGE_ID);
            when(configurationProvider.getConfiguration())
                .thenReturn(
                    ConnectorMessageProcessingConfiguration
                        .builder()
                        .outboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                        .build()
                );
            doThrow(ConnectorProcessingModeVerificationException.class)
                .when(messageVerifierService).verify(any(), any());

            var outboundMessageCommand = createBusinessMessageCommand();

            assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> outboundBusinessMessageReceiver.execute(outboundMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(processingModeVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(messageIdGeneratorService).execute();
            verify(configurationProvider).getConfiguration();
            verify(messageVerifierService).verify(any(), eq(ProcessingModeVerificationMode.STRICT));
            verifyNoInteractions(stagingEventPublisher);
        }

        @Test
        void should_submit_the_message_to_the_staging_queue() {
            doNothing().when(businessDomainVerifierService).execute(any());
            doNothing().when(processingModeVerifierService).execute(any());
            when(messageIdGeneratorService.execute()).thenReturn(MESSAGE_ID);
            when(configurationProvider.getConfiguration())
                .thenReturn(
                    ConnectorMessageProcessingConfiguration
                        .builder()
                        .outboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                        .build()
                );
            doNothing().when(messageVerifierService).verify(any(), any());

            var outboundMessageCommand = createBusinessMessageCommand();

            outboundBusinessMessageReceiver.execute(outboundMessageCommand);

            var messageCaptor = ArgumentCaptor.forClass(ConnectorBusinessMessage.class);
            verify(stagingEventPublisher).publish(messageCaptor.capture());

            var message = messageCaptor.getValue();
            assertThat(message).isNotNull();
            assertThat(message.identifier()).isEqualTo(MESSAGE_ID);
            assertThat(message.businessDomainIdentifier())
                .isEqualTo(outboundMessageCommand.businessDomainIdentifier());
            assertThat(message.backendMessageIdentifier())
                .isEqualTo(outboundMessageCommand.backendMessageIdentifier());
            assertThat(message.referenceToBackendMessageIdentifier())
                .isEqualTo(outboundMessageCommand.referenceToBackendMessageIdentifier());
            assertThat(message.backendName()).isEqualTo(outboundMessageCommand.backendName());
            assertThat(message.as4Properties()).isEqualTo(outboundMessageCommand.as4Properties());
            assertThat(message.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
            assertThat(message.businessContent())
                .isEqualTo(outboundMessageCommand.businessContent());
            assertThat(message.attachments()).isEqualTo(outboundMessageCommand.attachments());

            verify(businessDomainVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(processingModeVerifierService)
                .execute(outboundMessageCommand.businessDomainIdentifier());
            verify(messageVerifierService)
                .verify(message, ProcessingModeVerificationMode.STRICT);
        }
    }
}

