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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorDisabledUserIT")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorDisabledUserIT extends AbstractIntegrationTest {
    private static final String USER_UUID = "d43bfa931-3c25-47e4-b377-bf4ce7b0d04c_fake_user_test";

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
    void should_refuse_the_tokens_of_a_disabled_user() {
        when(clock.instant()).thenReturn(Instant.parse("2026-08-18T10:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        var login = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorLoginRequest.builder()
                                       .username("test-user-it").password("password").build())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class)
            .getResponseBody();
        assertThat(login).isNotNull();

        // the tokens work while the user is enabled
        apiClient.get()
            .uri("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + login.accessToken())
            .exchange()
            .expectStatus()
            .isOk();

        // an administrator disables the user
        apiClient.patch()
            .uri("/api/v1/admin/users/{uuid}", USER_UUID)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"enabled\": false}")
            .exchange()
            .expectStatus()
            .isOk();

        // the access token issued before is refused
        apiClient.get()
            .uri("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + login.accessToken())
            .exchange()
            .expectStatus()
            .isUnauthorized();

        // and so is the refresh token
        apiClient.post()
            .uri("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorRefreshTokenRequest.builder().refreshToken(login.refreshToken()).build())
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
