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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import eu.ecodex.connector.infrastructure.property.auth.jwt.RefreshTokenProperties;
import java.time.Duration;
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
    JwtAuthenticationProvider provider;

    @Test
    void generateToken_should_return_token() {
        // Given
        var user = ConnectorUserTestFixtures.createDefaultUserWithRoles();
        var userDetails = ConnectorUserTestFixtures.createUserDetails();

        when(jwtService.generateAccessToken(any())).thenReturn("token");

        // When
        var token = provider.generateToken(user);

        // Then
        assertThat(token).isEqualTo("token");

        verify(jwtService).generateAccessToken(userDetails);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void accessTokenExpiresInSeconds_should_return_access_token_duration_in_seconds() {
        // Given
        when(jwtProperties.getExpiration()).thenReturn(Duration.ofMinutes(10));

        // When
        var actual = provider.getAccessTokenExpiresInSeconds();

        // Then
        assertThat(actual).isEqualTo(600L);

        verify(jwtProperties).getExpiration();
        verifyNoMoreInteractions(jwtService, jwtProperties);
    }

    @Test
    void refreshTokenExpires_should_return_refresh_token_duration() {
        // Given
        Duration expiration = Duration.ofDays(2);
        var props = new RefreshTokenProperties(expiration);

        when(jwtProperties.getRefreshToken()).thenReturn(props);

        // When
        var actual = provider.getRefreshTokenExpiresIn();

        // Then
        assertThat(actual).isEqualTo(expiration);

        verify(jwtProperties).getRefreshToken();
        verifyNoMoreInteractions(jwtService, jwtProperties);
    }
}