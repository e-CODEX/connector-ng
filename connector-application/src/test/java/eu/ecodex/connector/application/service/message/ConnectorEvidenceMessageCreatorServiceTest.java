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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.ConnectorEvidenceMessageCreatorService;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorEvidenceMessageCreatorServiceTest {
    @Mock
    private ConnectorMessageIdGenerator messageIdGenerator;

    @InjectMocks
    private ConnectorEvidenceMessageCreatorService evidenceMessageCreator;

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

    @ParameterizedTest
    @MethodSource("provideEvidence")
    void should_create_evidence_message_successfully(ConnectorMessageEvidence evidence) {
        when(messageIdGenerator.generateIdentifier())
               .thenReturn("d040fe80-55a6-4d51-85de-9e16280eb503@connector.ecodex.eu");
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        var as4Properties = message.as4Properties();
        var action = ConnectorEvidenceMessageCreatorService.getEvidenceAction(evidence.type());
        as4Properties = as4Properties.toBuilder().action(action).build();
        message = message.toBuilder().as4Properties(as4Properties).build();

        var evidenceMessage = this.evidenceMessageCreator.create(message, evidence);

        assertThat(evidenceMessage).isNotNull();
        assertThat(evidenceMessage.evidences()).isNullOrEmpty();
        assertThat(evidenceMessage.identifier()).isNotEqualTo(message.identifier());
        assertThat(evidenceMessage.as4Properties().action().name()).isEqualTo(
                ConnectorEvidenceMessageCreatorService.getEvidenceAction(evidence.type()).name());
        assertThat(evidenceMessage.as4Properties().service()).isEqualTo(
                message.as4Properties().service());
        // assertThat(evidenceMessage.referenceToBackendMessageIdentifier()).isEqualTo(
        //         message.backendMessageIdentifier());
        assertThat(evidenceMessage.as4Properties().referenceToIdentifier()).isEqualTo(
                message.as4Properties().ebmsMessageIdentifier());
        assertThat(evidenceMessage.as4Properties().ebmsMessageIdentifier()).isEqualTo(
                message.as4Properties().ebmsMessageIdentifier());
        assertThat(evidenceMessage.as4Properties().conversationIdentifier()).isEqualTo(
                message.as4Properties().conversationIdentifier());
        assertThat(evidenceMessage.as4Properties().fromParty()).isEqualTo(
                message.as4Properties().fromParty());
        assertThat(evidenceMessage.as4Properties().toParty()).isEqualTo(
                message.as4Properties().toParty());
        assertThat(evidenceMessage.as4Properties().finalRecipient()).isEqualTo(
                message.as4Properties().finalRecipient());
        assertThat(evidenceMessage.as4Properties().originalSender()).isEqualTo(
                message.as4Properties().originalSender());
        assertThat(evidenceMessage.gatewayName()).isEqualTo(message.gatewayName());
        assertThat(evidenceMessage.backendName()).isEqualTo(message.backendName());
        assertThat(evidenceMessage.direction()).isEqualTo(
                message.direction());
    }
}
