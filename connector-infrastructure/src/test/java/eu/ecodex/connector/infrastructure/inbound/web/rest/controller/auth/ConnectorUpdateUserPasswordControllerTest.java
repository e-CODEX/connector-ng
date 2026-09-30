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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorUserInvalidPasswordException;
import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.domain.model.auth.ConnectorUpdateUserPasswordData;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorUpdateUserPasswordRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.identity.ConnectorUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
@Import(ConnectorUpdateUserPasswordControllerTest.WebSecurityTestConfig.class)
class ConnectorUpdateUserPasswordControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/auth/change-password";
    @MockitoBean
    private ConnectorUserAuthenticationProvider userAuthenticationProvider;
    @MockitoBean
    private ConnectorUpdateUserPassword updateUserPassword;
    @MockitoBean
    private ConnectorRefreshUserRefreshToken refreshUserRefreshToken;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void update_password_should_return_OK() throws Exception {
        // Given
        var accessToken = "access-token";
        var userPasswordRequest =
            ConnectorUserTestFixtures.createDefaultUpdateUserPasswordRequest();
        var connectorUser = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var userPrincipal = new ConnectorUserDetails(connectorUser, accessToken);

        doNothing().when(updateUserPassword).execute(any());

        // When
        mockMvc.perform(post(URL)
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andReturn();

        // Then
        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordData.class);
        verify(updateUserPassword).execute(captor.capture());

        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(
            ConnectorUpdateUserPasswordRequest.toDomain(connectorUser.uuid(), accessToken,
                userPasswordRequest)
        );

        assertNoMoreInteractions();
    }

    @Test
    void update_password_should_return_400() throws Exception {
        // Given
        var accessToken = "access-token";
        var userPasswordRequest =
            ConnectorUserTestFixtures.createDefaultUpdateUserPasswordRequest();
        var connectorUser = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var userPrincipal = new ConnectorUserDetails(connectorUser, accessToken);

        doThrow(ConnectorUserInvalidPasswordException.class).when(updateUserPassword).execute(
            any());

        // When
        mockMvc.perform(post(URL)
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

        // Then
        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordData.class);
        verify(updateUserPassword).execute(captor.capture());

        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(
            ConnectorUpdateUserPasswordRequest.toDomain(connectorUser.uuid(), accessToken,
                userPasswordRequest)
        );

        assertNoMoreInteractions();
    }

    @Test
    void update_password_should_return_400_when_new_password_is_too_short() throws Exception {
        // Given
        var connectorUserRequest =
            ConnectorUserTestFixtures.createDefaultUpdateUserShortPasswordRequest();
        // When
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectorUserRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

        // Then
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(userAuthenticationProvider, updateUserPassword,
            refreshUserRefreshToken);
    }

    @TestConfiguration
    @EnableWebSecurity
    static class WebSecurityTestConfig {
    }
}