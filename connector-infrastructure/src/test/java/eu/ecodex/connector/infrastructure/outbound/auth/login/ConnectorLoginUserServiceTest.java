/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshUserTokenService;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ConnectorLoginUserServiceTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Mock
    ConnectorRefreshUserTokenService refreshTokenService;

    @InjectMocks
    ConnectorLoginUserService service;

    @Test
    void login_should_succeed() {
        // Given
        var username = "test";
        var password = "password";
        var principal = ConnectorUserTestFixtures.createUserDetails();
        var connectorUser = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var authenticatedToken = new UsernamePasswordAuthenticationToken(
            principal, password, principal.getAuthorities());
        var refreshToken = ConnectorRefreshToken.builder()
            .token("refresh-token-abc")
            .user(connectorUser)
            .revoked(false)
            .build();

        when(authenticationManager.authenticate(any())).thenReturn(authenticatedToken);
        when(authenticationTokenProvider.generateToken(any())).thenReturn("access-token");
        when(refreshTokenService.create(any())).thenReturn(refreshToken);
        when(authenticationTokenProvider.getAccessTokenExpiresInSeconds()).thenReturn(60L);

        // When
        var loginResponse = service.login(username, password);

        // Then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.accessToken()).isEqualTo("access-token");
        assertThat(loginResponse.refreshToken()).isEqualTo("refresh-token-abc");
        assertThat(loginResponse.expiresIn()).isEqualTo(60L);

        var authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        var authCaptorValue = authCaptor.getValue();
        assertThat(authCaptorValue.getPrincipal()).isEqualTo(username);
        assertThat(authCaptorValue.getCredentials()).isEqualTo(password);

        verify(authenticationTokenProvider).generateToken(connectorUser);
        verify(refreshTokenService).create(connectorUser);
        verify(authenticationTokenProvider).getAccessTokenExpiresInSeconds();
        verify(authenticationTokenProvider).getRefreshTokenExpiresIn();
        verifyNoMoreInteractions(authenticationTokenProvider, refreshTokenService);
    }

    @Test
    void login_should_throw_exception_when_authentication_fails() {
        // Given
        var username = "test";
        var password = "password";

        when(authenticationManager.authenticate(any())).thenThrow(
            InsufficientAuthenticationException.class);

        // When
        assertThrows(
            ConnectorUserBadCredentialsException.class, () -> service.login(username, password));

        // Then
        var authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        var authCaptorValue = authCaptor.getValue();
        assertThat(authCaptorValue.getPrincipal()).isEqualTo(username);
        assertThat(authCaptorValue.getCredentials()).isEqualTo(password);

        verifyNoMoreInteractions(authenticationTokenProvider, refreshTokenService);
    }
}