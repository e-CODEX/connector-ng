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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRefreshUserTokenServiceTest {

    @Mock
    ConnectorRefreshTokenRepository refreshTokenRepository;

    @Mock
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Mock
    Clock clock;

    @InjectMocks
    ConnectorRefreshUserTokenService service;

    @Test
    @DisplayName("Refresh token should throw exception when not found")
    void refresh_throws_exception_when_refresh_token_not_found() {
        // Given
        var refreshToken = "refresh-token";
        var accessToken = "access-token";

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.refresh(accessToken, refreshToken));

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    @DisplayName("Refresh token should throw exception when refresh token and access token user "
        + "does not matches")
    void refresh_throws_exception_when_refresh_token_user_not_matches_access_token_user() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var accessToken = "access-token";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().username(username).uuid(userId).build();
        var refreshTokenDuration = Duration.ofDays(30);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .token(refreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn("newUser");

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.refresh(accessToken, refreshToken));

        // Then
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(clock).instant();

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    @DisplayName("Refresh token should generate only the access token when it expires and the "
        + " new generated access token expires before the refresh token expiration date")
    void refresh_should_generate_new_access_token_successfully_without_rotated_refresh_token() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var accessToken = "access-token";
        var newAccessToken = "new-access-token";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().username(username).uuid(userId).build();
        var accessTokenDuration = Duration.ofMinutes(3);
        var accessTokenExpiresAt = loginTime.plus(accessTokenDuration);
        var refreshTokenDuration = Duration.ofDays(30);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .token(refreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn(username);
        when(authenticationTokenProvider.isAccessTokenExpired(any())).thenReturn(true);
        when(authenticationTokenProvider.generateToken(any())).thenReturn(newAccessToken);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);

        // When
        var newToken = service.refresh(accessToken, refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorLoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(authenticationTokenProvider).generateToken(user);
        verify(authenticationTokenProvider).getUsernameFromToken(accessToken);
        verify(authenticationTokenProvider).isAccessTokenExpired(accessToken);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(newAccessToken);
        verify(clock, times(3)).instant();

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    @DisplayName("Refresh token should return the current access token when it is not expired")
    void refresh_should_return_current_access_token_when_access_token_not_expired() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var accessToken = "access-token";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().username(username).uuid(userId).build();
        var accessTokenDuration = Duration.ofMinutes(3);
        var accessTokenExpiresAt = loginTime.plus(accessTokenDuration);
        var refreshTokenDuration = Duration.ofDays(30);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .token(refreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(
            Optional.of(expectedRefreshToken));
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn(username);
        when(authenticationTokenProvider.isAccessTokenExpired(any())).thenReturn(false);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);

        // When
        var newToken = service.refresh(accessToken, refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorLoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(authenticationTokenProvider).getUsernameFromToken(accessToken);
        verify(authenticationTokenProvider).isAccessTokenExpired(accessToken);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(accessToken);
        verify(clock, times(3)).instant();

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    @DisplayName("Refresh token should generate both tokens the access token and the refresh token"
        + " when the new generated access token expires after the refresh token expiration date")
    void refresh_should_generate_new_access_token_and_new_refresh_token() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var accessToken = "access-token";
        var newAccessToken = "new-access-token";
        var newRefreshToken = "new-refresh-token";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().username(username).uuid(userId).build();
        var accessTokenDuration = Duration.ofMinutes(3);
        var accessTokenExpiresAt = loginTime.plus(accessTokenDuration);
        var refreshTokenDuration = Duration.ofDays(3);
        var found = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .token(refreshToken)
            .expiresAt(loginTime.plus(Duration.ofMinutes(2)))
            .build();
        var expected = found.toBuilder()
            .token(newRefreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(found));
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn(username);
        when(authenticationTokenProvider.isAccessTokenExpired(any())).thenReturn(true);
        when(authenticationTokenProvider.generateToken(any())).thenReturn(newAccessToken);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);
        when(refreshTokenRepository.findByUserUuidAndRevoked(any(), anyBoolean())).thenReturn(
            List.of());
        when(authenticationTokenProvider.getRefreshTokenExpiresIn()).thenReturn(
            refreshTokenDuration);
        when(refreshTokenRepository.save(any())).thenReturn(expected);

        // When
        var newToken = service.refresh(accessToken, refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorLoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(authenticationTokenProvider).generateToken(user);
        verify(authenticationTokenProvider).getUsernameFromToken(accessToken);
        verify(authenticationTokenProvider).isAccessTokenExpired(accessToken);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(newAccessToken);
        verify(refreshTokenRepository).save(expected.toBuilder().token(null).build());
        verify(refreshTokenRepository).findByUserUuidAndRevoked(userId, false);
        verify(clock, times(4)).instant();

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }

    @Test
    @DisplayName("Refresh token should generate both tokens the access token and the refresh token"
        + " and revoke existing refresh token when the new generated access token expires after the"
        + " refresh token expiration date")
    void refresh_should_generate_new_access_token_and_refresh_token_if_existing_found() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var accessToken = "access-token";
        var newAccessToken = "new-access-token";
        var newRefreshToken = "new-refresh-token";
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder().username(username).uuid(userId).build();
        var accessTokenDuration = Duration.ofMinutes(3);
        var accessTokenExpiresAt = loginTime.plus(accessTokenDuration);
        var refreshTokenDuration = Duration.ofDays(3);
        var found = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .token(refreshToken)
            .expiresAt(loginTime.plus(Duration.ofMinutes(2)))
            .build();
        var expected = found.toBuilder()
            .token(newRefreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(found));
        when(authenticationTokenProvider.getUsernameFromToken(any())).thenReturn(username);
        when(authenticationTokenProvider.isAccessTokenExpired(any())).thenReturn(true);
        when(authenticationTokenProvider.generateToken(any())).thenReturn(newAccessToken);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);
        when(refreshTokenRepository.findByUserUuidAndRevoked(any(), anyBoolean())).thenReturn(
            List.of(found));
        when(authenticationTokenProvider.getRefreshTokenExpiresIn()).thenReturn(
            refreshTokenDuration);
        when(refreshTokenRepository.save(any())).thenReturn(expected);

        // When
        var newToken = service.refresh(accessToken, refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorLoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(authenticationTokenProvider).generateToken(user);
        verify(authenticationTokenProvider).getUsernameFromToken(accessToken);
        verify(authenticationTokenProvider).isAccessTokenExpired(accessToken);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(newAccessToken);
        verify(refreshTokenRepository).findByUserUuidAndRevoked(userId, false);
        verify(refreshTokenRepository).revokeByUserUuid(userId);
        verify(refreshTokenRepository).save(expected.toBuilder().token(null).build());
        verify(clock, times(4)).instant();

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }
}