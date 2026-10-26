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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.infrastructure.adapter.out.auth.accesstoken.ConnectorJwtAuthenticationFilter;
import eu.ecodex.connector.infrastructure.adapter.out.auth.accesstoken.ConnectorJwtParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;

@WebMvcTest
@ContextConfiguration(classes = {
    WebSecurityConfiguration.class,
    ConnectorJwtAuthenticationFilter.class,
    WebSecurityConfigurationRulesTest.ProbeController.class,
})
class WebSecurityConfigurationRulesTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    FilterChainProxy filterChainProxy;

    @MockitoBean
    ConnectorJwtParser jwtParser;
    @MockitoBean
    UserDetailsService userDetailsService;
    @MockitoBean
    CorsConfigurationSource corsConfigurationSource;

    @ParameterizedTest
    @CsvSource({
        "GET,  /api/v1/admin/probe,              USER,  200",
        "GET,  /api/v1/admin/users/probe,        USER,  403",
        "GET,  /api/v1/admin/users/probe,        ADMIN, 200",
        "GET,  /api/v1/admin/business-domains,   USER,  200",
        "POST, /api/v1/admin/business-domains,   USER,  403",
        "POST, /api/v1/admin/business-domains,   ADMIN, 200"
    })
    void user_role_access_matrix(String method, String path, String role, int expected) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(method), path).with(user("test").roles(role)))
            .andExpect(status().is(expected));
    }

    @Test
    void security_chain_is_active() {
        assertThat(filterChainProxy.getFilterChains()).isNotEmpty();
        assertThat(filterChainProxy.getFilterChains().getFirst().getFilters())
            .anyMatch(f -> f instanceof ConnectorJwtAuthenticationFilter);
    }

    @Test
    void anonymous_caller_gets_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/probe").with(anonymous()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_is_public() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login"))
            .andExpect(status().isOk());
    }

    @Test
    void refresh_is_public() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
            .andExpect(status().isOk());
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