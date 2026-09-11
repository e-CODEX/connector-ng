/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.role;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorRetrieveRoleIT extends AbstractIntegrationTest {
    public static final String PATH = "/api/v1/admin/users/roles";
    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectorRoleRepository roleRepository;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void getAll_should_succeeded_when_valid_token_is_provided() {
        var existing = roleRepository.findAll();
        assertThat(existing).isNotEmpty();

        var response = apiClient
            .get()
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
        var name = "ROLE_TEST";
        var existing = roleRepository.findByName(name);
        assertThat(existing).isNotEmpty();

        var response = apiClient
            .get()
            .uri(StringUtils.joinWith("/", PATH, existing.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorRoleDto.class)
            .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.identifier()).isEqualTo(existing.get().uuid());
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void get_should_failed_when_invalid_token_is_provided() {
        var name = "ROLE_TEST";
        var existing = roleRepository.findByName(name);
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
    void get_should_return_404_when_not_found_role() {
        var identifier = UUID
            .randomUUID()
            .toString();
        var existing = roleRepository.findByUuid(identifier);
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
