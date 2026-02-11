/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.presentation.rest.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.application.presentation.rest.request.pmode.ConnectorKeystoreCreationRequest;
import eu.ecodex.connector.application.presentation.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystoreType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureRestTestClient
@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength"})
@WebMvcTest(ConnectorProcessingModeAdminController.class)
public class ConnectorProcessingModeAdminControllerTest {
    private final ConnectorProcessingModeCreationRequest metadata =
            ConnectorProcessingModeCreationRequest
                    .builder()
                    .description("test processing mode")
                    .businessDomainIdentifier("fake_business_domain")
                    .truststore(
                            ConnectorKeystoreCreationRequest
                                    .builder()
                                    .description("test truststore")
                                    .password("12345")
                                    .type(ConnectorKeystoreType.JKS)
                                    .build()
                    )
                    .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestTestClient apiClient;
    @MockitoBean
    private ConnectorProcessingModeService connectorProcessingModeService;

    @Test
    void should_send_201_response_when_creating_processing_mode_with_application_xml() throws Exception {
        when(connectorProcessingModeService.register(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var truststore = new MockMultipartFile(
                "truststoreFile",
                "truststore.jks",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "truststore-content".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                asJsonString(metadata).getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
                                .file(truststore)
                                .file(metadataFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated());
    }

    @Test
    void should_send_201_response_when_creating_processing_mode_with_text_xml_file() throws Exception {
        when(connectorProcessingModeService.register(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.TEXT_XML_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var truststore = new MockMultipartFile(
                "truststoreFile",
                "truststore.jks",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "truststore-content".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                asJsonString(metadata).getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
                                .file(truststore)
                                .file(metadataFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated());
    }

    @Test
    void should_send_400_response_when_creating_processing_mode_if_pmode_file_type_is_not_xml() throws Exception {
        when(connectorProcessingModeService.register(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.TEXT_PLAIN_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var truststore = new MockMultipartFile(
                "truststoreFile",
                "truststore.jks",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "truststore-content".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                asJsonString(metadata).getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
                                .file(truststore)
                                .file(metadataFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isBadRequest());
    }

    @Test
    void should_send_400_response_when_creating_processing_mode_if_request_body_is_invalid() {
        apiClient.post()
                 .uri("/api/v1/admin/processing-modes")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body("{}")
                 .exchange()
                 .expectStatus().isBadRequest();
    }

    private String asJsonString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
