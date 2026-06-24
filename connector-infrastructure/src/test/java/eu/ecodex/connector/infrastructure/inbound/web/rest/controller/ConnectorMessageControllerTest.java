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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.MultipartFileTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message.ConnectorMessageController;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureRestTestClient
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorMessageController.class)
public class ConnectorMessageControllerTest {
    private static final String URL = "/api/v1/messages/outbound";
    @MockitoBean
    private ConnectorOutboundMessageReceiver messageStagingService;
    @MockitoBean
    private ConnectorBackendClientVerifier backendClientVerifierService;
    @MockitoBean
    private ConnectorUploadAttachments uploadAttachmentsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_send_201_created_when_submitting_outbound_message() throws Exception {
        when(messageStagingService.register(any()))
                .thenReturn(MessageTestFixtures.createOutboundBusinessMessage());
        when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());
        when(uploadAttachmentsService.execute(any())).thenReturn(
                List.of(MessageAttachmentTestFixtures.createAttachment())
        );

        mockMvc.perform(buildValidRequest().contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void should_send_201_created_when_submitting_outbound_message_with_detached_signature()
            throws Exception {
        when(messageStagingService.register(any()))
                .thenReturn(MessageTestFixtures.createOutboundBusinessMessage());
        when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());
        when(uploadAttachmentsService.execute(any())).thenReturn(
                List.of(MessageAttachmentTestFixtures.createAttachment())
        );

        var detachedSignature = new MockMultipartFile(
                "businessContent.businessDocument.document.detachedSignature.signature",
                "signature.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<document>signature</document>".getBytes()
        );

        var request = buildValidRequest()
                .file(detachedSignature)
                .param("businessContent.businessDocument.detachedSignature.mimeType", "XML");

        mockMvc.perform(request.contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void should_send_400_response_when_submitting_non_valid_outbound_message() throws Exception {
        mockMvc.perform(
                       multipart(HttpMethod.POST, URL)
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
    }

    private MockMultipartHttpServletRequestBuilder buildValidRequest() {
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

        return MockMvcRequestBuilders.multipart(HttpMethod.POST, URL)
                                     .file(businessContent)
                                     .file(businessDocument)
                                     .param("businessDomainIdentifier", "default_business_domain")
                                     .param(
                                             "backendMessageIdentifier",
                                             "56ed1a8f-b089-4615-9ddd-da302a665a11@backend_system"
                                     )
                                     // business content properties
                                     .param(
                                             "businessContent.businessDocument.aesType",
                                             "SIGNATURE_BASED"
                                     )
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
}
