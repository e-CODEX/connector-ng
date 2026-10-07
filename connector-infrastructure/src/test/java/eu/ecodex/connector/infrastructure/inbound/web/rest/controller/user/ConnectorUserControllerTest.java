/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorEditUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorUpdateUserPasswordRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorEditSelfRequest;
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

@WebMvcTest(ConnectorUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ConnectorUserControllerTest.WebSecurityTestConfig.class)
class ConnectorUserControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/auth/me";
    @MockitoBean
    private ConnectorEditUser editUser;
    @MockitoBean
    private ConnectorUpdateUserPassword updateUserPassword;
    @MockitoBean
    private ConnectorRetrieveUserByIdentifier retrieveUser;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void update_password_should_edit_user_password() throws Exception {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();
        var userPrincipal = ConnectorUserTestFixtures.createUserDetails();
        var userPasswordRequest =
            ConnectorUserTestFixtures.createDefaultUpdateUserPasswordRequest();
        var userPasswordCommand = ConnectorUpdateUserPasswordRequest.from(connectorUser.uuid(),
            userPrincipal.accessToken(), userPasswordRequest);

        doNothing().when(updateUserPassword).execute(any());

        // When
        mockMvc.perform(post(URL.concat("/change-password"))
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Then
        verify(updateUserPassword).execute(userPasswordCommand);

        assertNoMoreInteractions();
    }

    @Test
    void patch_should_edit_user_data() throws Exception {
        // Given
        var newEmail = "test_user_new@email.com";
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser()
            .toBuilder().email(newEmail).build();
        var userPrincipal = ConnectorUserTestFixtures.createUserDetails();
        var editSelfRequest = ConnectorUserTestFixtures.createDefaultPatchMeUserRequest();
        var expected =
            ConnectorUserTestFixtures.createUserDto().toBuilder().email(newEmail).build();

        when(editUser.execute(any(), any())).thenReturn(connectorUser);

        // When
        var mvcResult = mockMvc.perform(patch(URL)
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editSelfRequest))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Then
        assertThat(mvcResult).isNotNull();
        var json = mvcResult.getResponse().getContentAsString();
        var actual = objectMapper.readValue(json, ConnectorUserDto.class);
        assertThat(actual).isEqualTo(expected);

        var editUserCommand =
            ConnectorEditSelfRequest.toCommand(connectorUser.uuid(), editSelfRequest);
        verify(editUser).execute(connectorUser.uuid(), editUserCommand);

        assertNoMoreInteractions();
    }

    @Test
    void getByIdentifier_should_return_user_found() throws Exception {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();
        var connectorUserDto = ConnectorUserTestFixtures.createUserDto();
        var userPrincipal = ConnectorUserTestFixtures.createUserDetails();

        when(retrieveUser.execute(any())).thenReturn(connectorUser);

        // When
        var mvcResult = mockMvc.perform(get(URL)
                .with(authenticatedAs(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Then
        assertThat(mvcResult).isNotNull();
        var json = mvcResult.getResponse().getContentAsString();
        var actual = objectMapper.readValue(json, ConnectorUserDto.class);
        assertThat(actual).isEqualTo(connectorUserDto);

        verify(retrieveUser).execute(connectorUser.uuid());
        assertNoMoreInteractions();
    }

    @Test
    void getByIdentifier_should_returns_404_when_user_not_found() throws Exception {
        var userPrincipal = ConnectorUserTestFixtures.createUserDetails();

        when(retrieveUser.execute(any())).thenThrow(ConnectorUserNotFoundException.class);

        // When
        // Then
        mockMvc.perform(get(URL)
                .with(authenticatedAs(userPrincipal))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(editUser, retrieveUser);
    }

    @TestConfiguration
    @EnableWebSecurity
    static class WebSecurityTestConfig {
    }
}
