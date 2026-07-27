/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorListRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRemoveRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRole;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorRoleAdminController.class)
class ConnectorRoleAdminControllerTest extends AbstractWebMvcTest {

    private static final String URL = "/api/v1/admin/users/roles";

    @MockitoBean
    ConnectorRegisterRole registerRole;

    @MockitoBean
    ConnectorRetrieveRole retrieveRole;

    @MockitoBean
    ConnectorRemoveRole removeRole;

    @MockitoBean
    ConnectorListRole listRole;

    @Autowired
    private RestTestClient apiClient;

    @Test
    void register_should_register_new_role() {
        // Given
        var roleDto = ConnectorRoleDto.builder().name("new_role").build();
        var role = ConnectorRole.builder().name("new_role").build();

        when(registerRole.register(any())).thenReturn(role);

        // When
        var response = apiClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleDto)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(ConnectorRoleDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(roleDto);

        verify(registerRole).register(role);
        assertNoMoreInteractions();
    }

    @Test
    void update_should_update_existing_role() {
        // Given
        var identifier = "uuid";
        var roleDto = ConnectorRoleDto.builder().name("new_role").build();
        var role = ConnectorRole.builder().name("new_role").build();

        when(registerRole.update(any(), any())).thenReturn(role);

        // When
        var response = apiClient.put()
            .uri(URL + "/" + identifier)
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleDto)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorRoleDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(roleDto);

        verify(registerRole).update(identifier, role);
        assertNoMoreInteractions();
    }

    @Test
    void getByIdentifier_should_return_role_found() {
        // Given
        var identifier = "uuid";
        var roleDto = ConnectorRoleDto.builder().name("new_role").build();
        var role = ConnectorRole.builder().name("new_role").build();

        when(retrieveRole.getById(any())).thenReturn(role);

        // When
        var response = apiClient.get()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorRoleDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isEqualTo(roleDto);

        verify(retrieveRole).getById(identifier);
        assertNoMoreInteractions();
    }

    @Test
    void getByIdentifier_should_return_404_when_role_not_found() {
        // Given
        var identifier = "uuid";

        when(retrieveRole.getById(any())).thenThrow(ConnectorRoleNotFoundException.class);

        // When
        apiClient.get()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isNotFound();

        // Then
        verify(retrieveRole).getById(identifier);
        assertNoMoreInteractions();
    }


    @Test
    void getAll_should_return_all_roles() {
        // Given
        var roleDto = ConnectorRoleDto.builder().name("new_role").build();
        var role = ConnectorRole.builder().name("new_role").build();

        when(listRole.findAll()).thenReturn(List.of(role));

        // When
        var response = apiClient.get()
            .uri(URL)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(new ParameterizedTypeReference<List<ConnectorRoleDto>>() {
            });

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).usingRecursiveComparison().isEqualTo(List.of(roleDto));

        verify(listRole).findAll();
        assertNoMoreInteractions();
    }

    @Test
    void deleteByIdentifier() {
        // Given
        var identifier = "uuid";

        doNothing().when(removeRole).deleteByIdentifier(any());

        // When
        apiClient.delete()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Then
        verify(removeRole).deleteByIdentifier(identifier);
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(registerRole, removeRole, listRole, retrieveRole);
    }
}