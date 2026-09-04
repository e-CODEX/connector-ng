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
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
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
    private ConnectorRefreshTokenRepository refreshTokenRepository;
    @Autowired
    private ConnectorUserRepository userRepository;
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
    void request_should_failed_when_invalid_refresh_token() {
        var loginTime = Instant.parse("2026-08-18T10:00:00Z");
        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var refreshToken = UUID.randomUUID().toString();
        var refreshRequest = ConnectorRefreshTokenRequest
            .builder()
            .refreshToken(refreshToken)
            .build();

        // Refresh using admin token
        apiClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(refreshRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void request_should_failed_when_refresh_token_user_does_not_match_access_token_user() {
        var loginTime = Instant.parse("2026-08-18T10:00:00Z");
        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var refreshToken = "291b571a-1511-45a0-b990-368b5139011d"; // test-user-it refresh token
        var refreshRequest = ConnectorRefreshTokenRequest
            .builder()
            .refreshToken(refreshToken)
            .build();

        // Refresh using admin token
        apiClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(refreshRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void request_should_create_new_access_token_and_rotate_refresh_token_when_access_token_has_expired() {
        // Before login
        var userUuid = "d43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_admin";
        var revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        var notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).hasSize(3);
        assertThat(notRevoked).hasSize(1);

        // Move clock past the 15-minute expiration
        when(clock.instant()).thenReturn(Instant.parse("2026-08-18T10:16:00Z"));

        var existing = notRevoked.getFirst().token();
        var refreshRequest = ConnectorRefreshTokenRequest
            .builder()
            .refreshToken(existing)
            .build();

        var refreshResponse = apiClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(refreshRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class)
            .getResponseBody();

        revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).hasSize(4);
        assertThat(notRevoked).hasSize(1);

        assertThat(refreshResponse).isNotNull();
        var newAccessToken = refreshResponse.accessToken();

        assertThat(newAccessToken).isNotBlank();
        assertThat(refreshResponse.refreshToken()).isNotEqualTo(existing);

        // New access token must work
        apiClient.get()
            .uri("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
            .exchange()
            .expectStatus()
            .isOk();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void should_refresh_access_token_when_access_token_has_expired() {
        // Before login
        var userUuid = "d43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test";
        assertThat(userRepository.existsByUuid(userUuid)).isTrue();
        var user = userRepository.findByUsername("test-user-it");
        assertThat(user).isPresent();

        var revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        var notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).hasSize(1);
        assertThat(notRevoked).hasSize(1);

        var loginTime = Instant.parse("2026-08-18T10:00:00Z");
        when(clock.instant()).thenReturn(loginTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username("test-user-it")
            .password("password")
            .build();

        // login user first to get a valid access token
        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class)
            .getResponseBody();

        revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).isEmpty();
        assertThat(notRevoked).hasSize(1);

        assertThat(loginResponse).isNotNull();
        var expiredAccessToken = loginResponse.accessToken();
        var refreshToken = loginResponse.refreshToken();

        // Login → token expires at 10:15
        var refreshTime = Instant.parse("2026-08-18T10:16:00Z");
        when(clock.instant()).thenReturn(refreshTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // Refresh using the valid refresh token
        var refreshRequest = ConnectorRefreshTokenRequest
            .builder()
            .refreshToken(refreshToken)
            .build();

        var refreshResponse = apiClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccessToken)
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
        assertThat(refreshResponse.refreshToken()).isEqualTo(refreshToken);

        // New access token must work
        apiClient.get()
            .uri("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
            .exchange()
            .expectStatus()
            .isOk();
    }
}
