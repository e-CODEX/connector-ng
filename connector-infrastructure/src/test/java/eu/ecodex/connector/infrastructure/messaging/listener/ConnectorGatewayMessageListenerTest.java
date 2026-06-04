/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.service.impl.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.messaging.BaseJmsMessageTest;
import eu.ecodex.connector.infrastructure.messaging.listener.inbound.ConnectorGatewayMessageListener;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

public class ConnectorGatewayMessageListenerTest extends BaseJmsMessageTest {
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageBusinessContentRepository businessContentRepository;
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;
    @Mock
    private ConnectorEventPublisher inboundMessagePipelinePublisher;
    @Mock
    private ConnectorMessageIdGenerator messageIdGenerator;
    @Mock
    private ConnectorEventPublisher inboundEvidenceTriggerPublisher;
    @Mock
    private MapMessage message;

    private ConnectorGatewayMessageListener listener;

    @BeforeEach
    void setUp() {
        listener = new ConnectorGatewayMessageListener(
                messageRepository,
                businessContentRepository,
                attachmentRepository,
                evidenceRepository,
                fileStorageProvider,
                inboundMessagePipelinePublisher,
                messageIdGenerator,
                inboundEvidenceTriggerPublisher
        );
    }

    @Test
    void should_throw_exception_if_the_message_type_is_wrong() throws JMSException {
        when(message.getStringProperty("messageType")).thenReturn("outgoingMessage");

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_exception_if_the_message_id_is_null_or_empty() throws JMSException {
        when(message.getStringProperty("messageType")).thenReturn("incomingMessage");
        when(message.getStringProperty("messageId")).thenReturn("");

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_exception_if_the_message_has_zero_payload() throws JMSException {
        when(message.getStringProperty("messageType")).thenReturn("incomingMessage");
        when(message.getStringProperty("messageId")).thenReturn(
                "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu");
        when(message.getIntProperty("totalNumberOfPayloads")).thenReturn(0);

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_exception_if_the_message_payload_has_no_business_content_and_no_evidence()
            throws JMSException {
        stubValidHeader(message, 1);
        stubAS4Properties(message);
        when(message.getStringProperty("payload_1_description")).thenReturn("ASIC-S");
        when(message.getBytes("payload_1")).thenReturn(new byte[]{1, 2, 3});
        // fileStorageProvider and attachmentRepository must not blow up when called
        when(attachmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fileStorageProvider.save(any(), (byte[]) any())).thenReturn(anyString());

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @CsvSource({
            ",,",
            "BL,,",
            "BL,urn:oasis:names:tc:ebcore:partyid-type:ecodex,",
    })
    void should_throw_exception_if_the_message_from_party_is_invalid(
            String id, String type, String role) throws JMSException {
        stubValidHeader(message, 1);
        when(message.getStringProperty("service")).thenReturn("EPO");
        when(message.getStringProperty("serviceType")).thenReturn("urn:e-codex:services:");
        when(message.getStringProperty("action")).thenReturn("Form_A");
        when(message.getStringProperty("fromPartyId")).thenReturn(id);
        when(message.getStringProperty("fromPartyType")).thenReturn(type);
        when(message.getStringProperty("fromRole")).thenReturn(role);

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
            ",,",
            "RE,,",
            "RE,urn:oasis:names:tc:ebcore:partyid-type:ecodex,",
    })
    void should_throw_exception_if_the_message_to_party_is_invalid(
            String id, String type, String role) throws JMSException {
        stubValidHeader(message, 1);
        when(message.getStringProperty("service")).thenReturn("EPO");
        when(message.getStringProperty("serviceType")).thenReturn("urn:e-codex:services:");
        when(message.getStringProperty("action")).thenReturn("Form_A");
        when(message.getStringProperty("fromPartyId")).thenReturn("BL");
        when(message.getStringProperty("fromPartyType")).thenReturn(
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex");
        when(message.getStringProperty("fromRole")).thenReturn("GW");

        when(message.getStringProperty("toPartyId")).thenReturn(id);
        when(message.getStringProperty("toPartyType")).thenReturn(type);
        when(message.getStringProperty("toRole")).thenReturn(role);

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_handle_message_submission_successfully_if_the_message_is_a_business_message()
            throws JMSException {
        stubValidHeader(message, 4);
        stubAS4Properties(message);
        // payload 1: the required messageContent
        when(message.getStringProperty("payload_1_description")).thenReturn("messageContent");
        when(message.getBytes("payload_1")).thenReturn("<xml/>".getBytes());
        // payload 2: ASIC-S
        when(message.getStringProperty("payload_2_description")).thenReturn("ASIC-S");
        when(message.getBytes("payload_2")).thenReturn(new byte[]{1, 2, 3});
        // payload 3: xml trust token
        when(message.getStringProperty("payload_3_description")).thenReturn("tokenXML");
        when(message.getBytes("payload_3")).thenReturn("<xml/>".getBytes());
        // payload 4: evidence
        when(message.getStringProperty("payload_4_description")).thenReturn(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name());
        when(message.getBytes("payload_4")).thenReturn("<evidence/>".getBytes());

        when(messageIdGenerator.generateIdentifier()).thenReturn(
                "184b4564-72b2-4fe3-b5ce-6eaf93a1b7a7@connector.ecodex.eu");

        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(businessContentRepository.save(any(), any())).thenAnswer(i -> i.getArgument(0));
        when(attachmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fileStorageProvider.save(any(), (byte[]) any())).thenReturn(anyString());

        // Should complete without throwing; the unknown payload is silently skipped
        assertThatNoException().isThrownBy(() -> listener.handle(message));

        verify(inboundEvidenceTriggerPublisher, never()).publish(any());
        verify(inboundMessagePipelinePublisher).publish(any());
    }

    @Test
    void should_route_evidence_only_messages_to_confirmation_processor() throws JMSException {
        stubValidHeader(message, 1);
        stubAS4Properties(message);
        // payload 4: evidence
        when(message.getStringProperty("payload_1_description")).thenReturn(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name());
        when(message.getBytes("payload_1")).thenReturn("<evidence/>".getBytes());

        when(messageIdGenerator.generateIdentifier()).thenReturn(
                "184b4564-72b2-4fe3-b5ce-6eaf93a1b7a7@connector.ecodex.eu");
        when(messageRepository.save(any())).thenAnswer(invocation -> {
            var savedMessage = invocation.getArgument(0, ConnectorMessage.class);
            when(messageRepository.findByIdentifier(savedMessage.identifier())).thenReturn(
                    savedMessage);
            return savedMessage;
        });
        when(attachmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fileStorageProvider.save(any(), (byte[]) any())).thenReturn(anyString());

        // Should complete without throwing; the unknown payload is silently skipped
        assertThatNoException().isThrownBy(() -> listener.handle(message));

        verify(inboundEvidenceTriggerPublisher).publish(any());
        verify(inboundMessagePipelinePublisher, never()).publish(any());
    }

    @Test
    void should_handle_message_submission_successfully_by_handling_unknown_payload()
            throws JMSException {
        stubValidHeader(message, 5);
        stubAS4Properties(message);
        // payload 1: the required messageContent
        when(message.getStringProperty("payload_1_description")).thenReturn("messageContent");
        when(message.getBytes("payload_1")).thenReturn("<xml/>".getBytes());
        // payload 2: ASIC-S
        when(message.getStringProperty("payload_2_description")).thenReturn("ASIC-S");
        when(message.getBytes("payload_2")).thenReturn(new byte[]{1, 2, 3});
        // payload 3: xml trust token
        when(message.getStringProperty("payload_3_description")).thenReturn("tokenXML");
        when(message.getBytes("payload_3")).thenReturn("<xml/>".getBytes());
        // payload 4: evidence
        when(message.getStringProperty("payload_4_description")).thenReturn(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name());
        when(message.getBytes("payload_4")).thenReturn("<evidence/>".getBytes());
        // payload 3: completely unknown
        when(message.getStringProperty("payload_5_description")).thenReturn("weirdPayload");
        when(message.getBytes("payload_5")).thenReturn(new byte[]{9});

        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(attachmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fileStorageProvider.save(any(), (byte[]) any())).thenReturn(anyString());

        // Should complete without throwing; the unknown payload is silently skipped
        assertThatNoException().isThrownBy(() -> listener.handle(message));

        // pipelineEventPublisher was still called — processing continued
        verify(inboundEvidenceTriggerPublisher, never()).publish(any());
        verify(inboundMessagePipelinePublisher).publish(any());
    }

    private void stubValidHeader(MapMessage msg, int payloadCount) throws JMSException {
        when(msg.getStringProperty("messageType")).thenReturn("incomingMessage");
        when(message.getStringProperty("messageId")).thenReturn(
                "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu");
        when(msg.getIntProperty("totalNumberOfPayloads")).thenReturn(payloadCount);
    }

    private void stubAS4Properties(MapMessage msg) throws JMSException {
        when(msg.getStringProperty("service")).thenReturn("EPO");
        when(msg.getStringProperty("serviceType")).thenReturn("urn:e-codex:services:");
        when(msg.getStringProperty("action")).thenReturn("Form_A");
        when(msg.getStringProperty("fromPartyId")).thenReturn("BL");
        when(msg.getStringProperty("fromPartyType")).thenReturn(
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex");
        when(msg.getStringProperty("fromRole")).thenReturn("GW");
        when(msg.getStringProperty("toPartyId")).thenReturn("RE");
        when(msg.getStringProperty("toPartyType")).thenReturn(
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex");
        when(msg.getStringProperty("toRole")).thenReturn("GW");
        when(msg.getStringProperty("conversationId")).thenReturn(
                "669b78ca-652c-40b8-883e-512be910550b");
        when(msg.getStringProperty("messageId")).thenReturn(
                "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu");
        when(msg.getStringProperty("refToMessageId")).thenReturn(null);
        when(msg.getStringProperty("originalSender")).thenReturn("alice");
        when(msg.getStringProperty("finalRecipient")).thenReturn("bob");
    }
}
