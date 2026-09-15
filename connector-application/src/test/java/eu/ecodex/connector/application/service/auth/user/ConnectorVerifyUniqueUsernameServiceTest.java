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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorVerifyUniqueUsernameServiceTest {
    @Mock
    private ConnectorUserRepository connectorUserRepository;

    @InjectMocks
    private ConnectorVerifyUniqueUsernameService service;

    @Test
    void execute_should_not_throw_exception_when_username_does_not_exist() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();
        when(connectorUserRepository.existsByUsername(any())).thenReturn(Boolean.FALSE);

        // When
        service.execute(user);

        // Then
        verify(connectorUserRepository).existsByUsername(username);
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(connectorUserRepository);
    }

    @Test
    void execute_should_throw_exception_when_username_does_not_exist() {
        // Given
        var username = "user";
        var email = "email@test.com";
        var pwd = "password";
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .build();
        when(connectorUserRepository.existsByUsername(any())).thenReturn(Boolean.TRUE);
        var errorMessage = String.format("Username '%s' already exists", username);

        // When
        assertThatThrownBy(() -> service.execute(user))
            .isInstanceOf(ConnectorUserAlreadyExistsException.class)
            .hasMessage(errorMessage);

        // Then
        verify(connectorUserRepository).existsByUsername(username);
        assertNoMoreInteractions();
    }
}
