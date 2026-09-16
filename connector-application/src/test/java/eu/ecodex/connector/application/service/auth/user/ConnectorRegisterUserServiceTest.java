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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRegisterUserServiceTest {
    @Mock
    private ConnectorVerifyUniqueUserService verifyUniqueUser;
    @Mock
    private ConnectorUserRepository repository;
    @Mock
    private ConnectorUserPasswordEncoder passwordEncoder;

    @InjectMocks
    private ConnectorRegisterUserService service;

    @Test
    void register_should_register_user() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";

        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .roles(Set.of(ConnectorRole.builder().name("ROLE_USER").build()))
            .build();

        var encoded = user.toBuilder().password("encoded").build();
        var expected = encoded.toBuilder().uuid("identifier").build();

        doNothing().when(verifyUniqueUser).execute(any());
        when(passwordEncoder.encodePassword(any(ConnectorUser.class))).thenReturn(encoded);
        when(repository.save(any())).thenReturn(expected);

        // When
        var registered = service.execute(user);

        // Then
        assertThat(registered).isNotNull();
        assertThat(registered).isEqualTo(expected);
        verify(verifyUniqueUser).execute(user);
        verify(passwordEncoder).encodePassword(user);
        verify(repository).save(encoded);

        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(repository, passwordEncoder);
    }

    @Test
    void register_should_register_user_when_empty_mail() {
        // Given
        var username = "user";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .roles(Set.of(ConnectorRole.builder().name("ROLE_USER").build()))
            .build();
        var encoded = user.toBuilder().password("encoded").build();
        var expected = encoded.toBuilder().uuid("identifier").build();

        doNothing().when(verifyUniqueUser).execute(any());
        when(passwordEncoder.encodePassword(any(ConnectorUser.class))).thenReturn(encoded);
        when(repository.save(any())).thenReturn(expected);

        // When
        var registered = service.execute(user);

        // Then
        assertThat(registered).isNotNull();
        assertThat(registered).isEqualTo(expected);
        verify(verifyUniqueUser).execute(user);
        verify(passwordEncoder).encodePassword(user);
        verify(repository).save(encoded);

        assertNoMoreInteractions();
    }

    @Test
    void register_should_throw_exception_when_mail_already_exists() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .roles(Set.of(ConnectorRole.builder().name("ROLE_USER").build()))
            .build();
        var message = "User email 'email@test.com' already exists";
        doThrow(new ConnectorUserAlreadyExistsException(message))
            .when(verifyUniqueUser).execute(any());

        // When
        // Then
        assertThatThrownBy(() -> service.execute(user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(message);
        // Then
        verify(verifyUniqueUser).execute(user);
        assertNoMoreInteractions();
    }

    @Test
    void register_should_throw_exception_when_mail_username_exists() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";

        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .roles(Set.of(ConnectorRole.builder().name("ROLE_USER").build()))
            .build();

        var message = "User name 'user' already exists";
        doThrow(new ConnectorUserAlreadyExistsException(message))
            .when(verifyUniqueUser).execute(any());

        // When
        // Then
        assertThatThrownBy(() -> service.execute(user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(message);
        // Then
        verify(verifyUniqueUser).execute(user);
        assertNoMoreInteractions();
    }
}