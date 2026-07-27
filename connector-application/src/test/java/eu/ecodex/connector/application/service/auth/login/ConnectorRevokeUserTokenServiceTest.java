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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ConnectorRevokeUserTokenServiceTest {

    @Mock
    ConnectorRefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    ConnectorRevokeUserTokenService service;

    @Test
    void revoke_should_revoke_refresh_token_successfully() {
        // Given
        var userId = "uuid";
        var refreshToken = "token";

        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        service.revoke(userId, refreshToken);

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(refreshTokenRepository).save(expectedRefreshToken.toBuilder().revoked(true).build());

        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void revoke_should_do_nothing_when_refresh_token_already_revoked() {
        // Given
        var userId = "uuid";
        var refreshToken = "token";

        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(true)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        service.revoke(userId, refreshToken);

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);

        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void revoke_should_throw_exception_when_refresh_token_not_found() {
        // Given
        var userId = "uuid";
        var refreshToken = "token";

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.revoke(userId, refreshToken));

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);

        verifyNoMoreInteractions(refreshTokenRepository);
    }


    @Test
    void revoke_should_throw_exception_when_refresh_token_userId_differs() {
        // Given
        var userId = "uuid";
        var refreshToken = "token";

        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(true)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.revoke("anotherUuid", refreshToken));

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);

        verifyNoMoreInteractions(refreshTokenRepository);
    }

}