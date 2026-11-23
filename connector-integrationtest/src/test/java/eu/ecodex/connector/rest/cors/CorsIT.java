/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.cors;

import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("CorsIT REST")
@TestPropertySource(properties = "connector.cors.allowed-origins=https://admin.example.org")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class CorsIT extends AbstractIntegrationTest {
    private static final String ALLOWED_ORIGIN = "https://admin.example.org";
    private static final String OTHER_ORIGIN = "https://evil.example.org";

    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_answer_the_preflight_of_an_allowed_origin() {
        apiClient.method(HttpMethod.OPTIONS)
            .uri("/api/v1/auth/login")
            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type")
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN)
            .expectHeader()
            .doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }

    @Test
    void should_refuse_the_preflight_of_another_origin() {
        apiClient.method(HttpMethod.OPTIONS)
            .uri("/api/v1/auth/login")
            .header(HttpHeaders.ORIGIN, OTHER_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .exchange()
            .expectStatus()
            .isForbidden()
            .expectHeader()
            .doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    @Test
    void should_refuse_a_request_header_which_is_not_allowed() {
        apiClient.method(HttpMethod.OPTIONS)
            .uri("/api/v1/auth/login")
            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "x-custom-header")
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    void should_apply_cors_to_the_actuator() {
        // the pre-flight request carries no credentials: it is answered before the authentication
        apiClient.method(HttpMethod.OPTIONS)
            .uri("/actuator/health")
            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization")
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);

        // the request itself still has to authenticate, but the browser can read the answer
        apiClient.get()
            .uri("/actuator/health")
            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectHeader()
            .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);

        apiClient.get()
            .uri("/actuator/health")
            .header(HttpHeaders.ORIGIN, OTHER_ORIGIN)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    void should_not_apply_cors_outside_of_the_rest_api_and_the_actuator() {
        apiClient.get()
            .uri("/v3/api-docs")
            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .exchange()
            .expectHeader()
            .doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }
}
