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

import eu.ecodex.connector.JsonTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.MultipartFileTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message.ConnectorOutboundMessageController;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestTestClient
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorOutboundMessageController.class)
public class ConnectorOutboundMessageControllerTest {
    @MockitoBean
    private ConnectorOutboundMessageReceiver messageStagingService;
    @MockitoBean
    private ConnectorBackendClientVerifier backendClientVerifierService;
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getMetadataJson")
    void should_send_201_response_when_submitting_outbound_message(String jsonBody) throws Exception {
        // TODO set appropriate response
        when(messageStagingService.register(any()))
                .thenReturn(MessageTestFixtures.createValidOutboundBusinessMessage());
        when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());

        var metadataFile = new MockMultipartFile(
                "messageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                JsonTestFixtures.readJson(jsonBody).getBytes()
        );

        mockMvc.perform(
                       multipart(HttpMethod.POST, "/api/v1/messages/outbound")
                               .file(MultipartFileTestFixtures.createPart(
                                       "businessXMLDocument", MediaType.TEXT_XML_VALUE, "raw/Form_A.xml", "Form_A.xml"))
                               .file(metadataFile)
                               .contentType(MediaType.MULTIPART_FORM_DATA)
               )
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void should_send_400_response_when_submitting_non_valid_outbound_message() throws Exception {
        mockMvc.perform(
                       multipart(HttpMethod.POST, "/api/v1/messages/outbound")
                               .file(MultipartFileTestFixtures.createPart(
                                       "businessXMLDocument", MediaType.TEXT_XML_VALUE, "raw/Form_A.xml", "Form_A.xml"))
                               .file(MultipartFileTestFixtures.createPart(
                                       "businessPDFDocument", MediaType.APPLICATION_PDF_VALUE, "raw/Form_A.pdf", "Form_A.pdf"))
                               .contentType(MediaType.MULTIPART_FORM_DATA)
               )
               .andExpect(status().isBadRequest());
    }

     private static Stream<String> getMetadataJson() {
        return Stream.of(
                "json/message/outbound/creation.json",
                "json/message/outbound/creation-without-attachments.json",
                "json/message/outbound/creation-without-business-domain.json",
                "json/message/outbound/creation-without-detached-signature.json"
        );
    }
}
