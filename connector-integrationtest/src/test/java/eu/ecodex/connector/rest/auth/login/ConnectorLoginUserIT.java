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

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorLoginUserIT extends AbstractIntegrationTest {
    private static final String PATH = "/api/v1/auth/login";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void login_should_succeeded_for_default_admin_user_when_valid_credentials_are_provided() {
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(ConnectorUser.DEFAULT_ADMIN_USER_NAME)
            .password(ConnectorUser.DEFAULT_ADMIN_PASSWORD)
            .build();

        var loginResponse = apiClient.post()
            .uri(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        assertThat(loginResponse.getResponseBody().accessToken()).isNotBlank();
        assertThat(loginResponse.getResponseBody().refreshToken()).isNotBlank();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void login_should_succeeded_for_user_when_user_is_active_valid_credentials_are_provided() {
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username("test-user-it")
            .password("password")
            .build();

        var loginResponse = apiClient.post()
            .uri(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        assertThat(loginResponse.getResponseBody().accessToken()).isNotBlank();
        assertThat(loginResponse.getResponseBody().refreshToken()).isNotBlank();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void login_should_failed_for_user_when_user_is_not_active_valid_credentials_are_provided() {
        // login first to get the access token
        var username = "test-user-it";
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password("password")
            .build();

        var loginResponse = apiClient.post()
            .uri(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorLoginResponse.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        assertThat(accessToken).isNotBlank();

        // update user to disable it
        var request = ConnectorUserRequest
            .builder()
            .username(username)
            .enabled(false)
            .build();

        var updatedUser = apiClient
            .patch()
            .uri("/api/v1/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class)
            .getResponseBody();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.username()).isEqualTo(username);
        assertThat(updatedUser.enabled()).isFalse();

        // login again with the disabled user
        apiClient.post()
            .uri(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }


    @Test
    @Sql("classpath:sql/user.sql")
    void login_should_failed_for_default_admin_user_when_invalid_credentials_are_provided() {
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(ConnectorUser.DEFAULT_ADMIN_USER_NAME)
            .password("wrong-password")
            .build();

        apiClient.post()
            .uri(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
