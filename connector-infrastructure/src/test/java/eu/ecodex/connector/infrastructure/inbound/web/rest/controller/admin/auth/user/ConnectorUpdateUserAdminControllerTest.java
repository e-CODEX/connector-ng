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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorUserAdminController.class)
class ConnectorUpdateUserAdminControllerTest extends AbstractWebMvcTest {

    private static final String URL = "/api/v1/admin/users";

    @MockitoBean
    ConnectorRegisterUser registerUser;

    @MockitoBean
    ConnectorRetrieveUser retrieveUser;

    @MockitoBean
    ConnectorRemoveUser removeUser;

    @MockitoBean
    ConnectorListUser listUser;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_update_user_successfully() {
        // Given
        var connectorUser = ConnectorUserTestFixtures.createDefaultUser();
        var connectorUserRequest = ConnectorUserTestFixtures.createDefaultUserRequest();

        when(registerUser.update(anyString(), any())).thenReturn(connectorUser);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        // When
        var response = apiClient
                .put()
                .uri(URL + "/" + connectorUser.uuid())
                .contentType(MediaType.APPLICATION_JSON)
                .body(connectorUserRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ConnectorUserDto.class);

        // Then
        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assert responseBody != null;
        assertThat(responseBody)
                .usingRecursiveComparison()
                .isEqualTo(ConnectorUserTestFixtures.createUserDto());

        verify(registerUser).update(connectorUser.uuid(), connectorUser
                .toBuilder()
                .uuid(null)
                .build());
        verify(passwordEncoder).encode(connectorUserRequest.password());

        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser, passwordEncoder);
    }


}