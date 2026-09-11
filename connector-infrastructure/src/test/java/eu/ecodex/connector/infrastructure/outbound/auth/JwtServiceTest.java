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

import eu.ecodex.connector.ConnectorUserTestFixtures;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import eu.ecodex.connector.infrastructure.property.auth.jwt.RefreshTokenProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JwtService}.
 * This class contains tests for the JwtService class.
 */
class JwtServiceTest {

    private static final Instant FIXED_NOW = Instant.now();
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000L; // 15 min
    private static final String SECRET_STRING =
        "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcd";

    private final RefreshTokenProperties refreshTokenProps =
        new RefreshTokenProperties(Duration.ofDays(3), "0 0 3 * * *");
    private final ConnectorUserDetails userDetails = ConnectorUserTestFixtures.createUserDetails();

    private final JwtProperties jwtProperties =
        new JwtProperties(SECRET_STRING, Duration.ofMillis(ACCESS_TOKEN_EXPIRATION_MS),
            refreshTokenProps);


    private JwtService jwtService;

    private JwtService jwtServiceAt(JwtProperties jwtProperties, Instant instant) {
        return new JwtService(jwtProperties, Clock.fixed(instant, ZoneOffset.UTC));
    }

    @Test
    void generateToken_should_generate_valid_token() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);

        // When
        var accessToken = jwtService.generateAccessToken(userDetails);

        // Then
        assertThat(accessToken).isNotNull().isNotBlank();
        assertThat(accessToken.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    void extractUsername_should_extract_username_from_token() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var accessToken = jwtService.generateAccessToken(userDetails);

        // When
        var extractedUsername = jwtService.extractUsername(accessToken);

        // Then
        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    void extractAuthorities() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var accessToken = jwtService.generateAccessToken(userDetails);

        // When
        var extractedAuthorities = jwtService.extractAuthorities(accessToken);

        // Then
        assertThat(extractedAuthorities).isEqualTo(userDetails.getAuthorities());
    }

    @Test
    void isValidToken_should_return_FALSE_afterExpiration() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);

        var afterExpiry = FIXED_NOW.plusMillis(ACCESS_TOKEN_EXPIRATION_MS).plusSeconds(1);
        var jwtServiceLater = jwtServiceAt(jwtProperties, afterExpiry);

        assertThat(jwtServiceLater.isValidToken(token, userDetails)).isFalse();
    }


    @Test
    void isValidToken_should_return_TRUE_when_token_is_valid() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);

        // When
        // Then
        assertThat(jwtService.isValidToken(token, userDetails)).isTrue();
    }

    @Test
    void isValidToken_should_return_false_when_token_is_invalid() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);
        var otherUser = ConnectorUserDetails.builder().connectorUser(
            ConnectorUser.builder().build()
        ).build();

        // When
        // Then
        assertThat(jwtService.isValidToken(token, otherUser)).isFalse();
    }

    @Test
    void isValidToken_should_return_false_when_token_is_tampered() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);
        var tampered = token.substring(0, token.length() - 2) + "xx";

        // When
        // Then
        assertThat(jwtService.isValidToken(tampered, userDetails)).isFalse();
    }

    @Test
    void isValidToken_should_return_false_when_token_signed_ByDifferentKey() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var otherKey = "fedcba9876543210fedcba9876543210fedcba9876543210fedcba98765432";
        var props = new JwtProperties(otherKey, Duration.ofMillis(ACCESS_TOKEN_EXPIRATION_MS),
            refreshTokenProps);

        var jwtServiceOtherKey = new JwtService(props, Clock.fixed(FIXED_NOW, ZoneOffset.UTC));
        var tokenFromOtherIssuer = jwtServiceOtherKey.generateAccessToken(userDetails);

        // When
        // Then
        assertThat(jwtService.isValidToken(tokenFromOtherIssuer, userDetails)).isFalse();
    }


    @Test
    void isExpired_should_return_TRUE_when_token_has_expired() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);

        var afterExpiry = FIXED_NOW.plusMillis(ACCESS_TOKEN_EXPIRATION_MS).plusSeconds(1);
        var expiredJwtService = jwtServiceAt(jwtProperties, afterExpiry);

        // When
        var isExpired = expiredJwtService.isExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    void isExpired_should_return_FALSE_when_token_has_not_expired() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);

        var afterExpiry = FIXED_NOW.plusMillis(ACCESS_TOKEN_EXPIRATION_MS).minusSeconds(1);
        var expiredJwtService = jwtServiceAt(jwtProperties, afterExpiry);

        // When
        var isExpired = expiredJwtService.isExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void parseAllowingExpired_should_parse_expired_token() {
        // Given
        jwtService = jwtServiceAt(jwtProperties, FIXED_NOW);
        var token = jwtService.generateAccessToken(userDetails);

        var afterExpiry = FIXED_NOW.plusMillis(ACCESS_TOKEN_EXPIRATION_MS).plusSeconds(1);
        var expiredJwtService = jwtServiceAt(jwtProperties, afterExpiry);

        // When
        var claims = expiredJwtService.parseAllowingExpired(token);

        // Then
        assertThat(claims).isNotEmpty();
        assertThat(claims.getSubject()).isEqualTo(userDetails.getUsername());
    }
}