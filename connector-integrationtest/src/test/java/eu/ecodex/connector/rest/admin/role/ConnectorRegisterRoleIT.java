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

import static eu.ecodex.connector.domain.model.user.ConnectorRole.DEFAULT_ADMIN_ROLE;
import static eu.ecodex.connector.domain.model.user.ConnectorUser.DEFAULT_ADMIN_USER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;


class ConnectorRegisterRoleIT extends AbstractIntegrationTest {
    public static final String PATH = "/api/v1/admin/users/roles";
    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectorRoleRepository roleRepository;

    @Autowired
    private ConnectorUserRepository userRepository;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void register_should_succeeded_when_valid_token_is_provided() {
        var newRole = "new_role";
        var existing = roleRepository.findByName(newRole);
        var adminRole = roleRepository.findByName(DEFAULT_ADMIN_ROLE);
        var admin = userRepository.findByUsername(DEFAULT_ADMIN_USER_NAME);
        assertThat(admin).isNotEmpty();
        assertThat(adminRole).isNotEmpty();
        assertThat(existing).isEmpty();

        var request = ConnectorRoleDto
                .builder()
                .name(newRole)
                .build();

        var registeredRole = apiClient
                .post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ConnectorRoleDto.class)
                .getResponseBody();

        assertThat(registeredRole).isNotNull();

        existing = roleRepository.findByName(newRole);
        assertThat(existing).isNotEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void register_should_failed_when_role_already_exists() {
        var roleName = "ROLE_ADMIN_IT";
        var existing = roleRepository.findByName(roleName);
        assertThat(existing).isNotEmpty();

        var request = ConnectorRoleDto
                .builder()
                .name(roleName)
                .build();

        apiClient
                .post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus()
                .is4xxClientError();

        existing = roleRepository.findByName(roleName);
        assertThat(existing).isNotEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void register_should_failed_when_invalid_token_is_provided() {
        var newRole = "new_role";
        var existing = roleRepository.findByName(newRole);
        assertThat(existing).isEmpty();

        var request = ConnectorRoleDto
                .builder()
                .name(newRole)
                .build();

        apiClient
                .post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        existing = roleRepository.findByName(newRole);
        assertThat(existing).isEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void register_should_failed_when_missing_a_mandatory_field() {
        var countAll = roleRepository.findAll().size();
        var request = ConnectorRoleDto
                .builder()
                .name(StringUtils.EMPTY)
                .build();

        apiClient
                .post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus()
                .isBadRequest();

        assertThat(countAll).isEqualTo(roleRepository.findAll().size());
    }

}
