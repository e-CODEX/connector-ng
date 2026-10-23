/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest;


import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ErrorDispatchSecurityIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/link-partners?linkType=%s";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void invalidEnum_whenAuthenticated_returns_400_not_403() {
        apiClient.get()
            .uri(String.format(URL, "INVALID"))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void unknownUrl_withoutToken_returns_401_not_403() {
        apiClient.get().uri("/api/v1/does-not-exist").exchange().expectStatus().isUnauthorized();
    }
}
