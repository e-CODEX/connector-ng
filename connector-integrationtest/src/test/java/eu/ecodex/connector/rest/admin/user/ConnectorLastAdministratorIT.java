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
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.user.ConnectorEditUserRequest;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.user.ConnectorEditUserRolesRequest;
import eu.ecodex.connector.infrastructure.adapter.out.auth.identity.ConnectorUserDetails;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * The connector keeps at least one enabled administrator. user.sql holds two: "admin" and
 * "test-admin-it".
 */
@DisplayName("ConnectorLastAdministratorIT REST")
class ConnectorLastAdministratorIT extends AbstractIntegrationTest {
    private static final String PATH = "/api/v1/admin/users/";
    private static final String ADMIN = "admin";
    private static final String OTHER_ADMIN = "test-admin-it";
    private static final String ENABLED_ADMINS = """
        SELECT COUNT(*)
        FROM connector_users u
        JOIN connector_users_roles ur ON ur.user_id = u.id
        JOIN connector_roles r ON r.id = ur.role_id
        WHERE u.enabled = true AND r.name = 'ROLE_ADMIN'
        """;

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
    void should_refuse_to_delete_the_last_administrator() {
        removeAdministrator(OTHER_ADMIN, generateDefaultAdminToken()).expectStatus().isNoContent();

        removeAdministrator(ADMIN, generateDefaultAdminToken()).expectStatus().isEqualTo(409);

        assertThat(userRepository.findByUsername(ADMIN)).isNotEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_refuse_to_disable_the_last_administrator() {
        removeAdministrator(OTHER_ADMIN, generateDefaultAdminToken()).expectStatus().isNoContent();

        apiClient.patch()
            .uri(PATH + uuidOf(ADMIN))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorEditUserRequest.builder().enabled(false).build())
            .exchange()
            .expectStatus().isEqualTo(409);

        assertThat(userRepository.findByUsername(ADMIN).orElseThrow().enabled()).isTrue();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_refuse_to_remove_the_admin_role_of_the_last_administrator() {
        removeAdministrator(OTHER_ADMIN, generateDefaultAdminToken()).expectStatus().isNoContent();

        apiClient.patch()
            .uri(PATH + uuidOf(ADMIN) + "/roles")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ConnectorEditUserRolesRequest.builder().roles(Set.of("ROLE_USER")).build())
            .exchange()
            .expectStatus().isEqualTo(409);

        assertThat(userRepository.findByUsername(ADMIN).orElseThrow().roleNames())
            .contains("ROLE_ADMIN");
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_let_an_administrator_be_removed_while_another_one_remains() {
        removeAdministrator(ADMIN, tokenOf(OTHER_ADMIN)).expectStatus().isNoContent();

        assertThat(userRepository.findByUsername(ADMIN)).isEmpty();
        assertThat(jdbcTemplate.queryForObject(ENABLED_ADMINS, Integer.class)).isOne();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_keep_an_administrator_when_the_two_administrators_remove_each_other()
        throws Exception {
        var adminToken = generateDefaultAdminToken();
        var otherToken = tokenOf(OTHER_ADMIN);
        var start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);

        try {
            var first = executor.submit(() -> {
                start.await();
                return removeAdministrator(OTHER_ADMIN, adminToken).returnResult(String.class)
                    .getStatus().value();
            });
            var second = executor.submit(() -> {
                start.await();
                return removeAdministrator(ADMIN, otherToken).returnResult(String.class)
                    .getStatus().value();
            });
            start.countDown();

            var statuses =
                List.of(first.get(60, TimeUnit.SECONDS), second.get(60, TimeUnit.SECONDS));

            // one removal went through, the other one was refused (409), or its caller was
            // removed before being authenticated (401)
            assertThat(statuses).filteredOn(status -> status == 204).hasSize(1);
        } finally {
            executor.shutdownNow();
        }

        assertThat(jdbcTemplate.queryForObject(ENABLED_ADMINS, Integer.class)).isOne();
    }

    private RestTestClient.ResponseSpec removeAdministrator(String username, String token) {
        return apiClient.delete()
            .uri(PATH + uuidOf(username))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .exchange();
    }

    private String uuidOf(String username) {
        return userRepository.findByUsername(username).orElseThrow().uuid();
    }

    private String tokenOf(String username) {
        return jwtTokenService.generateAccessToken(
            new ConnectorUserDetails(userRepository.findByUsername(username).orElseThrow()));
    }
}
