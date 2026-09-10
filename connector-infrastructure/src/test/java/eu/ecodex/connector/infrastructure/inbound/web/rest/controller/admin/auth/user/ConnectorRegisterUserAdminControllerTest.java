/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorUserAdminController.class)
class ConnectorRegisterUserAdminControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/admin/users";
    @MockitoBean
    ConnectorUserPasswordEncoder passwordEncoder;
    @MockitoBean
    private ConnectorRetrieveUserByIdentifier connectorRetrieveUserByIdentifier;
    @MockitoBean
    private ConnectorRegisterUser connectorRegisterUser;
    @MockitoBean
    private ConnectorUpdateUser connectorUpdateUser;
    @MockitoBean
    private ConnectorPatchUser connectorPatchUser;
    @MockitoBean
    private ConnectorRemoveUser connectorRemoveUser;
    @MockitoBean
    private ConnectorListUser connectorListUser;
    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_register_user_without_roles_successfully() {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();
        var connectorUserRequest = ConnectorUserTestFixtures.createDefaultUserRequest();
        when(connectorRegisterUser.execute(any())).thenReturn(connectorUser);

        // When
        var response = apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(connectorUserRequest)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(ConnectorUserDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody)
            .usingRecursiveComparison()
            .isEqualTo(ConnectorUserTestFixtures.createUserDto());

        verify(connectorRegisterUser).execute(connectorUser
            .toBuilder()
            .uuid(null)
            .password("test_password")
            .build());

        assertNoMoreInteractions();
    }

    @Test
    void should_register_user_with_roles_successfully() {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var connectorUserRequest = ConnectorUserTestFixtures.createDefaultUserRequestWithRoles();
        when(connectorRegisterUser.execute(any())).thenReturn(connectorUser);

        // When
        var response = apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(connectorUserRequest)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(ConnectorUserDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assert responseBody != null;
        assertThat(responseBody)
            .usingRecursiveComparison()
            .isEqualTo(ConnectorUserTestFixtures.createUserDtoWithRoles());

        verify(connectorRegisterUser).execute(connectorUser
            .toBuilder()
            .uuid(null)
            .password("test_password")
            .build());

        assertNoMoreInteractions();
    }

    @Test
    void should_not_register_user_when_username_is_missing() {
        // Given
        var connectorUserRequest = ConnectorUserTestFixtures.createUserRequest(
            "", null, "password"
        );

        // When
        apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(connectorUserRequest)
            .exchange()
            .expectStatus()
            .is4xxClientError();

        // Then
        assertNoMoreInteractions();
    }

    @Test
    void should_not_register_user_when_password_is_missing() {
        // Given
        var connectorUserRequest = ConnectorUserTestFixtures.createUserRequest(
            "username", null, null
        );

        // When
        apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(connectorUserRequest)
            .exchange()
            .expectStatus()
            .isBadRequest(); // because of filter is deactivated

        // Then
        assertNoMoreInteractions();
    }

    @Test
    void should_not_register_user_when_field_already_exists() {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();
        var connectorUserRequest = ConnectorUserTestFixtures.createDefaultUserRequest();
        when(connectorRegisterUser.execute(any())).thenThrow(
            new ConnectorUserAlreadyExistsException("msg"));
        when(passwordEncoder.encodePassword(any(ConnectorUser.class))).thenReturn(connectorUser);

        // When
        var response = apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(connectorUserRequest)
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.CONFLICT);

        // Then
        assertThat(response).isNotNull();
        verify(connectorRegisterUser).execute(connectorUser.toBuilder()
            .uuid(null)
            .password("test_password")
            .build());
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(connectorPatchUser, connectorListUser, connectorRemoveUser,
            connectorRegisterUser, connectorUpdateUser, connectorRetrieveUserByIdentifier);
    }
}