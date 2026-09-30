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
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeTruststoreDto;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("ConnectorUpdateProcessingModeTruststoreIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorUpdateProcessingModeTruststoreIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/processing-modes/%s/truststore";
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @WithPmodeData
    void should_update_processing_mode_truststore() {
        var parts = creationParts();

        apiClient.patch()
                 .uri(URL.formatted("4f10aed9-2e5f-4780-87f7-5fe1070d5ccf"))
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorProcessingModeTruststoreDto>() {
                 })
                 .value(truststoreDto -> {
                     assertThat(truststoreDto).isNotNull();
                     assertThat(truststoreDto.filename()).isNotEmpty();
                     assertThat(truststoreDto.password()).isNotEmpty();
                     assertThat(truststoreDto.type()).isNotNull();
                     assertThat(truststoreDto.certificateInfo().size()).isGreaterThan(0);
                 });
    }

    @Test
    @WithInitialData
    void should_failed_when_pmode_does_not_exist() {
        var parts = creationParts();

        apiClient.patch()
                 .uri(URL.formatted("unknown-identifier"))
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().isNotFound();
    }

    @Test
    @WithInitialData
    void should_failed_when_not_authenticated() {
        var parts = creationParts();

        apiClient.patch()
                 .uri(URL.formatted("unknown-identifier"))
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().is4xxClientError();
    }

    private MultiValueMap<String, Object> creationParts() {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add(
            "truststoreFile",
            FilePartTestFixtures.filePart(
                "truststore.jks",
                FileTestFixtures.readAsBytes("pmode/truststore.jks"),
                MediaType.APPLICATION_OCTET_STREAM
            )
        );

        parts.add("password", "12345");

        return parts;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/user.sql",
    })
    private @interface WithInitialData {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/user.sql",
    })
    private @interface WithPmodeData {
    }
}
