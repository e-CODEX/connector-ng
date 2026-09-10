/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorVerifyUniqueUserServiceTest {
    @Mock
    private ConnectorVerifyUniqueUsernameService verifyUniqueUsernameService;
    @Mock
    private ConnectorVerifyUniqueUserEmailService verifyUniqueUserEmailService;

    @InjectMocks
    private ConnectorVerifyUniqueUserService service;

    @Test
    void execute_should_verify_uniqueness_when_registering_a_new_user() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        doNothing().when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());
        // When
        service.execute(user);

        //Then
        verify(verifyUniqueUsernameService).execute(user);
        verify(verifyUniqueUserEmailService).execute(user);
        assertNoMoreInteractions();
    }

    @Test
    void execute_with_identifier_should_verify_uniqueness_when_updating_user() {
        // Given
        var identifier = "uuid";
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        doNothing().when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());
        // When
        service.execute(identifier, user);

        //Then
        var userToUpdate = user.toBuilder().uuid(identifier).build();
        verify(verifyUniqueUsernameService).execute(userToUpdate);
        verify(verifyUniqueUserEmailService).execute(userToUpdate);
        assertNoMoreInteractions();
    }

    @Test
    void execute_should_throw_Exception_on_registering_when_new_user_username_already_exists() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        var errorMessage = "Username already exists";
        doThrow(new ConnectorUserAlreadyExistsException(errorMessage))
            .when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());

        // When
        assertThatThrownBy(() -> service.execute(user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(errorMessage);

        //Then
        verify(verifyUniqueUsernameService).execute(user);
        verify(verifyUniqueUserEmailService).execute(user);
        assertNoMoreInteractions();
    }

    @Test
    void execute_with_identifier_should_throw_Exception_on_updating_when_user_username_already_exists() {
        // Given
        var identifier = "uuid";
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        var errorMessage = "Username already exists";
        doThrow(new ConnectorUserAlreadyExistsException(errorMessage))
            .when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());

        // When
        assertThatThrownBy(() -> service.execute(identifier, user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(errorMessage);

        //Then
        var userToUpdate = user.toBuilder().uuid(identifier).build();
        verify(verifyUniqueUsernameService).execute(userToUpdate);
        verify(verifyUniqueUserEmailService).execute(userToUpdate);
        assertNoMoreInteractions();
    }

    @Test
    void execute_should_throw_Exception_on_registering_when_new_user_email_already_exists() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        var errorMessage = "User email already exists";
        doThrow(new ConnectorUserAlreadyExistsException(errorMessage))
            .when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());

        // When
        assertThatThrownBy(() -> service.execute(user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(errorMessage);

        //Then
        verify(verifyUniqueUsernameService).execute(user);
        verify(verifyUniqueUserEmailService).execute(user);
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(verifyUniqueUserEmailService, verifyUniqueUsernameService);
    }

    @Test
    void execute_with_identifier_should_throw_Exception_on_updating_when_user_email_already_exists() {
        // Given
        var identifier = "uuid";
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();

        var errorMessage = "User email already exists";
        doThrow(new ConnectorUserAlreadyExistsException(errorMessage))
            .when(verifyUniqueUsernameService).execute(any());
        doNothing().when(verifyUniqueUserEmailService).execute(any());

        // When
        assertThatThrownBy(() -> service.execute(identifier, user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(errorMessage);

        //Then
        var userToUpdate = user.toBuilder().uuid(identifier).build();
        verify(verifyUniqueUsernameService).execute(userToUpdate);
        verify(verifyUniqueUserEmailService).execute(userToUpdate);
        assertNoMoreInteractions();
    }
}