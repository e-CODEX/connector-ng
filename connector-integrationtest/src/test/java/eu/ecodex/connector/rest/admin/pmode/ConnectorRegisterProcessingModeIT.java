/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.pmode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.FilePartTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRegisterProcessingModeIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/processing-modes";
    private static final String BUSINESS_DOMAIN = "default_business_domain";
    private static final String DESCRIPTION = "test processing mode";
    private static final String TRUSTSTORE_PASSWORD = "changeit";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_succeed_to_create_processing_mode() {
        var response = apiClient.post()
                                .uri(URL)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(creationParts(BUSINESS_DOMAIN))
                                .exchange()
                                .expectStatus().isCreated()
                                .returnResult(ConnectorProcessingModeDto.class)
                                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.businessDomainIdentifier()).isEqualTo(BUSINESS_DOMAIN);
        assertThat(response.description()).isEqualTo(DESCRIPTION);
    }

    @Test
    void should_fail_to_create_a_pmode_if_the_specified_business_domain_does_not_exist() {
        apiClient.post()
                 .uri(URL)
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(creationParts("fake_business_domain"))
                 .exchange()
                 .expectStatus().isNotFound();
    }

    @Test
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql"
    })
    void should_fail_to_create_a_pmode_if_the_specified_business_domain_has_already_one() {
        apiClient.post()
                 .uri(URL)
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(creationParts(BUSINESS_DOMAIN))
                 .exchange()
                 .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_fail_to_create_a_pmode_if_the_truststore_is_missing() {
        var parts = creationParts(BUSINESS_DOMAIN);
        parts.remove("truststore.truststoreFile");

        apiClient.post()
                 .uri(URL)
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().isBadRequest();
    }

    private MultiValueMap<String, Object> creationParts(String businessDomainIdentifier) {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add(
            "processingModeFile",
            FilePartTestFixtures.filePart(
                "pmode.xml",
                FileTestFixtures.readAsString("pmode/pmode.xml").getBytes(StandardCharsets.UTF_8),
                MediaType.APPLICATION_XML
            )
        );

        parts.add(
            "truststore.truststoreFile",
            FilePartTestFixtures.filePart(
                "truststore.p12",
                FileTestFixtures.readAsBytes("truststore/truststore.p12"),
                MediaType.APPLICATION_OCTET_STREAM
            )
        );

        parts.add("businessDomainIdentifier", businessDomainIdentifier);
        parts.add("description", DESCRIPTION);
        parts.add("truststore.password", TRUSTSTORE_PASSWORD);

        return parts;
    }
}
