/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.infrastructure.adapter.out.auth.accesstoken.ConnectorJwtAuthenticationFilter;
import eu.ecodex.connector.infrastructure.adapter.out.auth.accesstoken.ConnectorJwtParser;
import eu.ecodex.connector.infrastructure.config.property.ConnectorBasicAuthProperties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;

@WebMvcTest
@ContextConfiguration(classes = {
    WebSecurityConfiguration.class,
    ConnectorJwtAuthenticationFilter.class,
    WebSecurityConfigurationRulesTest.ProbeController.class,
    WebSecurityConfigurationRulesTest.BasicAuthTestConfiguration.class,
})
class WebSecurityConfigurationRulesTest {
    private static final String BASIC_AUTH_USER = "swagger";
    private static final String BASIC_AUTH_PASSWORD = "swagger-secret";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    FilterChainProxy filterChainProxy;

    @MockitoBean
    ConnectorJwtParser jwtParser;
    @MockitoBean("inMemoryUserDetailsManager")
    UserDetailsService userDetailsService;
    @MockitoBean
    CorsConfigurationSource corsConfigurationSource;

    @Nested
    class ApiChain {
        @ParameterizedTest
        @CsvSource({
            "GET,  /api/v1/admin/probe,              USER,  200",
            "GET,  /api/v1/admin/users/probe,        USER,  403",
            "GET,  /api/v1/admin/users/probe,        ADMIN, 200",
            "GET,  /api/v1/admin/business-domains,   USER,  200",
            "POST, /api/v1/admin/business-domains,   USER,  403",
            "POST, /api/v1/admin/business-domains,   ADMIN, 200",
            "GET,  /api/v1/admin/jms/queues/stats,   USER,  200",
            "POST, /api/v1/admin/jms/queues/dlq/replay,                  USER,  403",
            "POST, /api/v1/admin/jms/queues/dlq/replay,                  ADMIN, 200",
            "POST, /api/v1/admin/jms/queues/dlq/messages/ID-1/replay,    USER,  403",
            "POST, /api/v1/admin/jms/queues/dlq/messages/ID-1/replay,    ADMIN, 200",
            "DELETE, /api/v1/admin/jms/queues/dlq/messages/ID-1,         USER,  403",
            "DELETE, /api/v1/admin/jms/queues/dlq/messages/ID-1,         ADMIN, 200",
            "GET,  /api/v1/admin/evidences/uuid-1/download,              USER,  403",
            "GET,  /api/v1/admin/evidences/uuid-1/download,              ADMIN, 200"
        })
        void should_apply_role_access_matrix(
            String method, String path, String role, int expected) throws Exception {
            mockMvc.perform(request(HttpMethod.valueOf(method), path).with(user("test").roles(role)))
                   .andExpect(status().is(expected));
        }

        @Test
        void should_register_jwt_filter_in_api_chain() {
            assertThat(filterChainProxy.getFilters("/api/v1/probe"))
                .anyMatch(ConnectorJwtAuthenticationFilter.class::isInstance);
        }

        @Test
        void should_return_unauthorized_when_caller_is_anonymous() throws Exception {
            mockMvc.perform(get("/api/v1/probe").with(anonymous()))
                   .andExpect(status().isUnauthorized());
        }

        @Test
        void should_allow_anonymous_login() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login"))
                   .andExpect(status().isOk());
        }

        @Test
        void should_allow_anonymous_refresh() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh"))
                   .andExpect(status().isOk());
        }
    }

    @Nested
    class SwaggerChain {
        @Test
        void should_not_register_jwt_filter_in_swagger_chain() {
            assertThat(filterChainProxy.getFilters("/swagger-ui/index.html"))
                .noneMatch(ConnectorJwtAuthenticationFilter.class::isInstance);
        }

        @Test
        void should_return_unauthorized_when_no_basic_credentials() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                   .andExpect(status().isUnauthorized());
        }

        @Test
        void should_return_unauthorized_when_password_is_wrong() throws Exception {
            mockMvc.perform(get("/v3/api-docs").with(httpBasic(BASIC_AUTH_USER, "wrong")))
                   .andExpect(status().isUnauthorized());
        }

        @Test
        void should_let_request_through_when_basic_credentials_are_valid() throws Exception {
            mockMvc.perform(get("/v3/api-docs").with(httpBasic(BASIC_AUTH_USER, BASIC_AUTH_PASSWORD)))
                   .andExpect(result -> assertThat(result.getResponse().getStatus())
                       .isNotIn(401, 403));
        }
    }

    @TestConfiguration
    static class BasicAuthTestConfiguration {
        @Bean
        ConnectorBasicAuthProperties connectorBasicAuthProperties() {
            return new ConnectorBasicAuthProperties(BASIC_AUTH_USER, BASIC_AUTH_PASSWORD);
        }
    }

    @RestController
    static class ProbeController {
        @GetMapping("/api/v1/probe")
        String authenticated() {
            return "ok";
        }

        @GetMapping("/api/v1/admin/probe")
        String admin() {
            return "ok";
        }

        @GetMapping("/api/v1/admin/users/probe")
        String adminUsers() {
            return "ok";
        }

        @GetMapping("/api/v1/admin/business-domains")
        String getBusinessDomains() {
            return "ok";
        }

        @PostMapping("/api/v1/admin/business-domains")
        String postBusinessDomains() {
            return "ok";
        }

        @GetMapping("/api/v1/admin/jms/queues/stats")
        String queuesStats() {
            return "ok";
        }

        @PostMapping({
            "/api/v1/admin/jms/queues/dlq/replay",
            "/api/v1/admin/jms/queues/dlq/messages/{id}/replay"
        })
        String replay() {
            return "ok";
        }

        @GetMapping("/api/v1/admin/evidences/{uuid}/download")
        String downloadEvidence() {
            return "ok";
        }

        @DeleteMapping("/api/v1/admin/jms/queues/dlq/messages/{id}")
        String deleteDlqMessage() {
            return "ok";
        }

        @PostMapping("/api/v1/auth/login")
        String login() {
            return "ok";
        }

        @PostMapping("/api/v1/auth/refresh")
        String refresh() {
            return "ok";
        }
    }
}
