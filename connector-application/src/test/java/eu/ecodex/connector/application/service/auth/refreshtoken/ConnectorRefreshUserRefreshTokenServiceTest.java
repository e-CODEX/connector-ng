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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.user.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorIssueUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRefreshUserRefreshTokenServiceTest {
    @InjectMocks
    ConnectorRefreshUserRefreshTokenService service;
    @Mock
    private Clock clock;
    @Mock
    private ConnectorIssueUserRefreshToken issueUserRefreshToken;
    @Mock
    private ConnectorRefreshTokenRepository refreshTokenRepository;
    @Mock
    private ConnectorVerifyUserRefreshTokenService verifyUserRefreshTokenService;
    @Mock
    private ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Test
    @DisplayName("Refresh token should throw exception when not found")
    void refresh_throws_exception_when_refresh_token_not_found() {
        // Given
        var refreshToken = "refresh-token";

        when(verifyUserRefreshTokenService.execute(any())).thenThrow(
            ConnectorUserBadCredentialsException.class);

        // When
        assertThrows(ConnectorUserBadCredentialsException.class,
            () -> service.execute(refreshToken));

        // Then
        verify(verifyUserRefreshTokenService).execute(refreshToken);
        assertNoMoreAssertions();
    }


    @Test
    @DisplayName("Refresh token should generate both tokens the access token and the refresh token")
    void refresh_should_generate_new_access_token_and_new_refresh_token() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var newAccessToken = "new-access-token";
        var newRefreshToken = ConnectorRefreshTokenGenerator.generate();
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder()
            .username(username)
            .uuid(userId)
            .mustChangePassword(Boolean.FALSE)
            .build();

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
            .revoked(false)
            .user(user)
            .token(newRefreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(verifyUserRefreshTokenService.execute(any())).thenReturn(found);
        when(authenticationTokenProvider.generateAccessToken(any())).thenReturn(newAccessToken);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);
        when(refreshTokenRepository.findByUserUuidAndRevoked(any(), anyBoolean())).thenReturn(
            List.of());
        when(issueUserRefreshToken.execute(any())).thenReturn(expected);

        // When
        var newToken = service.execute(refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorUserAuthenticationResult.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(verifyUserRefreshTokenService).execute(refreshToken);
        verify(authenticationTokenProvider).generateAccessToken(user);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(newAccessToken);
        verify(refreshTokenRepository).findByUserUuidAndRevoked(userId, false);
        verify(issueUserRefreshToken).execute(user);
        verify(clock, times(2)).instant();
        assertNoMoreAssertions();
    }

    @Test
    @DisplayName("Refresh token should generate both tokens the access token and the refresh token"
        + " and revoke existing refresh tokens")
    void refresh_should_generate_new_access_token_and_refresh_token_if_existing_found() {
        // Given
        var userId = "uuid";
        var username = "user";
        var refreshToken = "refresh-token";
        var newAccessToken = "new-access-token";
        var newRefreshToken = ConnectorRefreshTokenGenerator.generate();
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var user = ConnectorUser.builder()
            .mustChangePassword(Boolean.FALSE)
            .username(username)
            .uuid(userId)
            .build();
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
            .revoked(false)
            .user(user)
            .token(newRefreshToken)
            .expiresAt(loginTime.plus(refreshTokenDuration))
            .build();

        when(clock.instant()).thenReturn(loginTime);
        when(verifyUserRefreshTokenService.execute(any())).thenReturn(found);
        when(authenticationTokenProvider.generateAccessToken(any())).thenReturn(newAccessToken);
        when(authenticationTokenProvider.getAccessTokenExpirationDate(any())).thenReturn(
            accessTokenExpiresAt);
        when(refreshTokenRepository.findByUserUuidAndRevoked(any(), anyBoolean())).thenReturn(
            List.of(found));
        when(issueUserRefreshToken.execute(any())).thenReturn(expected);

        // When
        var newToken = service.execute(refreshToken);

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isEqualTo(ConnectorUserAuthenticationResult.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(accessTokenDuration.getSeconds())
            .refreshExpiresIn(refreshTokenDuration.getSeconds())
            .build()
        );
        verify(verifyUserRefreshTokenService).execute(refreshToken);
        verify(authenticationTokenProvider).generateAccessToken(user);
        verify(authenticationTokenProvider).getAccessTokenExpirationDate(newAccessToken);
        verify(refreshTokenRepository).findByUserUuidAndRevoked(userId, false);
        verify(refreshTokenRepository).revokeByUserUuid(eq(userId), any(Instant.class));
        verify(issueUserRefreshToken).execute(user);
        verify(clock, times(2)).instant();
        assertNoMoreAssertions();
    }

    private void assertNoMoreAssertions() {
        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock,
            verifyUserRefreshTokenService);
    }
}
