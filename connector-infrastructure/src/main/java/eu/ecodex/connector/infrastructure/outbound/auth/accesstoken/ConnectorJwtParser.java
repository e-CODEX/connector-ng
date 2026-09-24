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
import eu.ecodex.connector.infrastructure.property.auth.ConnectorJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
public class ConnectorJwtParser {
    private final SecretKey secretKey;
    private final Clock clock;

    /**
     * Constructor for JwtTokenService.
     * Initializes the secret key and JWT properties from the provided JwtProperties.
     *
     * @param jwtProperties the JwtProperties object containing the secret key and other JWT-related
     */
    public ConnectorJwtParser(@NonNull ConnectorJwtProperties jwtProperties, @NonNull Clock clock) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(UTF_8));
        this.clock = clock;
    }

    /**
     * Extracts the username from the payload of the provided JWT token.
     *
     * @param token the JWT token from which the username is to be extracted.
     *
     * @return the username contained in the token payload.
     */
    public String extractUsername(@NonNull String token) {
        return parse(token).getPayload().getSubject();
    }

    /**
     * Extracts the authorities (roles) from the payload of a given JWT token.
     * The roles are converted into a list of {@code GrantedAuthority} objects.
     *
     * @param token the JWT token from which the authorities are to be extracted.
     *
     * @return a list of {@code GrantedAuthority} objects representing the roles
     *     contained in the token, or an empty list if no valid roles are found.
     */
    public List<? extends GrantedAuthority> extractAuthorities(@NonNull String token) {
        Claims claims = parse(token).getPayload();
        Object claim = claims.get("roles");

        if (!(claim instanceof List<?> roles)) {
            return List.of();
        }
        return roles
            .stream()
            .map(String::valueOf)
            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }


    /**
     * Validates the given JWT token by checking if it matches the username of the specified user
     * and ensures the token has not expired.
     *
     * @param token the JWT token to validate.
     * @param user  the {@code UserDetails} object representing the authenticated user whose details
     *              are compared against the token's payload.
     *
     * @return {@code true} if the token is valid, matches the user's username and is not expired;
     *     {@code false} otherwise.
     */
    public boolean isValidToken(@NonNull String token, @NonNull UserDetails user) {
        try {
            var claims = parse(token).getPayload();
            return claims.getSubject().equals(user.getUsername());
        } catch (JwtException e) {
            return false;
        }
    }


    /**
     * Checks if the given JWT token has expired.
     *
     * @param token accessToken to check
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired(@NonNull String token) {
        try {
            parse(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true; // signature verified successfully; only expiration failed
        } catch (JwtException | IllegalArgumentException e) {
            return false; // tampered, malformed, wrong key, or garbage input
        }
    }

    /**
     * Extracts claims even from an expired token, as long as its signature is correct.
     * Used specifically by the refresh flow, where an expired access token is expected
     * and its username still needs to be read to confirm it matches the refresh token's owner.
     *
     * @param token access token to parse
     *
     * @return the parsed claims
     */
    public Claims parseAllowingExpired(@NonNull String token) {
        try {
            return parse(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // signature was already verified before expiration was checked
        }
    }

    /**
     * Parses the given JWT and verifies its cryptographic signature against {@link #secretKey}.
     *
     * <p>The parser uses the injected {@link #clock} (the system clock
     * {@link eu.ecodex.connector.infrastructure.config.ClockConfig}) to evaluate
     * time-based claims such as {@code exp} (expiration) and {@code nbf} (not-before).
     * This makes expiration checks deterministic and testable — tests can advance a fixed/mocked
     * {@link Clock} instead of depending on wall-clock time.
     *
     * @param token the compact, serialized JWT (header.payload.signature)
     *
     * @return the parsed and signature-verified claims, wrapped in their {@link Jws} envelope
     *
     * @throws io.jsonwebtoken.security.SignatureException if the signature does not match
     *                                                     {@link #secretKey} (token was tampered
     *                                                     with or signed by a different key)
     * @throws ExpiredJwtException                         if the token's {@code exp} claim is in
     *                                                     the past,
     *                                                     relative to {@link #clock}
     * @throws io.jsonwebtoken.MalformedJwtException       if the token is not a well-formed JWT
     * @throws io.jsonwebtoken.security.SecurityException  if the JWT algorithm or key is
     *                                                     invalid/unsupported
     * @throws IllegalArgumentException                    if {@code token} is null, empty, or blank
     */
    public Jws<Claims> parse(@NonNull String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .clock(() -> Date.from(clock.instant()))
            .build()
            .parseSignedClaims(token);
    }
}
