/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.user.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
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
class ConnectorVerifyUserRefreshTokenServiceTest {
    @Mock
    private ConnectorRefreshTokenRepository refreshTokenRepository;
    @Mock
    private ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    @Mock
    private Clock clock;

    @InjectMocks
    private ConnectorVerifyUserRefreshTokenService verifyUserTokenService;

    @Test
    void verify_should_throw_exception_when_refresh_token_is_expired() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var userId = "uuid";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).enabled(true).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .token(token)
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.minus(duration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByTokenHashed(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        // Then
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    void verify_should_throw_exception_when_the_user_is_disabled() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid("uuid").enabled(false).build();
        var refreshToken = ConnectorRefreshToken.builder()
            .token(token)
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.plus(Duration.ofDays(3)))
            .build();

        when(refreshTokenRepository.findByTokenHashed(any()))
            .thenReturn(Optional.of(refreshToken));

        // When
        // Then
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    void verify_should_only_refuse_a_revoked_token_presented_within_the_grace_period() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var now = Instant.parse("2026-11-23T10:00:10Z");
        var refreshToken = revokedToken(token, now.minusSeconds(5));

        when(clock.instant()).thenReturn(now);
        when(authenticationTokenProvider.getRefreshTokenReuseGrace())
            .thenReturn(Duration.ofSeconds(10));
        when(refreshTokenRepository.findByTokenHashed(any()))
            .thenReturn(Optional.of(refreshToken));

        // When
        // Then
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        verify(refreshTokenRepository, never()).revokeByUserUuid(any(), any());
    }

    @Test
    void verify_should_revoke_every_token_when_a_revoked_token_is_presented_again_later() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var now = Instant.parse("2026-11-23T10:00:11Z");
        var refreshToken = revokedToken(token, now.minusSeconds(60));

        when(clock.instant()).thenReturn(now);
        when(authenticationTokenProvider.getRefreshTokenReuseGrace())
            .thenReturn(Duration.ofSeconds(10));
        when(refreshTokenRepository.findByTokenHashed(any()))
            .thenReturn(Optional.of(refreshToken));

        // When
        // Then
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        verify(refreshTokenRepository).revokeByUserUuid("uuid", now);
    }

    @Test
    void verify_should_revoke_every_token_when_a_revoked_token_has_no_revocation_date() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var now = Instant.parse("2026-11-23T10:00:11Z");
        var refreshToken = revokedToken(token, null);

        when(clock.instant()).thenReturn(now);
        when(refreshTokenRepository.findByTokenHashed(any()))
            .thenReturn(Optional.of(refreshToken));

        // When
        // Then
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        verify(refreshTokenRepository).revokeByUserUuid("uuid", now);
    }

    private static ConnectorRefreshToken revokedToken(String token, Instant revokedAt) {
        return ConnectorRefreshToken.builder()
            .token(token)
            .revoked(true)
            .revokedAt(revokedAt)
            .user(ConnectorUser.builder().uuid("uuid").enabled(true).build())
            .expiresAt(Instant.parse("2026-12-23T10:00:00Z"))
            .build();
    }

    @Test
    void verify_should_throw_exception_when_refresh_token_not_found() {
        // Given
        var token = "token";

        when(refreshTokenRepository.findByTokenHashed(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> verifyUserTokenService.execute(token));

        // Then
        verify(refreshTokenRepository).findByTokenHashed(
            ConnectorRefreshTokenGenerator.hash(token));
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    void verify_should_return_refresh_token_when_it_is_not_expired() {
        // Given
        var token = ConnectorRefreshTokenGenerator.generate();
        var userId = "uuid";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().uuid(userId).enabled(true).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .token(token)
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByTokenHashed(any())).thenReturn(
            Optional.of(expectedRefreshToken));

        // When
        var refreshToken = verifyUserTokenService.execute(token);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isEqualTo(expectedRefreshToken);

        verify(refreshTokenRepository).findByTokenHashed(
            ConnectorRefreshTokenGenerator.hash(token));
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }
}
