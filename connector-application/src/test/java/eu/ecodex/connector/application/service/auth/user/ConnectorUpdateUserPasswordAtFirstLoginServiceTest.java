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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.exception.ConnectorUserInvalidPasswordException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordCommand;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorUpdateUserPasswordAtFirstLoginServiceTest {
    @InjectMocks
    ConnectorUpdateUserPasswordAtFirstLoginService userPasswordAtFirstLoginService;
    @Mock
    private ConnectorUpdateUserPassword updateUserPassword;
    @Mock
    private ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;

    @Test
    void should_update_user_password_successfully() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("current-password")
            .mustChangePassword(Boolean.TRUE)
            .build();

        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        doNothing().when(updateUserPassword).execute(any());

        // When
        var updateData = ConnectorUpdateUserPasswordCommand.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();
        userPasswordAtFirstLoginService.execute(updateData);

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        assert updateData.accessToken() != null;

        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordCommand.class);
        verify(updateUserPassword).execute(captor.capture());

        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updateData);
        assertNoMoreAssertions();
    }

    @Test
    void should_not_update_user_password_when_new_password_and_current_password_are_same() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("current-password")
            .mustChangePassword(Boolean.TRUE)
            .build();

        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        doThrow(ConnectorUserInvalidPasswordException.class).when(updateUserPassword).execute(
            any());

        // When
        var updateData = ConnectorUpdateUserPasswordCommand.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("current-password")
            .build();

        assertThrows(ConnectorUserInvalidPasswordException.class,
            () -> userPasswordAtFirstLoginService.execute(updateData));

        // Then
        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordCommand.class);
        verify(updateUserPassword).execute(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updateData);
        assertNoMoreAssertions();
    }

    @Test
    void should_not_update_user_password_when_username_does_not_match_access_token_username() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("current-password")
            .mustChangePassword(Boolean.TRUE)
            .build();

        // When
        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        doThrow(ConnectorUserBadCredentialsException.class).when(updateUserPassword).execute(any());

        var updateData = ConnectorUpdateUserPasswordCommand.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();

        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> userPasswordAtFirstLoginService.execute(updateData));

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        assert updateData.accessToken() != null;
        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordCommand.class);
        verify(updateUserPassword).execute(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updateData);
        assertNoMoreAssertions();
    }


    @Test
    void should_not_update_user_password_when_current_password_does_not_match_existing_password() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("existing-password")
            .mustChangePassword(Boolean.TRUE)
            .build();

        // When
        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        doThrow(ConnectorUserInvalidPasswordException.class).when(updateUserPassword).execute(
            any());

        var updateData = ConnectorUpdateUserPasswordCommand.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();

        assertThrows(ConnectorUserInvalidPasswordException.class,
            () -> userPasswordAtFirstLoginService.execute(updateData));

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        var captor = ArgumentCaptor.forClass(ConnectorUpdateUserPasswordCommand.class);
        verify(updateUserPassword).execute(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updateData);
        assertNoMoreAssertions();
    }

    private void assertNoMoreAssertions() {
        verifyNoMoreInteractions(retrieveUserByIdentifier, updateUserPassword);
    }
}