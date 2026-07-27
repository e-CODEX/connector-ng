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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRetrieveProcessingMode;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.pmode.ConnectorProcessingModeAdminController;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@WebMvcTest(ConnectorProcessingModeAdminController.class)
@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:LineLength"})
public class ConnectorProcessingModeAdminControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/admin/processing-modes";
    private static final String BUSINESS_DOMAIN = "default";
    private static final String DESCRIPTION = "default processing mode for domain";
    private static final String TRUSTSTORE_PASSWORD = "changeit";
    private static final byte[] PMODE_CONTENT =
        "<processingMode>test content</processingMode>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] TRUSTSTORE_CONTENT =
        "binary-truststore-content".getBytes(StandardCharsets.UTF_8);

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

    private static MockMultipartFile processingModeFile(String contentType) {
        return new MockMultipartFile(
            "processingModeFile", "processing-mode.xml", contentType, PMODE_CONTENT);
    }

    private static MockMultipartFile truststoreFile() {
        return new MockMultipartFile(
            "truststore.truststoreFile", "truststore.p12",
            MediaType.APPLICATION_OCTET_STREAM_VALUE, TRUSTSTORE_CONTENT
        );
    }

    private static MockMultipartHttpServletRequestBuilder creationRequest() {
        return multipart(HttpMethod.POST, URL)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("businessDomainIdentifier", BUSINESS_DOMAIN)
            .param("description", DESCRIPTION);
    }

    @Nested
    class Creation {
        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
        void should_send_201_response_when_creating_processing_mode(String contentType)
            throws Exception {
            when(registerProcessingModeService.execute(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

            mockMvc.perform(creationRequest()
                                .file(processingModeFile(contentType))
                                .file(truststoreFile())
                                .param("truststore.password", TRUSTSTORE_PASSWORD))
                   .andExpect(status().isCreated());

            verify(registerProcessingModeService).execute(any(), any());
        }

        @Test
        void should_send_400_response_when_processing_mode_file_is_not_xml() throws Exception {
            mockMvc.perform(creationRequest()
                                .file(processingModeFile(MediaType.TEXT_PLAIN_VALUE))
                                .file(truststoreFile())
                                .param("truststore.password", TRUSTSTORE_PASSWORD))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(registerProcessingModeService);
        }

        @Test
        void should_send_400_response_when_processing_mode_file_is_missing() throws Exception {
            mockMvc.perform(creationRequest()
                                .file(truststoreFile())
                                .param("truststore.password", TRUSTSTORE_PASSWORD))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(registerProcessingModeService);
        }

        @Test
        void should_send_400_response_when_truststore_file_is_missing() throws Exception {
            mockMvc.perform(creationRequest()
                                .file(processingModeFile(MediaType.APPLICATION_XML_VALUE))
                                .param("truststore.password", TRUSTSTORE_PASSWORD))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(registerProcessingModeService);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @EmptySource
        void should_send_400_response_when_truststore_password_is_blank(String password)
            throws Exception {
            mockMvc.perform(creationRequest()
                                .file(processingModeFile(MediaType.APPLICATION_XML_VALUE))
                                .file(truststoreFile())
                                .param("truststore.password", password))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(registerProcessingModeService);
        }

        @Test
        void should_send_400_response_when_business_domain_identifier_is_missing()
            throws Exception {
            mockMvc.perform(multipart(HttpMethod.POST, URL)
                                .file(processingModeFile(MediaType.APPLICATION_XML_VALUE))
                                .file(truststoreFile())
                                .param("description", DESCRIPTION)
                                .param("truststore.password", TRUSTSTORE_PASSWORD)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());

            verifyNoInteractions(registerProcessingModeService);
        }
    }

    @Nested
    class Listing {
        @Test
        void should_list_processing_modes_successfully() {
            when(listProcessingModeService.execute())
                .thenReturn(List.of(ProcessingModeTestFixtures.createWithBusinessDomain()));

            var responseBody = apiClient.get()
                                        .uri(URL)
                                        .exchange()
                                        .expectStatus().isOk()
                                        .returnResult(ConnectorProcessingModeDto[].class)
                                        .getResponseBody();

            assertThat(responseBody).isNotNull().hasSize(1);
        }
    }

    @Nested
    class Retrieval {
        @Test
        void should_retrieve_a_processing_mode_successfully() {
            when(retrieveProcessingModeService.execute(any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

            var responseBody = apiClient.get()
                                        .uri(URL + "/{identifier}", "test-identifier")
                                        .exchange()
                                        .expectStatus().isOk()
                                        .returnResult(ConnectorProcessingModeDetailDto.class)
                                        .getResponseBody();

            assertThat(responseBody).isNotNull();
        }

        @Test
        void should_return_404_not_found_when_retrieving_a_processing_mode_with_unknown_identifier() {
            doThrow(ConnectorProcessingModeNotFoundException.class)
                .when(retrieveProcessingModeService).execute(any());

            apiClient.get()
                     .uri(URL + "/unknown-identifier")
                     .exchange()
                     .expectStatus().isNotFound();
        }
    }
}
