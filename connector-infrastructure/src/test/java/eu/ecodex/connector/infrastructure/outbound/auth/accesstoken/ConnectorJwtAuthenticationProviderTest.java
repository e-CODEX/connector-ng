/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.accesstoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.infrastructure.property.auth.ConnectorJwtProperties;
import eu.ecodex.connector.infrastructure.property.auth.ConnectorRefreshTokenProperties;
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
class ConnectorJwtAuthenticationProviderTest {
    @Mock
    ConnectorJwtParser tokenParser;
    @Mock
    ConnectorJwtGenerator tokenGenerator;

    @Mock
    ConnectorJwtProperties jwtProperties;

    @InjectMocks
    ConnectorJwtAuthenticationProvider jwtProvider;

    @Test
    void generateToken_should_return_token() {
        // Given
        var user = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var userDetails = ConnectorUserTestFixtures.createUserDetails();

        when(tokenGenerator.generateAccessToken(any())).thenReturn("token");

        // When
        var token = jwtProvider.generateAccessToken(user);

        // Then
        assertThat(token).isEqualTo("token");

        verify(tokenGenerator).generateAccessToken(userDetails);
        verifyNoMoreInteractions(tokenParser);
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
        var props = new ConnectorRefreshTokenProperties(expiration, StringUtils.EMPTY);

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
        when(tokenParser.isExpired(any())).thenReturn(Boolean.FALSE);

        // When
        boolean expired = jwtProvider.isAccessTokenExpired("token");

        // Then
        assertThat(expired).isFalse();
        verify(tokenParser).isExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void isAccessTokenExpired_should_return_TRUE() {
        // Given
        when(tokenParser.isExpired(any())).thenReturn(Boolean.TRUE);

        // When
        boolean expired = jwtProvider.isAccessTokenExpired("token");

        // Then
        assertThat(expired).isTrue();
        verify(tokenParser).isExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void getAccessTokenExpirationDate_should_return_access_token_expiration_date() {
        // Given
        var claims = mock(Claims.class);
        var dateString = "2026-09-01T00:00:00Z";
        when(claims.getExpiration()).thenReturn(Date.from(Instant.parse(dateString)));
        when(tokenParser.parseAllowingExpired(any())).thenReturn(claims);

        // When
        var actual = jwtProvider.getAccessTokenExpirationDate("token");

        // Then
        assertThat(actual).isNotNull().isEqualTo(Instant.parse(dateString));
        verify(tokenParser).parseAllowingExpired("token");
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(tokenParser, jwtProperties);
    }

    @Test
    void getUsernameFromToken_should_extract_username_when_token_is_expired() {
        // Given
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("username");
        when(tokenParser.parseAllowingExpired(any())).thenReturn(claims);

        // When
        var actual = jwtProvider.getUsernameFromToken("token");

        // Then
        assertThat(actual).isNotNull().isEqualTo("username");
        verify(tokenParser).parseAllowingExpired("token");
        assertNoMoreInteractions();
    }

    @Test
    void getRefreshTokenCleanupCron_should_return_cron_expression() {
        // Given
        var cron = "0 0 3 * * *";
        var expiration = Duration.ofDays(2);
        var props = new ConnectorRefreshTokenProperties(expiration, cron);

        when(jwtProperties.getRefreshToken()).thenReturn(props);

        // When
        // Then
        assertThat(jwtProvider.getRefreshTokenCleanupCron()).isEqualTo(cron);
        verify(jwtProperties).getRefreshToken();
        assertNoMoreInteractions();
    }
}