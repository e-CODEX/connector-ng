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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorUserAdminController.class)
class ConnectorRetrieveUserAdminControllerTest extends AbstractWebMvcTest {

    private static final String URL = "/api/v1/admin/users";

    @MockitoBean
    ConnectorRegisterUser registerUser;

    @MockitoBean
    ConnectorRetrieveUser retrieveUser;

    @MockitoBean
    ConnectorRemoveUser removeUser;

    @MockitoBean
    ConnectorListUser listUser;

    @Autowired
    private RestTestClient apiClient;

    @Test
    void getByIdentifier_should_return_user_found() {
        // Given
        var identifier = "uuid";
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();

        when(retrieveUser.getByIdentifier(any())).thenReturn(connectorUser);

        // When
        var response = apiClient.get()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody)
            .usingRecursiveComparison()
            .isEqualTo(ConnectorUserTestFixtures.createUserDto());

        verify(retrieveUser).getByIdentifier(identifier);
        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser);
    }

    @Test
    void getByIdentifier_should_return_404_when_user_not_found() {
        // Given
        var identifier = "uuid";

        when(retrieveUser.getByIdentifier(any())).thenThrow(ConnectorUserNotFoundException.class);

        // When
        apiClient.get()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isNotFound();

        // Then
        verify(retrieveUser).getByIdentifier(identifier);
        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser);
    }

    @Test
    void getAll_should_return_all_users() {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();

        when(listUser.findAllWithRoles()).thenReturn(List.of(connectorUser));

        // When
        var response = apiClient.get()
            .uri(URL)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(new ParameterizedTypeReference<List<ConnectorUserDto>>() {
            });

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody)
            .usingRecursiveComparison()
            .isEqualTo(List.of(ConnectorUserTestFixtures.createUserDto()));

        verify(listUser).findAllWithRoles();
        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser);
    }
}