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

import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import java.time.Duration;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
 * <p>Dependencies:
 * - {@link JwtProperties}: Specifies configuration values such as the secret key and expiration
 * period.
 * - {@link UserDetails}: Represents authenticated user information, including roles and username.
 * - {@link SecretKey}: Used for cryptographic operations.
 *
 * <p>Thread-safety:
 * This class is thread-safe assuming the provided {@link JwtProperties}
 * have been correctly initialized and remain immutable during runtime.
 *
 * <p>Responsibilities:
 * - Generate JWT tokens with user-specific claims and expiration times.
 * - Extract the username from an existing token payload.
 * - Validate if a token matches the user details and is not expired.
 * - Parse the token to extract and verify its claims.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthenticationProvider implements ConnectorAuthenticationTokenProvider {

    JwtTokenService jwtTokenService;
    JwtProperties jwtProperties;

    @Override
    public String generateToken(ConnectorUser connectorUser) {
        var user = new ConnectorUserDetails(connectorUser);
        return jwtTokenService.generateToken(user);
    }

    @Override
    public long accessTokenExpiresInSeconds() {
        return jwtProperties
                .getExpiration()
                .toSeconds();
    }

    @Override
    public Duration refreshTokenExpires() {
        return jwtProperties
                .getRefreshToken()
                .expiration();
    }

}
