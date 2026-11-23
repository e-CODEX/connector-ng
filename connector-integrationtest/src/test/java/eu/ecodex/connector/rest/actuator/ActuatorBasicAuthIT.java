/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.actuator;

import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("Actuator basic authentication REST")
public class ActuatorBasicAuthIT extends AbstractIntegrationTest {
    private static final String HEALTH = "/actuator/health";

    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_reject_actuator_health_without_credentials() {
        apiClient.get()
                 .uri(HEALTH)
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    void should_reject_actuator_health_with_wrong_credentials() {
        apiClient.get()
                 .uri(HEALTH)
                 .headers(headers -> headers.setBasicAuth("swagger", "wrong"))
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    void should_serve_actuator_health_with_valid_credentials() {
        apiClient.get()
                 .uri(HEALTH)
                 .headers(headers -> headers.setBasicAuth("swagger", "swagger"))
                 .exchange()
                 .expectStatus()
                 .isOk();
    }

    @Test
    void should_not_accept_jwt_on_actuator_health() {
        apiClient.get()
                 .uri(HEALTH)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }
}
