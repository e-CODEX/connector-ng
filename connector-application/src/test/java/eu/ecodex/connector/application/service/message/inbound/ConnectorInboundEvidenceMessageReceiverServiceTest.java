/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound;

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
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorBusinessMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import java.util.List;
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
@DisplayName("ConnectorInboundEvidenceMessageReceiverService")
public class ConnectorInboundEvidenceMessageReceiverServiceTest {
    private static final String MESSAGE_ID =
        "28c86f29-5953-42d5-8336-1a03f7e86951@eu.ecodex.connector";

    @Mock
    private ConnectorBusinessDomainVerifier businessDomainVerifierService;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider configurationProvider;
    @Mock
    private ConnectorBusinessMessageVerifier messageVerifierService;
    @Mock
    private ConnectorMessageIdGenerator messageIdGeneratorService;
    @Mock
    private ConnectorMessageEventPublisher<ConnectorEvidenceMessage> inboundEvidenceTriggerPublisher;

    private ConnectorInboundEvidenceMessageReceiver inboundEvidenceMessageReceiver;

    @BeforeEach
    void setUp() {
        inboundEvidenceMessageReceiver = new ConnectorInboundEvidenceMessageReceiverService(
            businessDomainVerifierService,
            configurationProvider,
            messageVerifierService,
            messageIdGeneratorService,
            inboundEvidenceTriggerPublisher
        );
    }

    private ConnectorInboundEvidenceMessageCommand createInboundEvidenceMessageCommand() {
        return ConnectorInboundEvidenceMessageCommand
            .builder()
            .businessDomainIdentifier(
                BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                          .identifier()
            )
            .gatewayName("default_gateway")
            .as4Properties(AS4PropertiesTestFixtures.defaultAS4Properties().build())
            .transportedEvidences(List.of(EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence()))
            .build();
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_command_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> inboundEvidenceMessageReceiver.execute(null)
            );

            verifyNoInteractions(
                businessDomainVerifierService,
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                inboundEvidenceTriggerPublisher
            );
        }
    }

    @Nested
    @DisplayName("when receiving an inbound evidence message")
    class WhenReceivingAnInboundEvidenceMessage {
        @Test
        void should_fail_when_the_business_domain_is_not_found() {
            doThrow(ConnectorBusinessDomainNotFoundException.class)
                .when(businessDomainVerifierService).execute(any());

            var inboundEvidenceMessageCommand = createInboundEvidenceMessageCommand();

            assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> inboundEvidenceMessageReceiver.execute(inboundEvidenceMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(inboundEvidenceMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                inboundEvidenceTriggerPublisher
            );
        }

        @Test
        void should_fail_when_the_business_domain_is_not_enabled() {
            doThrow(ConnectorBusinessDomainNotEnabledException.class)
                .when(businessDomainVerifierService).execute(any());

            var inboundEvidenceMessageCommand = createInboundEvidenceMessageCommand();

            assertThrows(
                ConnectorBusinessDomainNotEnabledException.class,
                () -> inboundEvidenceMessageReceiver.execute(inboundEvidenceMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(inboundEvidenceMessageCommand.businessDomainIdentifier());
            verifyNoInteractions(
                configurationProvider,
                messageVerifierService,
                messageIdGeneratorService,
                inboundEvidenceTriggerPublisher
            );
        }

        @Test
        void should_fail_when_the_message_verification_fails() {
            doNothing().when(businessDomainVerifierService).execute(any());
            when(messageIdGeneratorService.execute()).thenReturn(MESSAGE_ID);
            when(configurationProvider.getConfiguration())
                .thenReturn(
                    ConnectorMessageProcessingConfiguration
                        .builder()
                        .inboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                        .build()
                );
            doThrow(ConnectorProcessingModeVerificationException.class)
                .when(messageVerifierService).verify(any(), any());

            var inboundEvidenceMessageCommand = createInboundEvidenceMessageCommand();

            assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> inboundEvidenceMessageReceiver.execute(inboundEvidenceMessageCommand)
            );

            verify(businessDomainVerifierService)
                .execute(inboundEvidenceMessageCommand.businessDomainIdentifier());
            verify(messageIdGeneratorService).execute();
            verify(configurationProvider).getConfiguration();
            verify(messageVerifierService).verify(any(), eq(ProcessingModeVerificationMode.STRICT));
            verifyNoInteractions(inboundEvidenceTriggerPublisher);
        }

        @Test
        void should_submit_the_message_to_the_inbound_evidence_trigger_publisher() {
            doNothing().when(businessDomainVerifierService).execute(any());
            when(messageIdGeneratorService.execute()).thenReturn(MESSAGE_ID);
            when(configurationProvider.getConfiguration())
                .thenReturn(
                    ConnectorMessageProcessingConfiguration
                        .builder()
                        .inboundMessageVerificationMode(ProcessingModeVerificationMode.STRICT)
                        .build()
                );
            doNothing().when(messageVerifierService).verify(any(), any());

            var inboundEvidenceMessageCommand = createInboundEvidenceMessageCommand();

            inboundEvidenceMessageReceiver.execute(inboundEvidenceMessageCommand);

            var messageCaptor = ArgumentCaptor.forClass(ConnectorEvidenceMessage.class);
            verify(inboundEvidenceTriggerPublisher).publish(messageCaptor.capture());

            var message = messageCaptor.getValue();
            assertThat(message).isNotNull();
            assertThat(message.identifier()).isEqualTo(MESSAGE_ID);
            assertThat(message.businessDomainIdentifier())
                .isEqualTo(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID);
            assertThat(message.gatewayName()).isEqualTo(ConnectorDefaults.DEFAULT_GATEWAY_NAME);
            assertThat(message.as4Properties())
                .isEqualTo(inboundEvidenceMessageCommand.as4Properties());
            assertThat(message.direction())
                .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
            assertThat(message.transportedEvidences())
                .isEqualTo(inboundEvidenceMessageCommand.transportedEvidences());

            verify(businessDomainVerifierService)
                .execute(inboundEvidenceMessageCommand.businessDomainIdentifier());
            verify(messageVerifierService)
                .verify(message, ProcessingModeVerificationMode.STRICT);
        }
    }
}
