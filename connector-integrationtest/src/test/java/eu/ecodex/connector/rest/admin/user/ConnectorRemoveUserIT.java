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
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorRemoveUserIT extends AbstractIntegrationTest {
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
    void remove_should_succeeded_when_valid_token_is_provided() {
        var username = "test-user-it";
        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();

        apiClient.delete()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        existing = userRepository.findByUsername(username);
        assertThat(existing).isEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void remove_should_return_404_when_not_found_user() {
        var identifier = UUID.randomUUID().toString();
        var existing = userRepository.findByUuid(identifier);
        assertThat(existing).isEmpty();

        apiClient.delete()
            .uri(StringUtils.joinWith("/", PATH, identifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void remove_should_failed_when_invalid_token_is_provided() {
        var username = "test-user-it";
        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();

        apiClient
            .delete()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer ")
            .exchange()
            .expectStatus()
            .isUnauthorized();

        existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();
    }

}
