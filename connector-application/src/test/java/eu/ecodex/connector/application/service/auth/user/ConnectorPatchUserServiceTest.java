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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorPatchUserServiceTest {

    @Mock
    ConnectorUserRepository repository;

    @Mock
    ConnectorUserPasswordEncoder passwordEncoder;

    @InjectMocks
    ConnectorRegisterUserService service;

    @Test
    void patch_should_patch_user() {
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

        var encodedPwd = "encoded";
        var encoded = user.toBuilder().password(encodedPwd).build();
        var expected = encoded.toBuilder().uuid(identifier).build();

        when(repository.findByUuid(any())).thenReturn(Optional.of(expected));
        when(repository.existsByEmailAndUuidNot(any(), any())).thenReturn(Boolean.FALSE);
        when(repository.existsByUsernameAndUuidNot(any(), any())).thenReturn(Boolean.FALSE);
        when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);
        when(passwordEncoder.encodePassword(anyString())).thenReturn(encodedPwd);
        when(repository.save(any())).thenReturn(expected);

        // When
        var registered = service.patch(identifier, user);

        // Then
        assertThat(registered).isNotNull();
        assertThat(registered).isEqualTo(expected);

        verify(repository).findByUuid(identifier);
        verify(repository).existsByEmailAndUuidNot(email, identifier);
        verify(repository).existsByUsernameAndUuidNot(username, identifier);
        verify(passwordEncoder).matches(pwd, encodedPwd);
        verify(passwordEncoder).encodePassword(pwd);
        verify(repository).save(expected);

        verifyNoMoreInteractions(repository, passwordEncoder);
    }


    @Test
    void patch_should_throw_user_not_found_exception() {
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

        when(repository.findByUuid(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserNotFoundException.class, () -> service.patch(identifier, user));

        // Then
        verify(repository).findByUuid(identifier);

        verifyNoMoreInteractions(repository, passwordEncoder);
    }


    @Test
    void patch_should_throw_exception_when_mail_already_exists() {
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

        var encodedPwd = "encoded";
        var encoded = user.toBuilder().password(encodedPwd).build();
        var expected = encoded.toBuilder().uuid(identifier).build();

        when(repository.findByUuid(any())).thenReturn(Optional.of(expected));
        when(repository.existsByUsernameAndUuidNot(any(), any())).thenReturn(Boolean.FALSE);
        when(repository.existsByEmailAndUuidNot(any(), any())).thenReturn(Boolean.TRUE);

        // When
        assertThrows(ConnectorUserAlreadyExistsException.class,
            () -> service.patch(identifier, user));

        // Then
        verify(repository).findByUuid(identifier);
        verify(repository).existsByUsernameAndUuidNot(username, identifier);
        verify(repository).existsByEmailAndUuidNot(email, identifier);
        verifyNoMoreInteractions(repository, passwordEncoder);
    }

    @Test
    void patch_should_throw_exception_when_mail_username_exists() {
        // Given
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

        var encodedPwd = "encoded";
        var encoded = user.toBuilder().password(encodedPwd).build();
        var expected = encoded.toBuilder().uuid(identifier).build();

        when(repository.findByUuid(any())).thenReturn(Optional.of(expected));
        when(repository.existsByUsernameAndUuidNot(any(), any())).thenReturn(Boolean.TRUE);

        // When
        assertThrows(ConnectorUserAlreadyExistsException.class,
            () -> service.patch(identifier, user));

        // Then
        verify(repository).findByUuid(identifier);
        verify(repository).existsByUsernameAndUuidNot(username, identifier);
        verifyNoMoreInteractions(repository, passwordEncoder);
    }
}