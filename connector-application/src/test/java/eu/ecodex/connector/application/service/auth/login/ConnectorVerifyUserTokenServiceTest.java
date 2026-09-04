/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorVerifyUserTokenServiceTest {

    @Mock
    ConnectorRefreshTokenRepository refreshTokenRepository;

    @Mock
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Mock
    Clock clock;

    @InjectMocks
    ConnectorRefreshUserTokenService service;


    @Test
    void verify_should_throw_exception_when_refresh_token_is_expired() {
        // Given
        var token = "token";
        var userId = "uuid";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.minus(duration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.verifyExpiration(token));

        // Then
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    void verify_should_throw_exception_when_refresh_token_not_found() {
        // Given
        var token = "token";

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.verifyExpiration(token));

        // Then
        verify(refreshTokenRepository).findByToken(token);
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    void verify_should_return_refresh_token_when_it_is_not_expired() {
        // Given
        var token = "token";
        var userId = "uuid";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        var refreshToken = service.verifyExpiration(token);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isEqualTo(expectedRefreshToken);

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }
}