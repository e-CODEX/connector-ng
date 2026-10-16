/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByUsername;
import eu.ecodex.connector.infrastructure.adapter.out.auth.identity.ConnectorUserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorUserDetailsServiceImplTest {
    @Mock
    ConnectorRetrieveUserByUsername retrieveUserByUsername;

    @InjectMocks
    ConnectorUserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_should_return_user_details() {
        // Given
        var username = "test";
        var userDetails = ConnectorUserTestFixtures.createUserDetails();
        var user = ConnectorUserTestFixtures.createDefaultUserWithRoles();

        when(retrieveUserByUsername.execute(any())).thenReturn(user);

        // When
        var userDetailsFound = service.loadUserByUsername(username);

        // Then
        assertThat(userDetailsFound).isEqualTo(userDetails);
        verify(retrieveUserByUsername).execute(username);
        verifyNoMoreInteractions(retrieveUserByUsername);
    }

    @Test
    void loadUserByUsername_should_throw_exception_when_user_not_found() {
        // Given
        var username = "test";

        when(retrieveUserByUsername.execute(any())).thenThrow(
            ConnectorUserNotFoundException.class);

        // When
        assertThrows(
            ConnectorUserNotFoundException.class,
            () -> service.loadUserByUsername(username)
        );

        // Then
        verify(retrieveUserByUsername).execute(username);
        verifyNoMoreInteractions(retrieveUserByUsername);
    }
}
