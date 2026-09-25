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

import static java.nio.charset.StandardCharsets.UTF_8;

import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.infrastructure.outbound.auth.identity.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.property.auth.ConnectorJwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * A service implementation for generating, validating, and parsing JWT tokens.
 * This class uses a symmetric key for signing and verifying tokens, as well as customizable
 * properties provided via {@link ConnectorJwtProperties}.
 *
 * <p>This implementation provides methods to create tokens, extract information
 * from tokens, and validate tokens against specific user details.
 *
 * <p>It conforms to the {@link ConnectorAuthenticationTokenProvider} interface.
 *
 * <p>Dependencies:
 * - {@link ConnectorJwtProperties}: Specifies configuration values such as the secret key and
 * expiration
 * period.
 * - {@link UserDetails}: Represents authenticated user information, including roles and username.
 * - {@link SecretKey}: Used for cryptographic operations.
 *
 **/
@Slf4j
@Service
public class ConnectorJwtGenerator {
    private final SecretKey secretKey;
    private final ConnectorJwtProperties jwtProperties;
    private final Clock clock;

    /**
     * Constructor for JwtTokenService.
     * Initializes the secret key and JWT properties from the provided JwtProperties.
     *
     * @param jwtProperties the JwtProperties object containing the secret key and other JWT-related
     */
    public ConnectorJwtGenerator(@NonNull ConnectorJwtProperties jwtProperties,
                                 @NonNull Clock clock) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(UTF_8));
        this.jwtProperties = jwtProperties;
        this.clock = clock;
    }

    /**
     * Generates a JSON Web Token (JWT) for the given user, including user-specific claims such as
     * username and granted roles, and sets the token's expiration time based on the configured
     * properties.
     *
     * @param user the {@code ConnectorUserDetails} object representing the authenticated user for
     *             whom the token is being generated. It includes user-specific information like
     *             username and granted roles.
     *
     * @return a {@code String} representing the generated JWT token encoded with the user's details
     *     and cryptographically signed using the configured secret key.
     */
    public String generateAccessToken(@NonNull ConnectorUserDetails user) {
        var now = clock.instant();
        var userRoles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        log.debug("Generating JWT token for user {} ", user.getUsername());
        return Jwts.builder()
            .subject(user.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(jwtProperties.getExpiration().toSeconds())))
            .claims(Map.of("roles", userRoles, "userId", user.getUserId()))
            .signWith(secretKey)
            .compact();
    }
}
