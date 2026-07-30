/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.login;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentiaslException;
import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.login.ConnectorRefreshUserToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing and refreshing user authentication tokens.
 * This service handles the creation, verification, revocation, and refreshing of
 * {@link ConnectorRefreshToken} objects.
 * <p>
 * Responsibilities include:
 * - Generating new refresh tokens for authenticated users.
 * - Validating and verifying the status of refresh tokens, including expiration and revocation.
 * - Revoking tokens explicitly when needed.
 * - Generating new access tokens for users via the refresh token process.
 * <p>
 * Dependencies:
 * - {@link ConnectorRefreshTokenRepository}: Used for CRUD operations on refresh tokens.
 * - {@link ConnectorAuthenticationTokenProvider}: Provides mechanisms for generating new access tokens.
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
                                new ConnectorUserNotFoundException("Invalid refresh token"));

        if (refreshToken.revoked()) {
            throw new ConnectorUserBadCredentiaslException("Refresh token revoked");
        }

        if (refreshToken.expiresAt().isBefore(Instant.now())) {
            throw new ConnectorUserBadRequestException("Refresh token expired");
        }
        return refreshToken;
    }

    @Override
    public void revoke(ConnectorRefreshToken refreshToken) {
        refreshToken.toBuilder().revoked(true);
    }

    @Override
    public LoginResponse refresh(String token) {
        var refreshToken = this.verify(token);
        var user = refreshToken.user();
        String accessToken = authenticationTokenProvider.generateToken(user);

        return new LoginResponse(accessToken, token,
                authenticationTokenProvider.accessTokenExpiresInSeconds()
        );
    }

}
