/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.auth.logout;

import eu.ecodex.connector.application.port.api.iam.auth.ConnectorLogoutUser;
import eu.ecodex.connector.application.port.api.iam.auth.ConnectorRefreshUserToken;
import eu.ecodex.connector.application.port.spi.iam.auth.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.iam.auth.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.application.service.iam.auth.login.ConnectorUserDetails;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Service implementation of the {@link ConnectorLogoutUser} interface responsible for handling user logout functionality.
 * This service ensures that a user's authentication session associated with a given token is invalidated securely.
 * <p>
 * This implementation integrates with the connector system to manage and revoke access tokens, ensuring that
 * the token can no longer be used once the user logs out. Proper security measures should be followed to
 * invalidate sessions and revoke tokens effectively.
 * <p>
 * Annotations:
 * - {@code @Service}: Registers this class as a Spring Service for dependency injection.
 * - {@code @Slf4j}: Provides logging capabilities within the class.
 * - {@code @RequiredArgsConstructor}: Generates a constructor with required arguments for final fields.
 * - {@code @FieldDefaults}: Sets the field default modifiers (e.g., private and final).
 * <p>
 * Interfaces:
 * - Implements: {@link ConnectorLogoutUser} to provide the logout functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRefreshUserTokenService implements ConnectorRefreshUserToken {

    ConnectorRefreshTokenRepository repository;
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Override
    public ConnectorRefreshToken create(ConnectorUser user) {

        ConnectorRefreshToken refreshToken = ConnectorRefreshToken.builder()
                .revoked(false)
                .user(user)
                .expiresAt(Instant.now().plus(Duration.ofDays(30)))
                .build();

        return repository.save(refreshToken);
    }

    @Override
    public ConnectorRefreshToken verify(String token) {
        var refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new BadCredentialsException("Invalid refresh token"));

        if (refreshToken.revoked()) {
            throw new BadCredentialsException("Refresh token revoked");
        }

        if (refreshToken.expiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void revoke(ConnectorRefreshToken refreshToken) {
        refreshToken.toBuilder().revoked(true);
    }

    @Override
    public LoginResponse refresh(String token) {

        ConnectorRefreshToken refreshToken =
                this.verify(token);

        ConnectorUser user = refreshToken.user();
        ConnectorUserDetails userDetails = new ConnectorUserDetails(user);
        String accessToken = authenticationTokenProvider.generateToken(userDetails);

        return new LoginResponse(
                accessToken,
                token,
                3600 // TODO fix me
        );
    }

}
