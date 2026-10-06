/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.auth.user;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorUpdateUserPasswordRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorUpdateUserPasswordIT extends AbstractIntegrationTest {
    public static final String PATH = "/api/v1/auth/change-password";

    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectorUserRepository userRepository;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_password_should_not_update_when_must_change_password_is_False() {
        var username = "test-user-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().mustChangePassword()).isFalse();

        var password = "password";
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password(password)
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        var refreshToken = loginResponse.getResponseBody().refreshToken();
        assertThat(accessToken).isNotBlank();

        var newPassword = "Passw0rd123!";
        var request = ConnectorUpdateUserPasswordRequest.builder()
            .refreshToken(refreshToken)
            .currentPassword(password)
            .newPassword(newPassword)
            .build();

        apiClient.post().uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .is4xxClientError();

        var after = userRepository.findByUsername(username);
        assertThat(before).isEqualTo(after);
        assertThat(after).isNotEmpty();
        assertThat(after.get().mustChangePassword()).isFalse();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_password_should_not_update_when_passwords_does_not_match_existing_one() {
        var username = "test-user3-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().mustChangePassword()).isTrue();

        var password = "Passw0rd123!";
        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password(password)
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        var refreshToken = loginResponse.getResponseBody().refreshToken();
        assertThat(accessToken).isNotBlank();

        var newPassword = "Passw0rd123!";
        var currentPassword = "MyNewPassword";
        var request = ConnectorUpdateUserPasswordRequest.builder()
            .refreshToken(refreshToken)
            .currentPassword(currentPassword)
            .newPassword(newPassword)
            .build();

        apiClient.post()
            .uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest();

        var after = userRepository.findByUsername(username);
        assertThat(before).isEqualTo(after);
        assertThat(after).isNotEmpty();
        assertThat(after.get().mustChangePassword()).isTrue();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_password_should_not_update_when_passwords_does_not_change() {
        var username = "test-user3-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().mustChangePassword()).isTrue();

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password("Passw0rd123!")
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        var refreshToken = loginResponse.getResponseBody().refreshToken();
        assertThat(accessToken).isNotBlank();

        var request = ConnectorUpdateUserPasswordRequest.builder()
            .refreshToken(refreshToken)
            .currentPassword("Passw0rd123!")
            .newPassword("Passw0rd123!")
            .build();

        apiClient.post()
            .uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest();

        var after = userRepository.findByUsername(username);
        assertThat(before).isEqualTo(after);
        assertThat(after).isNotEmpty();
        assertThat(after.get().mustChangePassword()).isTrue();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_password_should_not_update_when_new_password_is_too_short() {
        var username = "test-user3-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().enabled()).isTrue();

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password("Passw0rd123!")
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        var refreshToken = loginResponse.getResponseBody().refreshToken();
        assertThat(accessToken).isNotBlank();

        var request = ConnectorUpdateUserPasswordRequest.builder()
            .refreshToken(refreshToken)
            .currentPassword("Passw0rd123!")
            .newPassword("test!")
            .build();

        apiClient.post()
            .uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest();

        var after = userRepository.findByUsername(username);
        assertThat(before).isEqualTo(after);
        assertThat(after).isNotEmpty();
        assertThat(after.get().mustChangePassword()).isTrue();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_password_should_succeeded_when_valid_credentials_are_provided() {
        var username = "test-user3-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().enabled()).isTrue();
        assertThat(before.get().mustChangePassword()).isTrue();

        var loginRequest = ConnectorLoginRequest
            .builder()
            .username(username)
            .password("Passw0rd123!")
            .build();

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();
        var refreshToken = loginResponse.getResponseBody().refreshToken();
        assertThat(accessToken).isNotBlank();

        var request = ConnectorUpdateUserPasswordRequest.builder()
            .refreshToken(refreshToken)
            .currentPassword("Passw0rd123!")
            .newPassword("new-password")
            .build();

        apiClient.post().uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isNoContent();

        var after = userRepository.findByUsername(username);
        assertThat(before).isNotEqualTo(after);
        assertThat(after).isNotEmpty();
        assertThat(after.get().mustChangePassword()).isFalse();
    }
}
