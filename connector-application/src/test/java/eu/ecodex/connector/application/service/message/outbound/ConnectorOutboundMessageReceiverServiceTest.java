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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorMessageException;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorVerifyTriggeredEvidence;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)

@DisplayName("ConnectorOutboundMessageReceiverService")
public class ConnectorOutboundMessageReceiverServiceTest {
    private static final String MESSAGE_ID =
        "28c86f29-5953-42d5-8336-1a03f7e86951@eu.ecodex.connector";

    @Mock
    private ConnectorEventPublisher evidenceTriggerEventPublisher;
    @Mock
    private ConnectorEventPublisher stagingEventPublisher;
    @Mock
    private ConnectorMessageIdGenerator messageIdGenerator;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider messageProcessingConfigurationProvider;
    @Mock
    private ConnectorMessageVerifier messageVerifier;
    @Mock
    private ConnectorBusinessDomainVerifier businessDomainVerifier;
    @Mock
    private ConnectorVerifyTriggeredEvidence verifyTriggeredEvidenceService;

    private ConnectorOutboundMessageReceiver messageReceiverService;

    @BeforeEach
    void setUp() {
        messageReceiverService = new ConnectorOutboundMessageReceiverService(
            messageProcessingConfigurationProvider,
            messageVerifier,
            stagingEventPublisher,
            evidenceTriggerEventPublisher,
            messageIdGenerator,
            businessDomainVerifier,
            verifyTriggeredEvidenceService
        );
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> messageReceiverService.execute(null)
            );

            verifyNoInteractions(
                evidenceTriggerEventPublisher,
                stagingEventPublisher,
                verifyTriggeredEvidenceService
            );
        }

        @Test
        void should_fail_when_the_message_is_neither_business_nor_evidence() {
            var outboundMessage = MessageTestFixtures.createEvidenceMessage();

            assertThrows(
                ConnectorMessageException.class,
                () -> messageReceiverService.execute(outboundMessage)
            );

            verifyNoInteractions(
                businessDomainVerifier,
                evidenceTriggerEventPublisher,
                stagingEventPublisher,
                verifyTriggeredEvidenceService
            );
        }
    }

    @Nested
    @DisplayName("when receiving a business message")
    class WhenReceivingABusinessMessage {
        @Test
        void should_fail_when_the_business_domain_is_null() {
            var outboundMessage = MessageTestFixtures.createOutboundStagingBusinessMessage()
                                                     .toBuilder()
                                                     .businessDomainIdentifier(null)
                                                     .build();

            assertThrows(
                IllegalStateException.class,
                () -> messageReceiverService.execute(outboundMessage)
            );

            verifyNoInteractions(
                evidenceTriggerEventPublisher,
                stagingEventPublisher,
                verifyTriggeredEvidenceService
            );
        }

        @Test
        void should_fail_when_the_business_domain_is_not_found() {
            doThrow(ConnectorBusinessDomainNotFoundException.class)
                .when(businessDomainVerifier).execute(any());

            var outboundMessage = MessageTestFixtures.createOutboundStagingBusinessMessage();

            assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> messageReceiverService.execute(outboundMessage)
            );

            verifyNoInteractions(
                evidenceTriggerEventPublisher,
                stagingEventPublisher,
                verifyTriggeredEvidenceService
            );
        }

        @Test
        void should_fail_when_the_business_domain_is_not_enabled() {
            doThrow(ConnectorBusinessDomainNotEnabledException.class)
                .when(businessDomainVerifier).execute(any());

            var outboundMessage = MessageTestFixtures.createOutboundStagingBusinessMessage();

            assertThrows(
                ConnectorBusinessDomainNotEnabledException.class,
                () -> messageReceiverService.execute(outboundMessage)
            );

            verifyNoInteractions(
                evidenceTriggerEventPublisher,
                stagingEventPublisher,
                verifyTriggeredEvidenceService
            );
        }

        @Test
        void should_submit_the_message_to_the_staging_queue() {
            doNothing().when(businessDomainVerifier).execute(any());
            when(messageIdGenerator.generateIdentifier()).thenReturn(MESSAGE_ID);
            when(messageProcessingConfigurationProvider.getConfiguration())
                .thenReturn(
                    ConnectorMessageProcessingConfiguration
                        .builder()
                        .outboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                        .build()
                );
            doNothing().when(messageVerifier).verify(any(), any());

            var outboundMessage = MessageTestFixtures.createOutboundStagingBusinessMessage();

            var message = messageReceiverService.execute(outboundMessage);

            assertThat(outboundMessage.identifier()).isNull();
            assertThat(message.identifier()).isNotNull();
            assertThat(message.identifier()).isEqualTo(MESSAGE_ID);

            verifyNoInteractions(evidenceTriggerEventPublisher);
            verify(stagingEventPublisher).publish(any());
        }
    }

    @Nested
    @DisplayName("when receiving an evidence trigger message")
    class WhenReceivingAnEvidenceTriggerMessage {
        @ParameterizedTest
        @ValueSource(classes = {
            ConnectorEvidenceException.class,
            ConnectorMessageNotFoundException.class
        })
        void should_fail_when_the_triggered_evidence_verification_fails(
            Class<? extends Exception> exceptionClass) {
            doThrow(exceptionClass).when(verifyTriggeredEvidenceService).verify(any());
            when(messageIdGenerator.generateIdentifier()).thenReturn(MESSAGE_ID);

            var outboundMessage = MessageTestFixtures.createEvidenceTriggerMessage();

            assertThrows(exceptionClass, () -> messageReceiverService.execute(outboundMessage));

            verifyNoInteractions(stagingEventPublisher, businessDomainVerifier, messageVerifier);
        }

        @Test
        void should_submit_the_message_to_the_evidence_queue() {
            doNothing().when(verifyTriggeredEvidenceService).verify(any());
            when(messageIdGenerator.generateIdentifier()).thenReturn(MESSAGE_ID);

            var outboundMessage = MessageTestFixtures.createEvidenceTriggerMessage();

            var message = messageReceiverService.execute(outboundMessage);

            assertThat(outboundMessage.identifier()).isNull();
            assertThat(message.identifier()).isNotNull();
            assertThat(message.identifier()).isEqualTo(MESSAGE_ID);

            verifyNoInteractions(
                businessDomainVerifier,
                stagingEventPublisher,
                messageVerifier
            );
            verify(evidenceTriggerEventPublisher).publish(any());
        }
    }
}

