/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.roleassignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRoleAssignment;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorRoleAssignmentController.class)
class ConnectorRoleAssignmentControllerTest extends AbstractWebMvcTest {

    private static final String URL = "/api/v1/admin/users/%s/roles";

    @MockitoBean
    ConnectorRegisterRoleAssignment registerRoleAssignment;

    @Autowired
    private RestTestClient apiClient;


    @Test
    void register_role_assignment_should_return_OK() {
        // Given
        var userIdentifier = "user-identifier";
        var roleUser = "ROLE_USER";
        var connectorUser = ConnectorUserTestFixtures.createDefaultUserWithRoles();

        when(registerRoleAssignment.register(any(), any())).thenReturn(connectorUser);

        // When
        var registeredUser = apiClient.post()
            .uri(String.format(URL, userIdentifier))
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleUser)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class)
            .getResponseBody();

        // Then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.roles()).hasSize(1);

        verify(registerRoleAssignment).register(userIdentifier, roleUser);
        verifyNoMoreInteractions(registerRoleAssignment);
    }

    @Test
    void delete_role_assignment_should_return_OK() {
        // Given
        var userIdentifier = "user-identifier";
        var roleUser = "ROLE_USER";
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();

        when(registerRoleAssignment.remove(any(), any())).thenReturn(connectorUser);

        // When
        var registeredUser = apiClient.method(HttpMethod.DELETE)
            .uri(String.format(URL, userIdentifier))
            .body(roleUser)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class)
            .getResponseBody();

        // Then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.roles()).isNull();

        verify(registerRoleAssignment).remove(userIdentifier, roleUser);
        verifyNoMoreInteractions(registerRoleAssignment);
    }
}