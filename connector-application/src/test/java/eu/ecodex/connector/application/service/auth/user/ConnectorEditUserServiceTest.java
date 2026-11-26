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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.user.ConnectorLastAdministratorException;
import eu.ecodex.connector.application.exception.user.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorEditUserCommand;
import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorEditUserServiceTest {
    @Mock
    private ConnectorUserRepository repository;
    @Mock
    private ConnectorVerifyUniqueUserEmailService verifyUniqueUser;
    @Mock
    private ConnectorRetrieveUserByIdentifierService retrieveUserByIdentifier;
    @Mock
    private ConnectorRefreshTokenRepository refreshTokenRepository;
    @Mock
    private ConnectorAdminSafeguard adminSafeguard;
    @Mock
    private Clock clock;

    @InjectMocks
    private ConnectorEditUserService service;

    @Test
    void edit_user_should_update_user_successfully() {
        // Given
        var identifier = "uuid";
        var email = "email@test.com";
        var editUserCommand = ConnectorEditUserCommand.builder()
            .email(email)
            .build();
        var user = ConnectorEditUserCommand.toDomain(editUserCommand);
        var expected = user.toBuilder()
            .uuid(identifier)
            .mustChangePassword(Boolean.FALSE)
            .build();

        when(retrieveUserByIdentifier.execute(any())).thenReturn(expected);
        doNothing().when(verifyUniqueUser).execute(any());
        when(repository.save(any())).thenReturn(expected);

        // When
        var registered = service.execute(identifier, editUserCommand);

        // Then
        assertThat(registered).isNotNull();
        assertThat(registered).isEqualTo(expected);

        verify(retrieveUserByIdentifier).execute(identifier);
        verify(verifyUniqueUser).execute(user);
        verify(repository).save(expected);

        assertNoMoreInteractions();
    }

    @Test
    void edit_should_throw_user_not_found_exception() {
        // Given
        var identifier = "uuid";
        var email = "email@test.com";

        var user = ConnectorEditUserCommand.builder()
            .email(email)
            .build();

        when(retrieveUserByIdentifier.execute(any())).thenThrow(
            ConnectorUserNotFoundException.class);

        // When
        assertThrows(ConnectorUserNotFoundException.class, () -> service.execute(identifier, user));

        // Then
        verify(retrieveUserByIdentifier).execute(identifier);
        assertNoMoreInteractions();
    }


    @Test
    void edit_user_should_throw_exception_when_email_address_already_exists() {
        // Given
        var identifier = "uuid";
        var email = "email@test.com";
        var editUserCommand = ConnectorEditUserCommand.builder()
            .email(email)
            .build();

        var user = ConnectorEditUserCommand.toDomain(editUserCommand);
        var expected = user.toBuilder().uuid(identifier).build();
        var message = "User email 'email@test.com' already exists";

        when(retrieveUserByIdentifier.execute(any())).thenReturn(expected);
        doThrow(new ConnectorUserAlreadyExistsException(message)).when(verifyUniqueUser).execute(
            any());

        // When
        // Then
        assertThatThrownBy(() -> service.execute(identifier, editUserCommand))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(message);

        verify(retrieveUserByIdentifier).execute(identifier);

        verify(verifyUniqueUser).execute(user);
        assertNoMoreInteractions();
    }

    @Test
    void edit_should_throw_exception_when_username_exists() {
        // Given
        var identifier = "uuid";
        var email = "email@test.com";

        var editUserCommand = ConnectorEditUserCommand.builder()
            .email(email)
            .build();
        var user = ConnectorEditUserCommand.toDomain(editUserCommand);
        var expected = user.toBuilder().uuid(identifier).build();
        var message = "User name 'user' already exists";

        when(retrieveUserByIdentifier.execute(any())).thenReturn(expected);
        doThrow(new ConnectorUserAlreadyExistsException(message)).when(verifyUniqueUser).execute(
            any());

        // When
        assertThatThrownBy(() -> service.execute(identifier, editUserCommand))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(message);

        // Then
        verify(retrieveUserByIdentifier).execute(identifier);
        verify(verifyUniqueUser).execute(user);
        assertNoMoreInteractions();
    }

    @Test
    void edit_should_revoke_the_refresh_tokens_when_the_user_is_disabled() {
        // Given
        var existing = ConnectorUser.builder().uuid("uuid").enabled(true).build();
        var now = Instant.parse("2026-11-23T10:00:00Z");
        when(retrieveUserByIdentifier.execute("uuid")).thenReturn(existing);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(clock.instant()).thenReturn(now);

        // When
        var saved = service.execute(
            "uuid", ConnectorEditUserCommand.builder().enabled(false).build()
        );

        // Then
        assertThat(saved.enabled()).isFalse();
        verify(refreshTokenRepository).revokeByUserUuid("uuid", now);
        verify(adminSafeguard).assertAnotherAdministratorRemains(existing);
    }

    @Test
    void edit_should_not_disable_the_last_administrator() {
        // Given
        var existing = ConnectorUser.defaultAdminUser().toBuilder().uuid("uuid").build();
        when(retrieveUserByIdentifier.execute("uuid")).thenReturn(existing);
        doThrow(new ConnectorLastAdministratorException("last"))
            .when(adminSafeguard).assertAnotherAdministratorRemains(existing);

        // When
        var disable = ConnectorEditUserCommand.builder().enabled(false).build();
        assertThatThrownBy(() -> service.execute("uuid", disable))
            .isInstanceOf(ConnectorLastAdministratorException.class);

        // Then
        verify(repository, never()).save(any());
        verify(refreshTokenRepository, never()).revokeByUserUuid(any(), any());
    }

    @Test
    void edit_should_not_revoke_the_refresh_tokens_when_the_user_stays_enabled() {
        // Given
        var existing = ConnectorUser.builder().uuid("uuid").enabled(true).build();
        when(retrieveUserByIdentifier.execute("uuid")).thenReturn(existing);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.execute("uuid", ConnectorEditUserCommand.builder().enabled(true).build());
        service.execute("uuid", ConnectorEditUserCommand.builder().email("a@b.eu").build());

        // Then
        verify(refreshTokenRepository, never()).revokeByUserUuid(any(), any());
        verifyNoInteractions(adminSafeguard);
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(
            repository, retrieveUserByIdentifier, verifyUniqueUser, refreshTokenRepository
        );
    }
}
