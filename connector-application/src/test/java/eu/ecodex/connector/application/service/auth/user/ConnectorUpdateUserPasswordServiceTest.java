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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.exception.ConnectorUserInvalidPasswordException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.domain.model.auth.ConnectorUpdateUserPasswordData;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorUpdateUserPasswordServiceTest {
    @InjectMocks
    ConnectorUpdateUserPasswordService updateUserPasswordService;
    @Mock
    private ConnectorPatchUser patchUser;
    @Mock
    private ConnectorUserPasswordEncoder passwordEncoder;
    @Mock
    private ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    @Mock
    private ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Test
    void should_update_user_password() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("current-password")
            .build();

        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn("username");
        when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.TRUE);
        when(patchUser.execute(any(), any())).thenReturn(user);

        // When
        var updateData = ConnectorUpdateUserPasswordData.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();
        updateUserPasswordService.execute(updateData);

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        verify(authenticationTokenProvider).getUsernameFromToken(updateData.accessToken());
        verify(passwordEncoder).matches(updateData.currentPassword(), user.password());

        var captor = ArgumentCaptor.forClass(ConnectorUser.class);
        verify(patchUser).execute(eq(user.uuid()), captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(
            ConnectorUser.builder()
                .uuid(user.uuid())
                .username(user.username())
                .password(updateData.newPassword())
                .build()
        );
        assertNoMoreAssertions();
    }

    @Test
    void should_not_update_user_password_when_new_password_and_current_password_are_same() {
        // Given
        var updateData = ConnectorUpdateUserPasswordData.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("current-password")
            .build();

        // When
        assertThrows(ConnectorUserInvalidPasswordException.class,
            () -> updateUserPasswordService.execute(updateData));

        // Then
        assertNoMoreAssertions();
    }

    @Test
    void should_not_update_user_password_when_username_does_not_match_access_token_username() {
        // Given
        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("current-password")
            .build();

        // When
        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn("username2");

        var updateData = ConnectorUpdateUserPasswordData.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> updateUserPasswordService.execute(updateData));

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        verify(authenticationTokenProvider).getUsernameFromToken(updateData.accessToken());

        assertNoMoreAssertions();
    }


    @Test
    void should_not_update_user_password_when_current_password_does_not_match_existing_password() {
        // Given
        var updateData = ConnectorUpdateUserPasswordData.builder()
            .uuid("uuid")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .currentPassword("current-password")
            .newPassword("new-password")
            .build();

        var user = ConnectorUser.builder()
            .uuid("uuid")
            .username("username")
            .password("existing-password")
            .build();

        // When
        when(retrieveUserByIdentifier.execute(any())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn("username");

        assertThrows(ConnectorUserInvalidPasswordException.class,
            () -> updateUserPasswordService.execute(updateData));

        // Then
        verify(retrieveUserByIdentifier).execute(user.uuid());
        verify(passwordEncoder).matches(updateData.currentPassword(), user.password());
        verify(authenticationTokenProvider).getUsernameFromToken(updateData.accessToken());
        assertNoMoreAssertions();
    }

    private void assertNoMoreAssertions() {
        verifyNoMoreInteractions(retrieveUserByIdentifier, authenticationTokenProvider,
            passwordEncoder, patchUser);
    }
}