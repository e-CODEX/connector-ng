/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshUserTokenService;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorLoginUserService;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorLogoutUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ConnectorAuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ConnectorLogoutControllerTest.WebSecurityTestConfig.class)
class ConnectorLogoutControllerTest extends AbstractWebMvcTest {

    @MockitoBean
    ConnectorLogoutUserService logoutUserService;

    @MockitoBean
    ConnectorLoginUserService loginUserService;

    @MockitoBean
    ConnectorRefreshUserTokenService userTokenService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void logout_should_logout_the_user() throws Exception {
        // Given
        var userPrincipal = ConnectorUserTestFixtures.createUserDetails();
        var refreshToken = "refresh-token-abc";
        var request = ConnectorLogoutRequest.builder().refreshToken(refreshToken).build();

        doNothing().when(logoutUserService).logout(any(), any());

        // When
        mockMvc.perform(post("/api/v1/auth/logout")
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer access-token-xyz")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Then
        verify(logoutUserService).logout(userPrincipal.getUserId(), refreshToken);
        verifyNoMoreInteractions(loginUserService, userTokenService, logoutUserService);
    }

    @Test
    void logout_should_return_400_when_not_authenticated() throws Exception {
        var request = new ConnectorLogoutRequest("refresh-token-abc");

        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    @EnableWebSecurity
    static class WebSecurityTestConfig {
    }

}