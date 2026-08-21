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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.TriggeredEvidenceMessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorEvidenceMessageCreatorService")
public class ConnectorEvidenceMessageCreatorServiceTest {
    private static final String IDENTIFIER = "d040fe80-55a6-4d51-85de-9e16280eb503@connector"
        + ".ecodex.eu";

    @Mock
    private ConnectorMessageIdGeneratorService messageIdGenerator;
    @Mock
    private ConnectorMessageEbmsIdGeneratorService messageEbmsIdGenerator;

    @InjectMocks
    private ConnectorEvidenceMessageCreatorService evidenceMessageCreator;

    @Nested
    @DisplayName("create evidence message")
    class CreateEvidenceMessage {
        private static Stream<ConnectorMessageEvidence> provideEvidence() {
            return Stream.of(
                EvidenceTestFixtures.createSubmissionAcceptanceEvidence(),
                EvidenceTestFixtures.createSubmissionRejectionEvidence(),
                EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence(),
                EvidenceTestFixtures.createRelayREMMDRejectionEvidence(),
                EvidenceTestFixtures.createRelayREMMDFailureEvidence(),
                EvidenceTestFixtures.createDeliveryEvidence(),
                EvidenceTestFixtures.createNonDeliveryEvidence(),
                EvidenceTestFixtures.createRetrievalEvidence(),
                EvidenceTestFixtures.createNonRetrievalEvidence()
            );
        }

        static Stream<Arguments> nullCases() {
            var message = BusinessMessageTestFixtures.createOutboundMessage();
            var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
            return Stream.of(
                arguments(null, null),
                arguments(null, evidence),
                arguments(message, null)
            );
        }

        @ParameterizedTest
        @MethodSource("provideEvidence")
        void should_create_evidence_message_successfully(ConnectorMessageEvidence evidence) {
            when(messageIdGenerator.generateIdentifier()).thenReturn(IDENTIFIER);
            lenient().when(messageEbmsIdGenerator.generateIdentifier())
                     .thenReturn("62705399-0793-485e-bc48-9f0a49bd9ba3@connector.ecodex.eu");

            var message = BusinessMessageTestFixtures.createOutboundMessage();
            var as4Properties = message.as4Properties();
            var action = ConnectorEvidenceMessageCreatorService.getEvidenceAction(evidence.type());
            as4Properties = as4Properties.toBuilder().action(action).build();
            message = message.toBuilder().as4Properties(as4Properties).build();

            var evidenceMessage = evidenceMessageCreator.create(message, evidence);

            assertThat(evidenceMessage).isNotNull();
            assertThat(evidenceMessage.identifier())
                .isNotEqualTo(message.identifier());
            assertThat(evidenceMessage.as4Properties().action().name())
                .isEqualTo(
                    ConnectorEvidenceMessageCreatorService.getEvidenceAction(
                        evidence.type()).name());
            assertThat(evidenceMessage.as4Properties().service()).isEqualTo(
                message.as4Properties().service());
            assertThat(evidenceMessage.referenceToBackendMessageIdentifier())
                .isEqualTo(message.backendMessageIdentifier());
            assertThat(evidenceMessage.as4Properties()
                                      .referenceToIdentifier()).isIn(
                message.as4Properties().ebmsMessageIdentifier(),
                message.backendMessageIdentifier()
            );
            assertThat(evidenceMessage.as4Properties()
                                      .ebmsMessageIdentifier()).isEqualTo(
                message.as4Properties().ebmsMessageIdentifier());
            assertThat(evidenceMessage.as4Properties()
                                      .conversationIdentifier()).isEqualTo(
                message.as4Properties().conversationIdentifier());
            assertThat(evidenceMessage.as4Properties().fromParty())
                .isEqualTo(
                    message.as4Properties().fromParty());
            assertThat(evidenceMessage.as4Properties().toParty()).isEqualTo(
                message.as4Properties().toParty());
            assertThat(evidenceMessage.as4Properties().finalRecipient())
                .isEqualTo(message.as4Properties().finalRecipient());
            assertThat(evidenceMessage.as4Properties().originalSender())
                .isEqualTo(message.as4Properties().originalSender());
            assertThat(evidenceMessage.gatewayName()).isEqualTo(message.gatewayName());
            assertThat(evidenceMessage.backendName()).isEqualTo(message.backendName());
            assertThat(evidenceMessage.direction()).isEqualTo(message.direction());
        }

        @ParameterizedTest
        @MethodSource("nullCases")
        void should_fail_if_a_param_is_null(
            ConnectorBusinessMessage message,
            ConnectorMessageEvidence evidence) {
            assertThrows(
                NullPointerException.class,
                () -> evidenceMessageCreator.create(
                    message,
                    evidence
                )
            );
        }
    }

    @Nested
    @DisplayName("create triggered evidence message")
    class CreateTriggeredEvidenceMessage {
        static Stream<Arguments> nullCases() {
            var message = BusinessMessageTestFixtures.createOutboundMessage();
            var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
            var triggeredEvidenceMessage =
                TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage();
            return Stream.of(
                arguments(null, null, null),
                arguments(null, null, triggeredEvidenceMessage),
                arguments(null, evidence, triggeredEvidenceMessage),
                arguments(message, null, null),
                arguments(message, evidence, null),
                arguments(null, evidence, null)
            );
        }

        @Test
        void should_create_triggered_evidence_message() {
            var triggeredMessage =
                TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage();
            var businessMessage = BusinessMessageTestFixtures.createInboundMessage();
            when(messageIdGenerator.generateIdentifier()).thenReturn(IDENTIFIER);


            var evidenceMessage = evidenceMessageCreator.createForTrigger(
                businessMessage,
                EvidenceTestFixtures.createDeliveryEvidence(),
                triggeredMessage
            );

            assertNotNull(evidenceMessage);
            assertThat(evidenceMessage.businessDomainIdentifier()).isEqualTo(businessMessage.businessDomainIdentifier());
            assertThat(evidenceMessage.identifier()).isEqualTo(IDENTIFIER);
            assertThat(evidenceMessage.backendMessageIdentifier()).isEqualTo(triggeredMessage.backendMessageIdentifier());
            assertThat(evidenceMessage.referenceToBackendMessageIdentifier()).isEqualTo(
                businessMessage.backendMessageIdentifier());
            assertThat(evidenceMessage.as4Properties().referenceToIdentifier())
                .isEqualTo(businessMessage.as4Properties().ebmsMessageIdentifier());
            assertThat(evidenceMessage.backendName()).isEqualTo(businessMessage.backendName());
            assertThat(evidenceMessage.gatewayName()).isEqualTo(businessMessage.gatewayName());
            assertThat(evidenceMessage.direction()).isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
            assertThat(evidenceMessage.as4Properties().finalRecipient())
                .isEqualTo(businessMessage.as4Properties().originalSender());
            assertThat(evidenceMessage.as4Properties().originalSender())
                .isEqualTo(businessMessage.as4Properties().finalRecipient());
            assertThat(evidenceMessage.transportedEvidences()).hasSize(1);
            assertThat(evidenceMessage.as4Properties().fromParty()).isEqualTo(
                businessMessage.as4Properties().toParty());
            assertThat(evidenceMessage.as4Properties().toParty()).isEqualTo(
                businessMessage.as4Properties().fromParty());
            assertThat(evidenceMessage.as4Properties().service()).isEqualTo(
                businessMessage.as4Properties().service());
        }

        @ParameterizedTest
        @MethodSource("nullCases")
        void should_fail_if_a_param_is_null(
            ConnectorBusinessMessage message,
            ConnectorMessageEvidence evidence,
            ConnectorTriggeredEvidenceMessage triggeredEvidenceMessage) {
            assertThrows(
                NullPointerException.class,
                () -> evidenceMessageCreator.createForTrigger(
                    message,
                    evidence,
                    triggeredEvidenceMessage
                )
            );
        }
    }
}
