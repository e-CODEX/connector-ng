/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.auth.logout;

import static eu.ecodex.connector.domain.model.user.ConnectorUser.DEFAULT_ADMIN_PASSWORD;
import static eu.ecodex.connector.domain.model.user.ConnectorUser.DEFAULT_ADMIN_USER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorLogoutUserIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @MockitoBean
    private Clock clock;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void logout_should_succeeded_for_default_admin_user() {
        var loginTime = Instant.parse("2026-08-18T10:00:00Z");

        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(DEFAULT_ADMIN_USER_NAME)
            .password(DEFAULT_ADMIN_PASSWORD)
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class)
            .getResponseBody();

        assertThat(loginResponse).isNotNull();
        var accessToken = loginResponse.accessToken();
        var refreshToken = loginResponse.refreshToken();
        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        var logoutRequest = ConnectorLogoutRequest
            .builder()
            .refreshToken(refreshToken)
            .build();

        apiClient.post()
            .uri("/api/v1/auth/logout")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(logoutRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class);

        apiClient.get()
            .uri("/api/v1/admin/attachments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .exchange()
            .expectStatus()
            .isOk();

        // Move clock past the 15-minute expiration
        when(clock.instant()).thenReturn(Instant.parse("2026-08-18T10:16:00Z"));

        apiClient.get()
            .uri("/api/v1/admin/attachments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    void logout_withoutToken_should_returns_401() {
        var request = new ConnectorLogoutRequest("refresh-token-abc");

        apiClient.post()
            .uri("/api/v1/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
