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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.infrastructure.inbound.jms.listener.inbound.ConnectorJmsGatewayMessageListener;
import eu.ecodex.connector.infrastructure.messaging.BaseJmsMessageTest;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorJmsGatewayMessageListener")
public class ConnectorJmsGatewayMessageListenerTest extends BaseJmsMessageTest {
    @Mock
    private ConnectorMessageAttachmentRepository attachmentRepository;
    @Mock
    private ConnectorFileStorageProvider fileStorageProvider;
    @Mock
    private ConnectorInboundBusinessMessageReceiver inboundMessageReceiverService;
    @Mock
    private ConnectorInboundEvidenceMessageReceiver inboundEvidenceReceiverService;
    @Mock
    private MapMessage message;

    @InjectMocks
    private ConnectorJmsGatewayMessageListener listener;

    private void stubValidHeader(MapMessage msg, int payloadCount) throws JMSException {
        when(msg.getStringProperty("messageType"))
            .thenReturn("incomingMessage");
        when(msg.getStringProperty("messageId"))
            .thenReturn(
                "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu"
            );
        when(msg.getIntProperty("totalNumberOfPayloads"))
            .thenReturn(payloadCount);
    }

    private void stubAS4Properties(MapMessage msg) throws JMSException {
        when(msg.getStringProperty("service"))
            .thenReturn("EPO");
        when(msg.getStringProperty("serviceType"))
            .thenReturn("urn:e-codex:services:");
        when(msg.getStringProperty("action"))
            .thenReturn("Form_A");
        when(msg.getStringProperty("fromPartyId"))
            .thenReturn("BL");
        when(msg.getStringProperty("fromPartyType"))
            .thenReturn(
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
            );
        when(msg.getStringProperty("fromRole"))
            .thenReturn("GW");
        when(msg.getStringProperty("toPartyId"))
            .thenReturn("RE");
        when(msg.getStringProperty("toPartyType"))
            .thenReturn(
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
            );
        when(msg.getStringProperty("toRole"))
            .thenReturn("GW");
        when(msg.getStringProperty("conversationId"))
            .thenReturn("669b78ca-652c-40b8-883e-512be910550b");
        when(msg.getStringProperty("messageId"))
            .thenReturn(
                "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu"
            );
        when(msg.getStringProperty("refToMessageId"))
            .thenReturn(null);
        when(msg.getStringProperty("originalSender"))
            .thenReturn("alice");
        when(msg.getStringProperty("finalRecipient"))
            .thenReturn("bob");
    }

    private void stubAttachmentPersistence() {
        when(attachmentRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(fileStorageProvider.save(any(), any(byte[].class)))
            .thenReturn(anyString());
    }

    @Nested
    @DisplayName("message validation")
    class MessageValidation {
        @Test
        void should_reject_null_jms_message() {
            assertThatThrownBy(() -> listener.handle(null))
                .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @Test
        void should_reject_message_with_invalid_message_type() throws JMSException {
            when(message.getStringProperty("messageType"))
                .thenReturn("outgoingMessage");

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @Test
        void should_reject_message_with_missing_message_id() throws JMSException {
            when(message.getStringProperty("messageType"))
                .thenReturn("incomingMessage");
            when(message.getStringProperty("messageId"))
                .thenReturn("");

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @Test
        void should_reject_message_with_no_payloads() throws JMSException {
            when(message.getStringProperty("messageType"))
                .thenReturn("incomingMessage");
            when(message.getStringProperty("messageId"))
                .thenReturn(
                    "9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu"
                );
            when(message.getIntProperty("totalNumberOfPayloads"))
                .thenReturn(0);

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @Test
        void should_reject_message_with_missing_payload_description() throws JMSException {
            stubValidHeader(message, 1);
            stubAS4Properties(message);

            when(message.getStringProperty("payload_1_description"))
                .thenReturn("");

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing description for payload at index 1");

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @Test
        void should_reject_message_without_transported_evidence() throws JMSException {
            stubValidHeader(message, 1);
            stubAS4Properties(message);

            when(message.getStringProperty("payload_1_description"))
                .thenReturn("ASIC-S");
            when(message.getBytes("payload_1"))
                .thenReturn(new byte[]{1, 2, 3});

            stubAttachmentPersistence();

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incoming message requires at least one transported evidence");

            verifyNoInteractions(
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @ParameterizedTest
        @CsvSource({
            ",,",
            "BL,,",
            "BL,urn:oasis:names:tc:ebcore:partyid-type:ecodex,"
        })
        void should_reject_message_with_invalid_from_party(
            String id,
            String type,
            String role
        ) throws JMSException {
            stubValidHeader(message, 1);

            when(message.getStringProperty("service"))
                .thenReturn("EPO");
            when(message.getStringProperty("serviceType"))
                .thenReturn("urn:e-codex:services:");
            when(message.getStringProperty("action"))
                .thenReturn("Form_A");
            when(message.getStringProperty("fromPartyId"))
                .thenReturn(id);
            when(message.getStringProperty("fromPartyType"))
                .thenReturn(type);
            when(message.getStringProperty("fromRole"))
                .thenReturn(role);

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }

        @ParameterizedTest
        @CsvSource({
            ",,",
            "RE,,",
            "RE,urn:oasis:names:tc:ebcore:partyid-type:ecodex,"
        })
        void should_reject_message_with_invalid_to_party(
            String id,
            String type,
            String role
        ) throws JMSException {
            stubValidHeader(message, 1);

            when(message.getStringProperty("service"))
                .thenReturn("EPO");
            when(message.getStringProperty("serviceType"))
                .thenReturn("urn:e-codex:services:");
            when(message.getStringProperty("action"))
                .thenReturn("Form_A");

            when(message.getStringProperty("fromPartyId"))
                .thenReturn("BL");
            when(message.getStringProperty("fromPartyType"))
                .thenReturn(
                    "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
                );
            when(message.getStringProperty("fromRole"))
                .thenReturn("GW");

            when(message.getStringProperty("toPartyId"))
                .thenReturn(id);
            when(message.getStringProperty("toPartyType"))
                .thenReturn(type);
            when(message.getStringProperty("toRole"))
                .thenReturn(role);

            assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(
                attachmentRepository,
                fileStorageProvider,
                inboundMessageReceiverService,
                inboundEvidenceReceiverService
            );
        }
    }

    @Nested
    @DisplayName("business message processing")
    class BusinessMessageProcessing {
        @Test
        void should_publish_business_message_successfully() throws JMSException {
            stubValidHeader(message, 4);
            stubAS4Properties(message);
            // payload 1: the required messageContent
            when(message.getStringProperty("payload_1_description")).thenReturn("messageContent");
            when(message.getStringProperty("payload_1_name")).thenReturn("Form_A.xml");
            when(message.getBytes("payload_1")).thenReturn("<xml/>".getBytes());
            // payload 2: ASIC-S
            when(message.getStringProperty("payload_2_description")).thenReturn("ASIC-S");
            when(message.getStringProperty("payload_2_name")).thenReturn("container.asics");
            when(message.getBytes("payload_2")).thenReturn(new byte[]{1, 2, 3});
            // payload 3: xml trust token
            when(message.getStringProperty("payload_3_description")).thenReturn("tokenXML");
            when(message.getStringProperty("payload_3_name")).thenReturn("tokenXML.xml");
            when(message.getBytes("payload_3")).thenReturn("<xml/>".getBytes());
            // payload 4: evidence
            when(message.getStringProperty("payload_4_description")).thenReturn(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name());
            when(message.getStringProperty("payload_4_name")).thenReturn("submission_acceptance"
                                                                             + ".xml");
            when(message.getBytes("payload_4")).thenReturn("<evidence/>".getBytes());

            stubAttachmentPersistence();

            assertThatNoException().isThrownBy(() -> listener.handle(message));

            var captor = ArgumentCaptor.forClass(ConnectorInboundBusinessMessageCommand.class);
            verify(inboundMessageReceiverService).execute(captor.capture());

            var command = captor.getValue();
            assertThat(command).isNotNull();
            assertThat(command.businessDomainIdentifier())
                .isEqualTo(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID);
            assertThat(command.gatewayName())
                .isEqualTo(ConnectorDefaults.DEFAULT_GATEWAY_NAME);
            assertThat(command.as4Properties()).isNotNull();
            assertThat(command.as4Properties().ebmsMessageIdentifier())
                .isEqualTo("9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu");
            assertThat(command.businessContent()).isNotNull();
            assertThat(command.businessContent().xmlContent().name()).isEqualTo("Form_A.xml");
            assertThat(command.businessContent().xmlContent().type())
                .isEqualTo(ConnectorAttachmentType.BUSINESS_CONTENT);
            assertThat(command.attachments()).hasSize(2);
            assertThat(command.transportedEvidences()).hasSize(1);
            assertThat(command.transportedEvidences().getFirst().type())
                .isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);

            verify(inboundEvidenceReceiverService, never()).execute(any());
            verify(attachmentRepository, times(3)).save(any());
            verify(fileStorageProvider, times(3)).save(any(), any(byte[].class));
        }

        @Test
        void should_use_fallback_name_when_payload_name_is_blank() throws JMSException {
            stubValidHeader(message, 2);
            stubAS4Properties(message);

            when(message.getStringProperty("payload_1_description")).thenReturn("messageContent");
            when(message.getStringProperty("payload_1_name")).thenReturn(null);
            when(message.getBytes("payload_1")).thenReturn("<xml/>".getBytes());

            when(message.getStringProperty("payload_2_description")).thenReturn(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name());
            when(message.getStringProperty("payload_2_name")).thenReturn(null);
            when(message.getBytes("payload_2")).thenReturn("<evidence/>".getBytes());

            stubAttachmentPersistence();

            assertThatNoException().isThrownBy(() -> listener.handle(message));

            var captor = ArgumentCaptor.forClass(ConnectorInboundBusinessMessageCommand.class);
            verify(inboundMessageReceiverService).execute(captor.capture());

            var command = captor.getValue();
            assertThat(command.businessContent().xmlContent().name()).isEqualTo("messagecontent");

            verify(inboundEvidenceReceiverService, never()).execute(any());
        }

        @Test
        void should_continue_processing_when_message_contains_unknown_payload()
            throws JMSException {
            stubValidHeader(message, 5);
            stubAS4Properties(message);

            // Business content
            when(message.getStringProperty("payload_1_description"))
                .thenReturn("messageContent");
            when(message.getStringProperty("payload_1_name"))
                .thenReturn("Form_A.xml");
            when(message.getBytes("payload_1"))
                .thenReturn("<xml/>".getBytes());

            // ASIC-S
            when(message.getStringProperty("payload_2_description"))
                .thenReturn("ASIC-S");
            when(message.getStringProperty("payload_2_name"))
                .thenReturn("container.asics");
            when(message.getBytes("payload_2"))
                .thenReturn(new byte[]{1, 2, 3});

            // XML trust token
            when(message.getStringProperty("payload_3_description"))
                .thenReturn("tokenXML");
            when(message.getStringProperty("payload_3_name"))
                .thenReturn("tokenXML.xml");
            when(message.getBytes("payload_3"))
                .thenReturn("<xml/>".getBytes());

            // Evidence
            when(message.getStringProperty("payload_4_description"))
                .thenReturn(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name()
                );
            when(message.getStringProperty("payload_4_name"))
                .thenReturn("submission_acceptance.xml");
            when(message.getBytes("payload_4"))
                .thenReturn("<evidence/>".getBytes());

            // Unknown payload
            when(message.getStringProperty("payload_5_description"))
                .thenReturn("weirdPayload");
            when(message.getStringProperty("payload_5_name"))
                .thenReturn("weird.xml");
            when(message.getBytes("payload_5"))
                .thenReturn(new byte[]{9});

            stubAttachmentPersistence();

            assertThatNoException()
                .isThrownBy(() -> listener.handle(message));

            verify(inboundMessageReceiverService).execute(any());
            verify(inboundEvidenceReceiverService, never()).execute(any());
            verify(attachmentRepository, times(3)).save(any());
            verify(fileStorageProvider, times(3)).save(any(), any(byte[].class));
        }
    }

    @Nested
    @DisplayName("evidence message processing")
    class EvidenceMessageProcessing {
        @Test
        void should_publish_evidence_only_message_to_evidence_processor() throws JMSException {
            stubValidHeader(message, 1);
            stubAS4Properties(message);

            when(message.getStringProperty("payload_1_description"))
                .thenReturn(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE.name()
                );
            when(message.getBytes("payload_1"))
                .thenReturn("<evidence/>".getBytes());

            assertThatNoException()
                .isThrownBy(() -> listener.handle(message));

            var captor = ArgumentCaptor.forClass(ConnectorInboundEvidenceMessageCommand.class);
            verify(inboundEvidenceReceiverService).execute(captor.capture());

            var command = captor.getValue();
            assertThat(command).isNotNull();
            assertThat(command.businessDomainIdentifier())
                .isEqualTo(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID);
            assertThat(command.gatewayName())
                .isEqualTo(ConnectorDefaults.DEFAULT_GATEWAY_NAME);
            assertThat(command.as4Properties()).isNotNull();
            assertThat(command.as4Properties().ebmsMessageIdentifier())
                .isEqualTo("9a855348-4ed7-11f1-815b-c6ceea70fe39@domibus.eu");
            assertThat(command.transportedEvidences()).hasSize(1);
            assertThat(command.transportedEvidences().getFirst().type())
                .isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);

            verify(inboundMessageReceiverService, never()).execute(any());
            verifyNoInteractions(attachmentRepository, fileStorageProvider);
        }
    }
}
