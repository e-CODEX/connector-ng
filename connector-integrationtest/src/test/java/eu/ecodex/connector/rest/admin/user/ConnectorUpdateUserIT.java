/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorUpdateUserIT extends AbstractIntegrationTest {
    public static final String PATH = "/api/v1/admin/users";
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
    void update_should_succeeded_when_valid_credentials_are_provided() {
        var username = "test-user-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();
        assertThat(before.get().enabled()).isFalse();

        var request = ConnectorUserRequest
            .builder()
            .username(username)
            .password("password")
            .email("test@email.com")
            .enabled(true)
            .build();

        var registeredUser = apiClient
            .put()
            .uri(StringUtils.joinWith("/", PATH, before.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class)
            .getResponseBody();

        assertThat(registeredUser).isNotNull();

        var after = userRepository.findByUsername(username);
        assertThat(before).isNotEqualTo(after);
        assertThat(registeredUser.enabled()).isTrue();
        assertThat(registeredUser.username()).isEqualTo(username);
        assertThat(registeredUser.email()).isEqualTo("test@email.com");
        assertThat(registeredUser.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_TEST");
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_should_failed_when_invalid_token_is_provided() {
        var username = "test-user-it";
        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();

        var request = ConnectorUserRequest
            .builder()
            .username(username)
            .password("password")
            .email("test@email.com")
            .enabled(true)
            .build();

        apiClient
            .put()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer ")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isUnauthorized();

        var afterUpdate = userRepository.findByUsername(username);
        assertThat(existing).isEqualTo(afterUpdate);
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_should_failed_when_username_already_exists() {
        var username = "test-user2-it";
        var before = userRepository.findByUsername(username);
        assertThat(before).isNotEmpty();

        var request = ConnectorUserRequest
            .builder()
            .username("test-user-it")
            .password("password")
            .email("test@email.com")
            .enabled(true)
            .build();

        apiClient
            .put()
            .uri(StringUtils.joinWith("/", PATH, before.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .is4xxClientError();

        var after = userRepository.findByUsername(username);
        assertThat(before).isEqualTo(after);
    }

}
