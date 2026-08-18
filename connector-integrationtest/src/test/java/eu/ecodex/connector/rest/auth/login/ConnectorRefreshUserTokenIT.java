/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
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

class ConnectorRefreshUserTokenIT extends AbstractIntegrationTest {
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
    void request_should_failed_when_access_token_has_expired() {
        var loginTime = Instant.parse("2026-08-18T10:00:00Z");

        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // Login → token expires at 10:15
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(ConnectorUser.DEFAULT_ADMIN_USER_NAME)
            .password(ConnectorUser.DEFAULT_ADMIN_PASSWORD)
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

        // Token is still valid at 10:10
        when(clock.instant()).thenReturn(Instant.parse("2026-08-18T10:10:00Z"));

        // Verify that the token works before it expires
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
    @Sql("classpath:sql/user.sql")
    void should_refresh_access_token_when_access_token_has_expired() {
        var loginTime = Instant.parse("2026-08-18T10:00:00Z");

        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(ConnectorUser.DEFAULT_ADMIN_USER_NAME)
            .password(ConnectorUser.DEFAULT_ADMIN_PASSWORD)
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
        var expiredAccessToken = loginResponse.accessToken();
        var refreshToken = loginResponse.refreshToken();

        var refreshTime = Instant.parse("2026-08-18T10:16:00Z");
        when(clock.instant()).thenReturn(refreshTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // Old access token must no longer work
        apiClient.get()
            .uri("/api/v1/admin/attachments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccessToken)
            .exchange()
            .expectStatus()
            .isUnauthorized();

        // Refresh using the valid refresh token
        var refreshRequest = ConnectorRefreshTokenRequest
            .builder()
            .refreshToken(refreshToken)
            .build();

        var refreshResponse = apiClient.post()
            .uri("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(refreshRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class)
            .getResponseBody();

        assertThat(refreshResponse).isNotNull();
        var newAccessToken = refreshResponse.accessToken();

        assertThat(newAccessToken).isNotBlank();
        assertThat(newAccessToken).isNotEqualTo(expiredAccessToken);

        // New access token must work
        apiClient.get()
            .uri("/api/v1/admin/attachments")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
            .exchange()
            .expectStatus()
            .isOk();
    }
}
