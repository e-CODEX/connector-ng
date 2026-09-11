/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import eu.ecodex.connector.infrastructure.property.auth.jwt.RefreshTokenProperties;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @Mock
    JwtService jwtService;

    @Mock
    JwtProperties jwtProperties;

    @InjectMocks
    JwtAuthenticationProvider jwtProvider;

    @Test
    void generateToken_should_return_token() {
        // Given
        var user = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var userDetails = ConnectorUserTestFixtures.createUserDetails();

        when(jwtService.generateAccessToken(any())).thenReturn("token");

        // When
        var token = jwtProvider.generateAccessToken(user);

        // Then
        assertThat(token).isEqualTo("token");

        verify(jwtService).generateAccessToken(userDetails);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void accessTokenExpiresIn_should_return_access_token_duration() {
        // Given
        var ofMinutes = Duration.ofMinutes(10);
        when(jwtProperties.getExpiration()).thenReturn(ofMinutes);

        // When
        var actual = jwtProvider.getAccessTokenExpiresIn();

        // Then
        assertThat(actual).isEqualTo(ofMinutes);

        verify(jwtProperties).getExpiration();
        assertNoMoreInteractions();
    }

    @Test
    void refreshTokenExpires_should_return_refresh_token_duration() {
        // Given
        var expiration = Duration.ofDays(2);
        var props = new RefreshTokenProperties(expiration, StringUtils.EMPTY);

        when(jwtProperties.getRefreshToken()).thenReturn(props);

        // When
        var actual = jwtProvider.getRefreshTokenExpiresIn();

        // Then
        assertThat(actual).isEqualTo(expiration);

        verify(jwtProperties).getRefreshToken();
        assertNoMoreInteractions();
    }

    @Test
    void isAccessTokenExpired_should_return_FALSE() {
        // Given
        when(jwtService.isExpired(any())).thenReturn(Boolean.FALSE);

        // When
        boolean expired = jwtProvider.isAccessTokenExpired("token");

        // Then
        assertThat(expired).isFalse();
        verify(jwtService).isExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void isAccessTokenExpired_should_return_TRUE() {
        // Given
        when(jwtService.isExpired(any())).thenReturn(Boolean.TRUE);

        // When
        boolean expired = jwtProvider.isAccessTokenExpired("token");

        // Then
        assertThat(expired).isTrue();
        verify(jwtService).isExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void getAccessTokenExpirationDate_should_return_access_token_expiration_date() {
        // Given
        var claims = mock(Claims.class);
        var dateString = "2026-09-01T00:00:00Z";
        when(claims.getExpiration()).thenReturn(Date.from(Instant.parse(dateString)));
        when(jwtService.parseAllowingExpired(any())).thenReturn(claims);

        // When
        var actual = jwtProvider.getAccessTokenExpirationDate("token");

        // Then
        assertThat(actual).isNotNull().isEqualTo(Instant.parse(dateString));
        verify(jwtService).parseAllowingExpired("token");
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(jwtService, jwtProperties);
    }

    @Test
    void getUsernameFromToken_should_extract_username_when_token_is_expired() {
        // Given
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("username");
        when(jwtService.parseAllowingExpired(any())).thenReturn(claims);

        // When
        var actual = jwtProvider.getUsernameFromToken("token");

        // Then
        assertThat(actual).isNotNull().isEqualTo("username");
        verify(jwtService).parseAllowingExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void getRefreshTokenCleanupCron_should_return_cron_expression() {
        // Given
        var cron = "0 0 3 * * *";
        var expiration = Duration.ofDays(2);
        var props = new RefreshTokenProperties(expiration, cron);

        when(jwtProperties.getRefreshToken()).thenReturn(props);

        // When
        // Then
        assertThat(jwtProvider.getRefreshTokenCleanupCron()).isEqualTo(cron);
        verify(jwtProperties).getRefreshToken();
        assertNoMoreInteractions();
    }
}