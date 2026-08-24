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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MultipartFileTestFixtures;
import eu.ecodex.connector.TriggeredEvidenceMessageTestFixtures;
import eu.ecodex.connector.application.port.api.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message.ConnectorMessageController;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageIdentifiers;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageRequest;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @MockitoBean
    private ConnectorOutboundBusinessMessageReceiver outboundBusinessMessageReceiverService;
    @MockitoBean
    private ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService;
    @MockitoBean
    private ConnectorBackendClientVerifier backendClientVerifierService;
    @MockitoBean
    private ConnectorUploadAttachments uploadAttachmentsService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private void stubOutboundSubmissionSuccess() {
        when(outboundBusinessMessageReceiverService.execute(any()))
            .thenReturn(BusinessMessageTestFixtures.createOutboundMessage());
        when(backendClientVerifierService.getBackendClient(any()))
            .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());
        when(uploadAttachmentsService.execute(any()))
            .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));
    }

    private MockMultipartHttpServletRequestBuilder buildValidOutboundMessageRequest() {
        var businessContent = new MockMultipartFile(
            "businessContent.contentFile",
            "content.xml",
            MediaType.APPLICATION_XML_VALUE,
            "<content>test</content>".getBytes()
        );

        var businessDocument = new MockMultipartFile(
            "businessContent.businessDocument.document",
            "document.xml",
            MediaType.APPLICATION_PDF_VALUE,
            "<document>test</document>".getBytes()
        );

        return multipart(HttpMethod.POST, OUTBOUND_MESSAGE_URL)
            .file(businessContent)
            .file(businessDocument)
            .param("businessDomainIdentifier", "default_business_domain")
            .param(
                "backendMessageIdentifier",
                "56ed1a8f-b089-4615-9ddd-da302a665a11@backend_system"
            )
            // business content properties
            .param("businessContent.businessDocument.aesType", "SIGNATURE_BASED")
            // AS4 properties — add all @NotNull nested fields here
            .param(
                "as4Properties.conversationIdentifier",
                "e6a173ec-de21-46dc-8a19-63a6cb74915d"
            )
            .param("as4Properties.originalSender", "alice")
            .param("as4Properties.finalRecipient", "bob")
            .param("as4Properties.service.name", "Connector-TEST")
            .param("as4Properties.service.type", "urn:e-codex:services:")
            .param("as4Properties.action.name", "ConTest_Form")
            .param("as4Properties.fromParty.identifier", "BL")
            .param(
                "as4Properties.fromParty.identifierType",
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
            )
            .param("as4Properties.fromParty.role", "GW")
            .param("as4Properties.toParty.identifier", "RE")
            .param(
                "as4Properties.toParty.identifierType",
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
            )
            .param("as4Properties.toParty.role", "GW");
    }

    private ConnectorEvidenceTriggerMessageRequest buildValidEvidenceTriggerRequest() {
        return ConnectorEvidenceTriggerMessageRequest
            .builder()
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .identifiers(ConnectorEvidenceTriggerMessageIdentifiers
                             .builder()
                             .referenceToIdentifier("msg-ref-001")
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
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            verifyNoInteractions(outboundEvidenceMessageReceiverService);
        }

        @Test
        void should_return_201_when_the_message_has_a_detached_signature() throws Exception {
            stubOutboundSubmissionSuccess();

            var detachedSignature = new MockMultipartFile(
                "businessContent.businessDocument.document.detachedSignature.signature",
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

            verifyNoInteractions(outboundEvidenceMessageReceiverService);
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
            when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());

            mockMvc.perform(
                       post(EVIDENCE_TRIGGER_URL)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(buildValidEvidenceTriggerRequest()))
                   )
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.identifier").isNotEmpty());

            verifyNoInteractions(outboundBusinessMessageReceiverService);
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
