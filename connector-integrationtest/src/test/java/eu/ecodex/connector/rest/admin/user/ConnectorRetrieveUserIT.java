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
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorRetrieveUserIT extends AbstractIntegrationTest {
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
    void getAll_should_succeeded_when_valid_token_is_provided() {
        var existing = userRepository.findAllWithRoles();
        assertThat(existing).isNotEmpty();

        var response = apiClient.get()
            .uri(PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(List.class)
            .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(existing.size());
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void get_should_succeeded_when_valid_token_is_provided() {
        var username = "test-user-it";
        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();

        var response = apiClient
            .get()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class)
            .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo(username);
        assertThat(response.enabled()).isEqualTo(existing.get().enabled());
        assertThat(response.roles().size()).isEqualTo(existing.get().roles().size());
        assertThat(response.uuid()).isEqualTo(existing.get().uuid());
        assertThat(response.email()).isEqualTo(existing.get().email());
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void get_should_failed_when_invalid_token_is_provided() {
        var username = "test-user-it";
        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();

        apiClient
            .get()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer ")
            .exchange()
            .expectStatus()
            .isUnauthorized();

    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void get_should_return_404_when_not_found_user() {
        var identifier = UUID.randomUUID().toString();
        var existing = userRepository.findByUuid(identifier);
        assertThat(existing).isEmpty();

        apiClient
            .get()
            .uri(StringUtils.joinWith("/", PATH, identifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}
