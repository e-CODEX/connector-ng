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
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.login.ConnectorRefreshTokenRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRefreshTokenReuseIT")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorRefreshTokenReuseIT extends AbstractIntegrationTest {
    private static final Instant LOGIN_TIME = Instant.parse("2026-08-18T10:00:00Z");

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
    void should_end_every_session_when_a_replaced_refresh_token_is_presented_again() {
        at(LOGIN_TIME);
        var login = login();

        at(LOGIN_TIME.plusSeconds(60));
        var refreshed = refresh(login.refreshToken(), 200);
        assertThat(refreshed.refreshToken()).isNotEqualTo(login.refreshToken());

        // the first refresh token was replaced: presenting it again, well after the grace period,
        // means that a copy of it exists
        at(LOGIN_TIME.plusSeconds(120));
        refresh(login.refreshToken(), 401);

        // so the current refresh token of the user was revoked as well
        refresh(refreshed.refreshToken(), 401);
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void should_only_refuse_a_replaced_refresh_token_presented_within_the_grace_period() {
        at(LOGIN_TIME);
        var login = login();

        at(LOGIN_TIME.plusSeconds(60));
        var refreshed = refresh(login.refreshToken(), 200);

        // a client retrying right after the replacement is refused, but not logged out
        at(LOGIN_TIME.plusSeconds(62));
        refresh(login.refreshToken(), 401);
        refresh(refreshed.refreshToken(), 200);
    }

    private void at(Instant instant) {
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    private ConnectorUserAuthenticationResult login() {
        var result = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorLoginRequest.builder()
                                       .username("test-user-it").password("password").build())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class)
            .getResponseBody();
        assertThat(result).isNotNull();
        return result;
    }

    private ConnectorUserAuthenticationResult refresh(String refreshToken, int expectedStatus) {
        var spec = apiClient.post()
            .uri("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorRefreshTokenRequest.builder().refreshToken(refreshToken).build())
            .exchange()
            .expectStatus()
            .isEqualTo(expectedStatus);

        // only a successful refresh returns tokens
        return expectedStatus == 200
            ? spec.returnResult(ConnectorUserAuthenticationResult.class).getResponseBody()
            : null;
    }
}
