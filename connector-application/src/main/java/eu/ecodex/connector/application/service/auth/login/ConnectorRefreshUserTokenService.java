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

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.login.ConnectorRefreshUserToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing and refreshing user authentication tokens.
 * This service handles the creation, verification, revocation, and refreshing of
 * {@link ConnectorRefreshToken} objects.
 *
 * <p>Responsibilities include:
 * - Generating new refresh tokens for authenticated users.
 * - Validating and verifying the status of refresh tokens, including expiration and revocation.
 * - Revoking tokens explicitly when needed.
 * - Generating new access tokens for users via the refresh token process.
 *
 * <p>Dependencies:
 * - {@link ConnectorRefreshTokenRepository}: Used for CRUD operations on refresh tokens.
 * - {@link ConnectorAuthenticationTokenProvider}: Provides mechanisms for generating new access
 * tokens.
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
        log.debug("Creating refresh token for user {}", user.uuid());

        List<ConnectorRefreshToken> revoked =
                repository.findByUserUuidAndRevoked(user.uuid(), false);

        if (!revoked.isEmpty()) {
            repository.revokeAllByUserUuid(user.uuid());
        }

        var refreshToken = ConnectorRefreshToken
                .builder()
                .revoked(false)
                .user(user)
                .expiresAt(Instant
                        .now()
                        .plus(authenticationTokenProvider.refreshTokenExpires()))
                .build();

        return repository.save(refreshToken);
    }

    @Override
    public ConnectorRefreshToken verify(String userId, String token) {
        var refreshToken =
                repository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ConnectorUserBadCredentialsException("Invalid refresh token"));

        if (!refreshToken
                .user()
                .uuid()
                .equals(userId)) {
            throw new ConnectorUserBadCredentialsException("Invalid refresh token");
        }

        if (refreshToken.revoked()) {
            throw new ConnectorUserBadCredentialsException("Refresh token revoked");
        }

        if (refreshToken
                .expiresAt()
                .isBefore(Instant.now())) {
            throw new ConnectorUserBadCredentialsException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void revoke(String userId, String token) {
        var refreshToken =
                repository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ConnectorUserBadCredentialsException("Invalid refresh token"));

        if (!refreshToken
                .user()
                .uuid()
                .equals(userId)) {
            throw new ConnectorUserBadCredentialsException(
                    "Invalid refresh token for user " + userId);
        }

        log.info("Revoking refresh token {}", token);
        if (refreshToken.revoked()) {
            return;
        }
        refreshToken
                .toBuilder()
                .revoked(true);
        repository.save(refreshToken);
    }

    @Override
    public LoginResponse refresh(String userId, String token) {
        var refreshToken = this.verify(userId, token);
        var user = refreshToken.user();
        var accessToken = authenticationTokenProvider.generateToken(user);

        return new LoginResponse(accessToken, token,
                authenticationTokenProvider.accessTokenExpiresInSeconds()
        );
    }

}
