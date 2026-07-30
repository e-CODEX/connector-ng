/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.auth;

import static java.nio.charset.StandardCharsets.UTF_8;

import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.infrastructure.outbound.security.auth.login.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.property.auth.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * A service implementation for generating, validating, and parsing JWT tokens.
 * This class uses a symmetric key for signing and verifying tokens, as well as customizable
 * properties provided via {@link JwtProperties}.
 * <p>
 * This implementation provides methods to create tokens, extract information
 * from tokens, and validate tokens against specific user details.
 * <p>
 * It conforms to the {@link ConnectorAuthenticationTokenProvider} interface.
 * <p>
 * Dependencies:
 * - {@link JwtProperties}: Specifies configuration values such as the secret key and expiration period.
 * - {@link UserDetails}: Represents authenticated user information, including roles and username.
 * - {@link SecretKey}: Used for cryptographic operations.
 * <p>
 * Thread-safety:
 * This class is thread-safe assuming the provided {@link JwtProperties}
 * have been correctly initialized and remain immutable during runtime.
 * <p>
 * Responsibilities:
 * - Generate JWT tokens with user-specific claims and expiration times.
 * - Extract the username from an existing token payload.
 * - Validate if a token matches the user details and is not expired.
 * - Parse the token to extract and verify its claims.
 */
@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtTokenService {

    SecretKey secretKey;
    JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(UTF_8));
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(ConnectorUserDetails user) {
        Instant now = Instant.now();
        var userRoles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        log.debug("Generating JWT token for user {} with roles {}", user.getUsername(), userRoles);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtProperties.getExpiration().toSeconds())))
                .claim("roles",
                        userRoles)
                .signWith(secretKey)
                .compact();

    }


    public String extractUsername(String token) {
        return parse(token)
                .getPayload()
                .getSubject();
    }


    public List<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = parse(token).getPayload();
        Object claim = claims.get("roles");

        if (!(claim instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }


    public boolean isValidToken(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername())
                && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return parse(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}
