/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.BusinessDomainIdentifierTestFixtures;
import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MultipartFileTestFixtures;
import eu.ecodex.connector.TriggeredEvidenceMessageTestFixtures;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message.ConnectorMessageController;
import eu.ecodex.connector.infrastructure.inbound.web.rest.parser.ConnectorRestOutboundMessageParser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageBusinessContent;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageIdentifiers;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageRequest;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ConnectorMessageController.class)
@DisplayName("ConnectorMessageController")
public class ConnectorMessageControllerTest extends AbstractWebMvcTest {
    private static final String OUTBOUND_MESSAGE_URL = "/api/v1/messages/outbound";
    private static final String EVIDENCE_TRIGGER_URL = "/api/v1/messages/evidence-trigger";

    private static final String BUSINESS_DOMAIN_IDENTIFIER = "default_business_domain";
    private static final String BACKEND_MESSAGE_IDENTIFIER =
        "56ed1a8f-b089-4615-9ddd-da302a665a11@backend_system";
    private static final String CONVERSATION_IDENTIFIER = "e6a173ec-de21-46dc-8a19-63a6cb74915d";
    private static final String PARTY_IDENTIFIER_TYPE =
        "urn:oasis:names:tc:ebcore:partyid-type:ecodex";
    private static final String REFERENCE_TO_IDENTIFIER = "msg-ref-001";
    private static final String BACKEND_NAME =
        LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name();
    // Real instances returned by the mocked parser (records cannot be mocked reliably).
    private final ConnectorBusinessDomainIdentifier businessDomainIdentifier =
        BusinessDomainIdentifierTestFixtures.createDefaultBusinessDomainIdentifier();
    private final ConnectorMessageBusinessContent businessContent =
        BusinessMessageTestFixtures.createOutboundMessage().businessContent();
    private final List<ConnectorMessageAttachment> attachments =
        List.of(MessageAttachmentTestFixtures.createAttachment());
    private final ConnectorParty fromParty = ConnectorParty
        .builder()
        .identifier("BL")
        .identifierType(PARTY_IDENTIFIER_TYPE)
        .role("GW")
        .roleType(ConnectorPartyRoleType.INITIATOR)
        .build();
    private final ConnectorParty toParty = ConnectorParty
        .builder()
        .identifier("RE")
        .identifierType(PARTY_IDENTIFIER_TYPE)
        .role("GW")
        .roleType(ConnectorPartyRoleType.RESPONDER)
        .build();
    @MockitoBean
    private ConnectorOutboundBusinessMessageReceiver outboundBusinessMessageReceiverService;
    @MockitoBean
    private ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService;
    @MockitoBean
    private ConnectorBackendClientVerifier backendClientVerifierService;
    @MockitoBean
    private ConnectorRestOutboundMessageParser restOutboundMessageParser;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    /**
     * Stubs every collaborator the controller calls while building the outbound command: the
     * backend client lookup, the four public parser methods, and the receiver itself.
     */
    private void stubOutboundSubmissionSuccess() throws IOException {
        when(backendClientVerifierService.getBackendClient(any())).thenReturn(BACKEND_NAME);

        when(restOutboundMessageParser.resolveBusinessDomainIdentifier(any()))
            .thenReturn(businessDomainIdentifier);
        when(restOutboundMessageParser.toBusinessContent(any())).thenReturn(businessContent);
        when(restOutboundMessageParser.toAttachments(any())).thenReturn(attachments);
        when(restOutboundMessageParser.toParty(any(), eq(ConnectorPartyRoleType.INITIATOR)))
            .thenReturn(fromParty);
        when(restOutboundMessageParser.toParty(any(), eq(ConnectorPartyRoleType.RESPONDER)))
            .thenReturn(toParty);

        when(outboundBusinessMessageReceiverService.execute(any()))
            .thenReturn(BusinessMessageTestFixtures.createOutboundMessage());
    }

    private ConnectorOutboundBusinessMessageCommand captureOutboundCommand() {
        var captor = ArgumentCaptor.forClass(ConnectorOutboundBusinessMessageCommand.class);
        verify(outboundBusinessMessageReceiverService).execute(captor.capture());
        return captor.getValue();
    }

    private MockMultipartHttpServletRequestBuilder buildValidOutboundMessageRequest() {
        var businessContentFile = new MockMultipartFile(
            "businessContent.contentFile",
            "content.xml",
            MediaType.APPLICATION_XML_VALUE,
            "<content>test</content>".getBytes()
        );

        var businessDocument = new MockMultipartFile(
            "businessContent.businessDocument.document",
            "document.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "%PDF-1.7 test".getBytes()
        );

        return multipart(HttpMethod.POST, OUTBOUND_MESSAGE_URL)
            .file(businessContentFile)
            .file(businessDocument)
            .param("businessDomainIdentifier", BUSINESS_DOMAIN_IDENTIFIER)
            .param("backendMessageIdentifier", BACKEND_MESSAGE_IDENTIFIER)
            // business content properties
            .param("businessContent.businessDocument.aesType", "SIGNATURE_BASED")
            // AS4 properties — add all @NotNull nested fields here
            .param("as4Properties.conversationIdentifier", CONVERSATION_IDENTIFIER)
            .param("as4Properties.originalSender", "alice")
            .param("as4Properties.finalRecipient", "bob")
            .param("as4Properties.service.name", "Connector-TEST")
            .param("as4Properties.service.type", "urn:e-codex:services:")
            .param("as4Properties.action.name", "ConTest_Form")
            .param("as4Properties.fromParty.identifier", "BL")
            .param("as4Properties.fromParty.identifierType", PARTY_IDENTIFIER_TYPE)
            .param("as4Properties.fromParty.role", "GW")
            .param("as4Properties.toParty.identifier", "RE")
            .param("as4Properties.toParty.identifierType", PARTY_IDENTIFIER_TYPE)
            .param("as4Properties.toParty.role", "GW");
    }

    private ConnectorEvidenceTriggerMessageRequest buildValidEvidenceTriggerRequest() {
        return ConnectorEvidenceTriggerMessageRequest
            .builder()
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .identifiers(ConnectorEvidenceTriggerMessageIdentifiers
                             .builder()
                             .referenceToIdentifier(REFERENCE_TO_IDENTIFIER)
                             .build()
            )
            .build();
    }

    @Nested
    @DisplayName("POST (submit an outbound message)")
    class SubmitOutboundMessage {
        @Test
        void should_return_201_when_the_message_is_submitted() throws Exception {
            stubOutboundSubmissionSuccess();

            mockMvc.perform(buildValidOutboundMessageRequest()
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.identifier").isNotEmpty());

            verifyNoInteractions(outboundEvidenceMessageReceiverService);
        }

        @Test
        void should_build_the_command_from_the_parsed_request_when_the_message_is_submitted()
            throws Exception {
            stubOutboundSubmissionSuccess();

            mockMvc.perform(buildValidOutboundMessageRequest()
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated());

            verify(restOutboundMessageParser)
                .resolveBusinessDomainIdentifier(BUSINESS_DOMAIN_IDENTIFIER);

            var command = captureOutboundCommand();
            assertThat(command.businessDomainIdentifier()).isEqualTo(businessDomainIdentifier);
            assertThat(command.backendMessageIdentifier()).isEqualTo(BACKEND_MESSAGE_IDENTIFIER);
            assertThat(command.referenceToBackendMessageIdentifier()).isNull();
            assertThat(command.backendName()).isEqualTo(BACKEND_NAME);
            assertThat(command.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
            assertThat(command.businessContent()).isEqualTo(businessContent);
            assertThat(command.attachments()).isEqualTo(attachments);
        }

        @Test
        void should_map_the_as4_properties_when_the_message_is_submitted() throws Exception {
            stubOutboundSubmissionSuccess();

            mockMvc.perform(buildValidOutboundMessageRequest()
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated());

            var as4Properties = captureOutboundCommand().as4Properties();
            assertThat(as4Properties.originalSender()).isEqualTo("alice");
            assertThat(as4Properties.finalRecipient()).isEqualTo("bob");
            assertThat(as4Properties.conversationIdentifier()).isEqualTo(CONVERSATION_IDENTIFIER);
            assertThat(as4Properties.ebmsMessageIdentifier()).isNull();
            assertThat(as4Properties.service().name()).isEqualTo("Connector-TEST");
            assertThat(as4Properties.service().type()).isEqualTo("urn:e-codex:services:");
            assertThat(as4Properties.action().name()).isEqualTo("ConTest_Form");
            // fromParty must be resolved as INITIATOR, toParty as RESPONDER
            assertThat(as4Properties.fromParty()).isEqualTo(fromParty);
            assertThat(as4Properties.toParty()).isEqualTo(toParty);
        }

        @Test
        void should_return_201_when_the_message_has_a_detached_signature() throws Exception {
            stubOutboundSubmissionSuccess();

            var detachedSignature = new MockMultipartFile(
                "businessContent.businessDocument.detachedSignature.signature",
                "signature.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<document>signature</document>".getBytes()
            );

            var request = buildValidOutboundMessageRequest()
                .file(detachedSignature)
                .param("businessContent.businessDocument.detachedSignature.mimeType", "XML");

            mockMvc.perform(request.contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.identifier").isNotEmpty());

            var captor = ArgumentCaptor.forClass(ConnectorOutboundMessageBusinessContent.class);
            verify(restOutboundMessageParser).toBusinessContent(captor.capture());
            var boundSignature = captor.getValue().businessDocument().detachedSignature();
            assertThat(boundSignature).isNotNull();
            assertThat(boundSignature.signature().getOriginalFilename())
                .isEqualTo("signature.xml");

            verifyNoInteractions(outboundEvidenceMessageReceiverService);
        }

        @Test
        void should_forward_the_attachment_identifiers_when_the_message_has_attachments()
            throws Exception {
            stubOutboundSubmissionSuccess();

            var request = buildValidOutboundMessageRequest()
                .param("attachments", "attachment-1", "attachment-2");

            mockMvc.perform(request.contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated());

            verify(restOutboundMessageParser)
                .toAttachments(List.of("attachment-1", "attachment-2"));
            assertThat(captureOutboundCommand().attachments()).isEqualTo(attachments);
        }

        @Test
        void should_return_400_when_the_message_is_invalid() throws Exception {
            mockMvc.perform(
                       multipart(HttpMethod.POST, OUTBOUND_MESSAGE_URL)
                           .file(MultipartFileTestFixtures.createPart(
                               "businessXMLDocument",
                               MediaType.TEXT_XML_VALUE,
                               "raw/Form_A.xml",
                               "Form_A.xml"
                           ))
                           .file(MultipartFileTestFixtures.createPart(
                               "businessPDFDocument",
                               MediaType.APPLICATION_PDF_VALUE,
                               "raw/Form_A.pdf",
                               "Form_A.pdf"
                           ))
                           .contentType(MediaType.MULTIPART_FORM_DATA)
                   )
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(
                restOutboundMessageParser,
                outboundBusinessMessageReceiverService,
                outboundEvidenceMessageReceiverService
            );
        }
    }

    @Nested
    @DisplayName("POST (submit an evidence trigger message)")
    class SubmitEvidenceTrigger {
        @Test
        void should_return_201_when_the_trigger_is_submitted() throws Exception {
            when(outboundEvidenceMessageReceiverService.execute(any()))
                .thenReturn(TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage());
            when(backendClientVerifierService.getBackendClient(any())).thenReturn(BACKEND_NAME);

            mockMvc.perform(
                       post(EVIDENCE_TRIGGER_URL)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(buildValidEvidenceTriggerRequest()))
                   )
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.identifier").isNotEmpty());

            var captor = ArgumentCaptor.forClass(ConnectorOutboundEvidenceMessageCommand.class);
            verify(outboundEvidenceMessageReceiverService).execute(captor.capture());
            var command = captor.getValue();
            assertThat(command.evidenceType()).isEqualTo(ConnectorEvidenceType.DELIVERY);
            assertThat(command.referenceToIdentifier()).isEqualTo(REFERENCE_TO_IDENTIFIER);
            assertThat(command.backendMessageIdentifier()).isNull();
            assertThat(command.backendName()).isEqualTo(BACKEND_NAME);

            verifyNoInteractions(outboundBusinessMessageReceiverService, restOutboundMessageParser);
        }

        @Test
        void should_return_400_when_the_payload_is_invalid() throws Exception {
            var request = ConnectorEvidenceTriggerMessageRequest
                .builder()
                .evidenceType(null)
                .identifiers(buildValidEvidenceTriggerRequest().identifiers())
                .build();

            mockMvc.perform(post(EVIDENCE_TRIGGER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(
                outboundBusinessMessageReceiverService,
                outboundEvidenceMessageReceiverService
            );
        }
    }
}

