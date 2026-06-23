/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.pmode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.FilePartTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tools.jackson.databind.ObjectMapper;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorProcessingModeIT extends AbstractIntegrationTest {
    private final ConnectorProcessingModeCreationRequest metadata =
            ConnectorProcessingModeCreationRequest
                    .builder()
                    .description("test processing mode")
                    .businessDomainIdentifier("default_business_domain")
                    .build();
    @Autowired
    private RestTestClient apiClient;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    /*
     * By default, a business domain is created at the startup if no one exists
     * We are going to use it for our tests
     */

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_succeed_to_create_processing_mode() {
        var parts = producePart();

        var jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        var metadataPart = new HttpEntity<>(objectMapper.writeValueAsString(metadata), jsonHeaders);

        parts.add("metadata", metadataPart);

        apiClient.post()
                 .uri("/api/v1/admin/processing-modes")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().isCreated()
                 .returnResult(ConnectorProcessingModeDto.class);
    }

    @Test
    void should_fail_to_create_a_pmode_if_the_specified_business_domain_does_not_exist() {
        var parts = producePart();

        var jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        var updatedMetadata = metadata
                .toBuilder()
                .businessDomainIdentifier("fake_business_domain")
                .build();

        var metadataPart = new HttpEntity<>(
                objectMapper.writeValueAsString(updatedMetadata),
                jsonHeaders
        );

        parts.add("metadata", metadataPart);

        var response = apiClient.post()
                                .uri("/api/v1/admin/processing-modes")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(parts)
                                .exchange()
                                .expectStatus().is4xxClientError()
                                .returnResult(ConnectorProcessingModeDto.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql"
    })
    void should_fail_to_create_a_pmode_if_the_specified_business_domain_has_already_one() {
        var parts = producePart();

        var jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        var metadataPart = new HttpEntity<>(objectMapper.writeValueAsString(metadata), jsonHeaders);

        parts.add("metadata", metadataPart);

        var response = apiClient.post()
                                .uri("/api/v1/admin/processing-modes")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(parts)
                                .exchange()
                                .expectStatus().is4xxClientError()
                                .returnResult(ConnectorProcessingModeDto.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql"
    })
    void should_succeed_to_get_processing_modes() {
        var response = apiClient.get()
                                .uri("/api/v1/admin/business-domains")
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(ConnectorProcessingModeDto[].class);

        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).hasSize(1);
    }

    private MultiValueMap<String, Object> producePart() {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add(
                "processingModeXmlFile",
                FilePartTestFixtures.filePart(
                        "pmode.xml",
                        FileTestFixtures.readAsString("pmode/pmode.xml").getBytes(),
                        MediaType.APPLICATION_XML
                )
        );

        return parts;
    }
}
