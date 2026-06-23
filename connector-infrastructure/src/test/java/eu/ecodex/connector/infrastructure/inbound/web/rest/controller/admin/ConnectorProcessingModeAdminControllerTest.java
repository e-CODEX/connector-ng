/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.JsonTestFixtures;
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.pmode.ConnectorListProcessingMode;
import eu.ecodex.connector.application.service.usecase.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.service.usecase.pmode.ConnectorRetrieveProcessingMode;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.pmode.ConnectorProcessingModeAdminController;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
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
import org.springframework.test.web.servlet.client.RestTestClient;

@AutoConfigureRestTestClient
@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength"})
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorProcessingModeAdminController.class)
public class ConnectorProcessingModeAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestTestClient apiClient;
    @MockitoBean
    private ConnectorRegisterProcessingMode registerProcessingModeService;
    @MockitoBean
    private ConnectorListProcessingMode listProcessingModeService;
    @MockitoBean
    private ConnectorRetrieveProcessingMode retrieveProcessingModeService;

    // save processing mode
    @Test
    void should_send_201_response_when_creating_processing_mode_with_application_xml() throws Exception {
        when(registerProcessingModeService.execute(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                JsonTestFixtures.readJson("json/processing-mode.creation.json").getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
                                .file(metadataFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated());
    }

    @Test
    void should_send_201_response_when_creating_processing_mode_with_text_xml_file() throws Exception {
        when(registerProcessingModeService.execute(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.TEXT_XML_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                JsonTestFixtures.readJson("json/processing-mode.creation.json").getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
                                .file(metadataFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated());
    }

    @Test
    void should_send_400_response_when_creating_processing_mode_if_pmode_file_type_is_not_xml() throws Exception {
        when(registerProcessingModeService.execute(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

        var processingModeXml = new MockMultipartFile(
                "processingModeXmlFile",
                "processing-mode.xml",
                MediaType.TEXT_PLAIN_VALUE,
                "<processingMode>test content</processingMode>".getBytes()
        );

        var metadataFile = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                JsonTestFixtures.readJson("json/processing-mode.creation.json").getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/admin/processing-modes")
                                .file(processingModeXml)
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

    // find all
    @Test
    void should_find_all_processing_modes_successfully() {
        when(listProcessingModeService.execute())
                .thenReturn(List.of(ProcessingModeTestFixtures.createWithBusinessDomain()));

        var response = apiClient.get()
                                .uri("/api/v1/admin/processing-modes")
                                .exchange()
                                .expectStatus()
                                .isOk()
                                .returnResult(ConnectorProcessingModeDto[].class);

        var responseBody = response.getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).hasSize(1);
    }
}
