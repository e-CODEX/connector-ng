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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorUserAdminController.class)
class ConnectorRemoveUserAdminControllerTest extends AbstractWebMvcTest {

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
    void deleteByIdentifier_should_delete_user_successfully() {
        // Given
        var identifier = "uuid";

        doNothing().when(removeUser).deleteById(any());

        // When
        apiClient.delete()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Then

        verify(removeUser).deleteById(identifier);
        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser);
    }

    @Test
    void deleteByIdentifier_should_throw_404_when_user_not_found() {
        // Given
        var identifier = "uuid";

        doThrow(ConnectorUserNotFoundException.class).when(removeUser).deleteById(any());

        // When
        apiClient.delete()
            .uri(URL + "/" + identifier)
            .exchange()
            .expectStatus()
            .isNotFound();

        // Then

        verify(removeUser).deleteById(identifier);
        verifyNoMoreInteractions(registerUser, retrieveUser, removeUser, listUser);
    }
}