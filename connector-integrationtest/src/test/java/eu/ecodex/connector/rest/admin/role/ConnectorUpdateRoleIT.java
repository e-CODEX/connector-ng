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
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

class ConnectorUpdateRoleIT extends AbstractIntegrationTest {
    public static final String PATH = "/api/v1/admin/users/roles";
    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectorUserRepository userRepository;

    @Autowired
    private ConnectorRoleRepository roleRepository;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_should_succeeded_when_valid_credentials_are_provided() {
        var roleName = "ROLE_TEST";
        var existingRole = roleRepository.findByName(roleName);
        assertThat(existingRole).isNotEmpty();

        var username = "test-user-it";
        var user = userRepository.findByUsername(username);
        assertThat(user).isNotEmpty();
        assertThat(user.get().roles()).hasSize(2);
        assertThat(user.get().roles().stream().map(ConnectorRole::name).toList())
            .contains("ROLE_TEST", "ROLE_USER");

        var newRole = "ROLE_TEST_2";
        var request = ConnectorRoleDto
            .builder()
            .name(newRole)
            .build();

        var registered = apiClient
            .put()
            .uri(StringUtils.joinWith("/", PATH, existingRole.get().uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorRoleDto.class)
            .getResponseBody();

        assertThat(registered).isNotNull();
        existingRole = roleRepository.findByName(roleName);
        assertThat(existingRole).isEmpty();
        var newRoleEntity = roleRepository.findByName(newRole);
        assertThat(newRoleEntity).isNotEmpty();
        assertThat(newRoleEntity.get().name()).isEqualTo(newRole);

        user = userRepository.findByUsername(username);
        assertThat(user).isNotEmpty();
        assertThat(user.get().roles()).hasSize(2);
        assertThat(user.get().roles().stream().map(ConnectorRole::name).toList())
            .contains("ROLE_TEST_2", "ROLE_USER");
    }


    @Test
    @Sql({"classpath:sql/user.sql"})
    void update_should_failed_when_username_already_exists() {
        var roleName = "ROLE_USER";
        var existing = roleRepository.findByName(roleName);
        assertThat(existing).isNotEmpty();

        var newRole = "ROLE_TEST";
        var toUpdate = roleRepository.findByName(newRole);
        assertThat(toUpdate).isNotEmpty();

        var request = ConnectorRoleDto
            .builder()
            .name(newRole)
            .build();

        apiClient
            .put()
            .uri(StringUtils.joinWith("/", PATH, existing
                .get()
                .uuid()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .is4xxClientError();
    }

}
