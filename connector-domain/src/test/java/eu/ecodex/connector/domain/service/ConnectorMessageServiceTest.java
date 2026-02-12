/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageProcessingConfigProviderTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorMessageIdentifierException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotBusinessException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorMessagePartyException;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;

    private ConnectorMessageService connectorMessageService;

    private static Stream<ConnectorEvidence> provideEvidence() {
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

    @BeforeEach
    void setUp() {
        this.connectorMessageService = new ConnectorMessageServiceImpl(
                messageRepository, messageProcessingConfigProvider
        );
    }

    // save a message
    @Test
    void should_register_message_successfully() {
        when(messageRepository.save(any())).thenReturn(
                MessageTestFixtures.createValidOutboundBusinessMessage());

        var savedMessage = this.connectorMessageService.register(
                MessageTestFixtures.createValidOutboundBusinessMessage());
        assertThat(savedMessage).isNotNull();
    }

    @Test
    void should_throw_exception_when_message_identifier_already_exists() {
        when(messageRepository.findByIdentifier(any())).thenReturn(
                MessageTestFixtures.createValidOutboundBusinessMessage());

        assertThrows(
                ConnectorMessageIdentifierException.class, () -> this.connectorMessageService.register(
                        MessageTestFixtures.createValidOutboundBusinessMessage())
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null() {
        assertThrows(NullPointerException.class, () -> this.connectorMessageService.register(null));
    }

    // find message by uuid
    @Test
    void should_find_message_by_identifier_successfully() {
        when(messageRepository.findByIdentifier(any()))
                .thenReturn(MessageTestFixtures.createValidOutboundBusinessMessage());

        var message = this.connectorMessageService.findByIdentifier(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.identifier()).isEqualTo(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_by_its_identifier() {
        when(messageRepository.findByIdentifier(any())).thenReturn(null);
        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> connectorMessageService.findByIdentifier("random-uuid")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_identifier_is_null() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.findByIdentifier(null));
    }

    // find by uuid and direction
    @Test
    void should_find_message_by_identifier_and_direction_successfully() {
        when(messageRepository.findByIdentifierAndDirection(any(), any()))
                .thenReturn(MessageTestFixtures.createValidOutboundBusinessMessage());

        var message = connectorMessageService.findByIdentifierAndDirection(
                MessageTestFixtures.createValidOutboundBusinessMessage(),
                ConnectorMessageDirection.BACKEND_TO_GATEWAY
        );

        assertThat(message).isNotNull();
        assertThat(message.identifier()).isEqualTo(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
        assertThat(message.direction()).isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_by_its_identifier_and_direction() {
        when(messageRepository.findByIdentifierAndDirection(any(), any()))
                .thenReturn(null);

        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> this.connectorMessageService.findByIdentifierAndDirection(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ConnectorMessageDirection.BACKEND_TO_GATEWAY
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_identifier_is_null_when_searching_by_identifier_and_direction() {
        assertThrows(
                NullPointerException.class,
                () -> connectorMessageService.findByIdentifierAndDirection(
                        null, ConnectorMessageDirection.BACKEND_TO_GATEWAY
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_direction_is_null_when_searching_by_identifier_and_direction() {
        assertThrows(
                NullPointerException.class,
                () -> connectorMessageService.findByIdentifierAndDirection(
                        null, null
                )
        );
    }

    // find by conversation uuid
    @Test
    void should_find_messages_by_conversation_identifier_successfully() {
        when(messageRepository.findByConversationIdentifier(any()))
                .thenReturn(Collections.singletonList(MessageTestFixtures.createValidOutboundBusinessMessage()));

        var messages = this.connectorMessageService.findByConversationIdentifier("f76276d5-d058-4477-95de-848c9673e543");

        assertThat(messages).isNotNull();
        assertThat(messages.size()).isEqualTo(1);
    }

    @Test
    void should_throw_exception_if_identifier_is_null_when_searching_by_conversation_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> connectorMessageService.findByConversationIdentifier(null)
        );
    }

    // check outgoing message to and from parties info
    @Test
    void should_check_outgoing_message_parties_info_successfully() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        // no thrown exception mean from and to parties are set correctly
        this.connectorMessageService.checkPartiesInfo(message);
    }

    @Test
    void should_throw_exception_when_outbound_message_from_party_info_is_null() {
        var message = MessageTestFixtures.createNullFromPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> connectorMessageService.checkPartiesInfo(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_to_party_info_is_null() {
        var message = MessageTestFixtures.createNullToPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> connectorMessageService.checkPartiesInfo(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_from_party_info_are_incorrect() {
        var message = MessageTestFixtures.createInvalidFromPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> connectorMessageService.checkPartiesInfo(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_to_party_info_are_incorrect() {
        var message = MessageTestFixtures.createInvalidToPartyOutboundBusinessMessage();
        assertThrows(
                ConnectorMessagePartyException.class,
                () -> connectorMessageService.checkPartiesInfo(message)
        );
    }

    @Test
    void should_throw_exception_when_outbound_message_direction_is_incorrect() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage()
                .toBuilder()
                .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                .build();
        assertThrows(
                UnsupportedOperationException.class,
                () -> connectorMessageService.checkPartiesInfo(message)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_checking_outgoing_message_parties_info() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.checkPartiesInfo(null));
    }

    // check if a message is an evidence or a business message
    @Test
    void should_return_true_if_message_is_business_message() {
        var isBusiness = this.connectorMessageService.isBusinessMessage(
                MessageTestFixtures.createValidOutboundBusinessMessage());
        assertThat(isBusiness).isTrue();
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_checking_if_business_message() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.isBusinessMessage(null));
    }

    @Test
    void should_return_false_if_message_is_not_business_message() {
        var isBusiness = this.connectorMessageService.isBusinessMessage(
                MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage());
        assertThat(isBusiness).isFalse();
    }

    @Test
    void should_return_true_if_message_is_an_evidence_message() {
        var isEvidence = this.connectorMessageService.isEvidenceMessage(
                MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage());
        assertThat(isEvidence).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_message() {
        var isEvidence = this.connectorMessageService.isEvidenceMessage(
                MessageTestFixtures.createValidOutboundBusinessMessage());
        assertThat(isEvidence).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_message_when_evidences_is_null() {
        var message = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage()
                .toBuilder()
                .evidences(null)
                .build();

        var isEvidence = this.connectorMessageService.isEvidenceMessage(message);
        assertThat(isEvidence).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_message_when_evidences_is_empty() {
        var message = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage()
                                 .toBuilder()
                                 .evidences(Collections.emptyList())
                                 .build();

        var isEvidence = this.connectorMessageService.isEvidenceMessage(message);
        assertThat(isEvidence).isFalse();
    }

    @Test
    void should_throw_exception_if_message_is_null_when_checking_if_message_is_an_evidence_message() {
        assertThrows(
                NullPointerException.class,
                () -> this.connectorMessageService.isEvidenceMessage(null)
        );
    }

    // check if the message is evidence trigger message
    @Test
    void should_return_true_if_message_is_evidence_trigger_message() {
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(
                MessageTestFixtures.createEvidenceTriggerMessage()
        );

        assertThat(isEvidenceTrigger).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message() {
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(
                MessageTestFixtures.createValidInboundBusinessMessage()
        );

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_null() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                .toBuilder()
                .evidences(null)
                .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_empty() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                 .toBuilder()
                                 .evidences(Collections.emptyList())
                                 .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_content_is_empty() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                 .toBuilder()
                                 .evidences(
                                         Collections.singletonList(
                                                 EvidenceTestFixtures.createEvidenceTrigger()
                                                                     .toBuilder()
                                                                     .content(new byte[1])
                                                                     .build()
                                         )
                                 )
                                 .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_throw_exception_if_message_is_null_when_checking_if_message_is_an_evidence_trigger_message() {
        assertThrows(
                NullPointerException.class,
                () -> this.connectorMessageService.isEvidenceTriggerMessage(null)
        );
    }

    // add evidence to a message
    @Test
    void should_add_evidence_to_message_successfully() {
        when(messageRepository.findByIdentifier(any())).thenReturn(
                MessageTestFixtures.createValidOutboundBusinessMessage());
        when(messageRepository.addEvidence(any(), any())).thenReturn(
                MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage());
        var businessMessage = MessageTestFixtures.createValidOutboundBusinessMessage();
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        var savedMessage = this.connectorMessageService.addEvidence(businessMessage, evidence);
        assertThat(savedMessage.evidences()).isNotNull();
        assertThat(savedMessage.evidences()).isNotEmpty();
        assertThat(savedMessage.evidences()).hasSize(1);
    }

    @Test
    void should_throw_exception_when_message_is_not_a_business_message_when_adding_an_evidence() {
        var businessMessage = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage();
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
        Assertions.assertThrows(
                ConnectorMessageNotBusinessException.class,
                () -> this.connectorMessageService.addEvidence(businessMessage, evidence)
        );
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_when_adding_evidence() {
        when(messageRepository.findByIdentifier(any())).thenReturn(null);
        Assertions.assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> this.connectorMessageService.addEvidence(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        EvidenceTestFixtures.createSubmissionAcceptanceEvidence()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_adding_evidence() {
        Assertions.assertThrows(
                NullPointerException.class, () -> this.connectorMessageService.addEvidence(
                        null,
                        EvidenceTestFixtures.createSubmissionAcceptanceEvidence()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_evidence_is_null_when_adding_evidence() {
        Assertions.assertThrows(
                NullPointerException.class, () -> this.connectorMessageService.addEvidence(
                        MessageTestFixtures.createValidOutboundBusinessMessage(), null)
        );
    }

    // set a message as rejected
    @Test
    void should_set_message_as_rejected_successfully() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        when(messageRepository.setAsRejected(any())).thenReturn(
                MessageTestFixtures.createRejectedMessage());
        var rejectedMessage = this.connectorMessageService.setAsRejected(message);
        assertThat(rejectedMessage.rejectedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_when_setting_as_rejected() {
        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> connectorMessageService.setAsRejected(
                        MessageTestFixtures.createValidOutboundBusinessMessage())
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_setting_as_rejected() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.setAsRejected(null));
    }

    // set the message as confirmed
    @Test
    void should_set_message_as_confirmed_successfully() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        when(messageRepository.setAsConfirmed(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());
        var confirmedMessage = connectorMessageService.setAsConfirmed(message);
        assertThat(confirmedMessage.confirmedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_when_setting_as_confirmed() {
        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> connectorMessageService.setAsConfirmed(
                        MessageTestFixtures.createValidOutboundBusinessMessage())
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_setting_as_confirmed() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.setAsConfirmed(null));
    }

    // check if a message has been rejected
    @Test
    void should_return_true_if_message_has_been_rejected() {
        when(messageRepository.findByIdentifier(any())).thenReturn(
                MessageTestFixtures.createRejectedMessage());
        var message = MessageTestFixtures.createRejectedMessage();
        var isRejected = connectorMessageService.isRejected(message);
        assertThat(isRejected).isTrue();
    }

    @Test
    void should_return_false_if_message_has_not_been_rejected() {
        when(messageRepository.findByIdentifier(any())).thenReturn(
                MessageTestFixtures.createValidOutboundBusinessMessage());
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        var isRejected = connectorMessageService.isRejected(message);
        assertThat(isRejected).isFalse();
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_checking_if_rejected() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.isRejected(null));
    }

    // create an evidence message
    @ParameterizedTest
    @MethodSource("provideEvidence")
    void should_create_evidence_message_successfully(ConnectorEvidence evidence) {
        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(MessageProcessingConfigProviderTestFixtures.getProcessingProperties());

        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        var evidenceMessage = this.connectorMessageService.createEvidenceMessage(message, evidence);

        assertThat(evidenceMessage).isNotNull();
        assertThat(evidenceMessage.evidences()).isNotEmpty();
        assertThat(evidenceMessage.evidences()).contains(evidence);
        assertThat(evidenceMessage.as4Properties().action().name()).isEqualTo(
                ConnectorMessageServiceImpl.getEvidenceAction(evidence.type()).name());
        assertThat(evidenceMessage.as4Properties().service()).isEqualTo(
                message.as4Properties().service());
        assertThat(evidenceMessage.referenceToBackendMessageIdentifier()).isEqualTo(
                message.backendMessageIdentifier());
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
        assertThat(evidenceMessage.direction()).isEqualTo(message.direction());
    }

    @Test
    void should_throw_null_pointer_exception_when_evidence_type_is_null_when_creating_action_for_evidence_message() {
        assertThrows(NullPointerException.class, () -> ConnectorMessageServiceImpl.getEvidenceAction(null));
    }

    // switch message direction
    @Test
    void should_switch_message_direction_successfully() {
        var initialMessage = MessageTestFixtures.createValidOutboundBusinessMessage();
        var switchedMessage = this.connectorMessageService.switchDirection(initialMessage);
        assertThat(switchedMessage.direction()).isNotEqualTo(initialMessage.direction());
        assertThat(switchedMessage.as4Properties().originalSender()).isNotEqualTo(
                initialMessage.as4Properties().originalSender());
        assertThat(switchedMessage.as4Properties().originalSender()).isEqualTo(
                initialMessage.as4Properties().finalRecipient());
        assertThat(switchedMessage.as4Properties().finalRecipient()).isNotEqualTo(
                initialMessage.as4Properties().finalRecipient());
        assertThat(switchedMessage.as4Properties().finalRecipient()).isEqualTo(
                initialMessage.as4Properties().originalSender());
        assertThat(switchedMessage.as4Properties().fromParty().roleType()).isEqualTo(
                initialMessage.as4Properties().fromParty().roleType());
        assertThat(switchedMessage.as4Properties().fromParty().role()).isEqualTo(
                initialMessage.as4Properties().fromParty().role());
        assertThat(switchedMessage.as4Properties().toParty().roleType()).isEqualTo(
                initialMessage.as4Properties().toParty().roleType());
        assertThat(switchedMessage.as4Properties().toParty().role()).isEqualTo(
                initialMessage.as4Properties().toParty().role());
    }

    // assign ebms uuid to message
    @Test
    void should_assign_ebms_identifier_to_message_successfully() {
        var processingProperties = MessageProcessingConfigProviderTestFixtures.getProcessingProperties();
        when(messageProcessingConfigProvider.getProcessingProperties()).thenReturn(
                processingProperties);
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        var assignedMessage = connectorMessageService.assignEbmsIdentifier(message);
        assertThat(assignedMessage.as4Properties().ebmsMessageIdentifier()).isNotNull();
        assertThat(assignedMessage.as4Properties().ebmsMessageIdentifier())
                .contains(processingProperties.ebmsIdSuffix());
    }

    @Test
    void should_throw_null_pointer_exception_when_message_is_null_when_assigning_ebms_identifier() {
        assertThrows(
                NullPointerException.class, () -> connectorMessageService.assignEbmsIdentifier(null)
        );
    }
}
