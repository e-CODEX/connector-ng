/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.swagger;

import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("Swagger basic authentication REST")
public class SwaggerBasicAuthIT extends AbstractIntegrationTest {
    private static final String API_DOCS = "/v3/api-docs/admin";

    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_reject_api_docs_without_credentials() {
        apiClient.get()
                 .uri(API_DOCS)
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    void should_reject_api_docs_with_wrong_credentials() {
        apiClient.get()
                 .uri(API_DOCS)
                 .headers(headers -> headers.setBasicAuth("swagger", "wrong"))
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    void should_serve_api_docs_with_valid_credentials() {
        apiClient.get()
                 .uri(API_DOCS)
                 .headers(headers -> headers.setBasicAuth("swagger", "swagger"))
                 .exchange()
                 .expectStatus()
                 .isOk();
    }

    @Test
    void should_not_accept_basic_credentials_on_rest_api() {
        apiClient.get()
                 .uri("/api/v1/admin/jms/queues/stats")
                 .headers(headers -> headers.setBasicAuth("swagger", "swagger"))
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    void should_not_accept_jwt_on_api_docs() {
        apiClient.get()
                 .uri(API_DOCS)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }
}
