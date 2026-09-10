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

import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import jakarta.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * A service implementation for generating, validating, and parsing JWT tokens.
 * This class uses a symmetric key for signing and verifying tokens, as well as customizable
 * properties provided via {@link JwtProperties}.
 *
 * <p>This implementation provides methods to create tokens, extract information
 * from tokens, and validate tokens against specific user details.
 *
 * <p>It conforms to the {@link ConnectorAuthenticationTokenProvider} interface.
 *
 * <p>Responsibilities:
 * - Generate JWT tokens with user-specific claims and expiration times.
 * - Extract the username from an existing token payload.
 * - Validate if a token matches the user details and is not expired.
 * - Parse the token to extract and verify its claims.
 */
@Slf4j
@Service
public class JwtAuthenticationProvider implements ConnectorAuthenticationTokenProvider {
   private final JwtService jwtTokenService;
   private final JwtProperties jwtProperties;

    public JwtAuthenticationProvider(JwtService jwtTokenService, JwtProperties jwtProperties) {
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateAccessToken(@Nonnull ConnectorUser connectorUser) {
        var user = new ConnectorUserDetails(connectorUser);
        return jwtTokenService.generateAccessToken(user);
    }

    @Override
    public Duration getAccessTokenExpiresIn() {
        return jwtProperties.getExpiration();
    }

    @Override
    public Duration getRefreshTokenExpiresIn() {
        return jwtProperties.getRefreshToken().expiration();
    }

    @Override
    public boolean isAccessTokenExpired(@NonNull String token) {
        return jwtTokenService.isExpired(token);
    }

    @Override
    public Instant getAccessTokenExpirationDate(@NonNull String token) {
        var claims = jwtTokenService.parseAllowingExpired(token);
        return claims.getExpiration().toInstant();
    }

    @Override
    public String getUsernameFromToken(@NonNull String token) {
        var claims = jwtTokenService.parseAllowingExpired(token);
        return claims.getSubject();
    }

    @Override
    public String getRefreshTokenCleanupCron() {
        return jwtProperties.getRefreshToken().cleanupCron();
    }
}
