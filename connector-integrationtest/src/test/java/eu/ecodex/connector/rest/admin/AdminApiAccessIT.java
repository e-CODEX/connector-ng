/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Checks, for every endpoint of the administration API found in the application, that a user
 * without the administrator role is refused. The endpoints are discovered, so a new endpoint which
 * is not restricted makes this test fail.
 */
@DisplayName("AdminApiAccessIT REST")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class AdminApiAccessIT extends AbstractIntegrationTest {
    private static final String ADMIN_API = "/api/v1/admin/";
    /**
     * The endpoints which are also open to the users without the administrator role.
     */
    private static final Set<String> OPEN_TO_ALL_USERS = Set.of(
        "GET /api/v1/admin/messages/stats",
        "GET /api/v1/admin/messages/reports",
        "GET /api/v1/admin/business-domains"
    );

    @Autowired
    private RestTestClient apiClient;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_refuse_a_user_without_the_admin_role_on_every_admin_endpoint_but_the_open_ones() {
        var endpoints = adminEndpoints();
        assertThat(endpoints).hasSizeGreaterThan(20);
        var token = "Bearer " + generateDefaultSimpleUserToken();

        var wrong = new ArrayList<String>();
        for (var endpoint : endpoints) {
            var status = statusOf(endpoint, token);
            var open = OPEN_TO_ALL_USERS.contains(endpoint);
            if (open == (status == 403 || status == 401)) {
                wrong.add("%s -> %d".formatted(endpoint, status));
            }
        }

        assertThat(wrong).isEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_let_an_administrator_reach_every_admin_endpoint() {
        var token = "Bearer " + generateDefaultAdminToken();

        var refused = new ArrayList<String>();
        for (var endpoint : adminEndpoints()) {
            var status = statusOf(endpoint, token);
            if (status == 403 || status == 401) {
                refused.add("%s -> %d".formatted(endpoint, status));
            }
        }

        assertThat(refused).isEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_let_a_user_without_the_admin_role_read_the_statistics_and_reports() {
        var token = "Bearer " + generateDefaultSimpleUserToken();
        var period = "?from=2026-11-22T23:00:00.000Z&to=2026-11-25T14:33:40.941Z";

        for (var path : List.of("/api/v1/admin/messages/reports", "/api/v1/admin/messages/stats")) {
            apiClient.get()
                .uri(path + period)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus()
                .isOk();
        }
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_let_a_user_without_the_admin_role_list_but_not_create_business_domains() {
        var token = "Bearer " + generateDefaultSimpleUserToken();

        apiClient.get()
            .uri("/api/v1/admin/business-domains")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isOk();

        apiClient.post()
            .uri("/api/v1/admin/business-domains")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    /**
     * Gives the administration endpoints as {@code METHOD /path}, with a dummy value for the path
     * variables: the authorization is checked before the controller is reached.
     */
    private List<String> adminEndpoints() {
        var endpoints = new ArrayList<String>();

        handlerMapping.getHandlerMethods().keySet().forEach(info -> {
            var patterns = info.getPathPatternsCondition();
            if (patterns == null) {
                return;
            }
            var methods = info.getMethodsCondition().getMethods();
            for (var pattern : patterns.getPatternValues()) {
                if (!pattern.startsWith(ADMIN_API)) {
                    continue;
                }

                var path = pattern.replaceAll("\\{[^/}]+}", "x");
                var requestMethods = methods.isEmpty() ? Set.of(RequestMethod.GET) : methods;
                requestMethods.forEach(method -> endpoints.add(method.name() + " " + path));
            }
        });

        return endpoints.stream().distinct().sorted().toList();
    }

    private int statusOf(String endpoint, String token) {
        var parts = endpoint.split(" ", 2);

        return apiClient
            .method(HttpMethod.valueOf(parts[0]))
            .uri(parts[1])
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .returnResult(String.class)
            .getStatus()
            .value();
    }
}
