/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.token;

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.token.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.token.ConnectorVerifyUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
public class ConnectorRefreshUserRefreshTokenService implements ConnectorRefreshUserRefreshToken {
    private final ConnectorRefreshTokenRepository repository;
    private final ConnectorVerifyUserRefreshToken verifyUserToken;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    private final Clock clock;

    /**
     * Constructs an instance of {@code ConnectorRefreshUserTokenService}.
     *
     * <p>This service is responsible for handling user token refresh operations,
     * including validation of user tokens and management of authentication
     * and refresh tokens. It operates in conjunction with various supporting
     * components such as repositories and token providers.
     *
     * @param repository                  the {@code ConnectorRefreshTokenRepository} used for
     *                                    managing and persisting refresh tokens.
     * @param verifyUserToken             the {@code ConnectorVerifyUserTokenService}
     *                                    responsible for verifying the validity of user tokens.
     * @param authenticationTokenProvider the {@code ConnectorAuthenticationTokenProvider}
     *                                    responsible for generating and managing authentication
     *                                    tokens.
     * @param clock                       the {@code Clock} instance used for providing current time
     *                                    during token expiration and rotation operations.
     */
    public ConnectorRefreshUserRefreshTokenService(
        ConnectorRefreshTokenRepository repository,
        ConnectorVerifyUserRefreshToken verifyUserToken,
        ConnectorAuthenticationTokenProvider authenticationTokenProvider, Clock clock) {
        this.repository = repository;
        this.verifyUserToken = verifyUserToken;
        this.authenticationTokenProvider = authenticationTokenProvider;
        this.clock = clock;
    }

    @Override
    public ConnectorLoginResponse execute(@NonNull String accessToken,
                                          @NonNull String refreshToken) {
        var verifiedRefreshToken = verifyUserToken.execute(refreshToken);

        if (!authenticationTokenProvider.getUsernameFromToken(accessToken).equals(
            verifiedRefreshToken.user().username())) {
            throw new ConnectorUserBadCredentialsException(
                "Access token and refresh token do not belong to the same user.");
        }

        log.debug("Refreshing access token for user {}", verifiedRefreshToken.user().uuid());
        var newAccessToken = authenticationTokenProvider.isAccessTokenExpired(accessToken)
            ? authenticationTokenProvider.generateAccessToken(verifiedRefreshToken.user())
            : accessToken;

        var accessTokenExpiresAt =
            authenticationTokenProvider.getAccessTokenExpirationDate(newAccessToken);

        var newRefreshToken = shouldRotateRefreshToken(verifiedRefreshToken, accessTokenExpiresAt)
            ? rotateRefreshToken(verifiedRefreshToken.user())
            : verifiedRefreshToken;

        return new ConnectorLoginResponse(newAccessToken, newRefreshToken.token(),
            Duration.between(clock.instant(), accessTokenExpiresAt).getSeconds(),
            Duration.between(clock.instant(), newRefreshToken.expiresAt()).getSeconds()
        );
    }

    /**
     * Rotates the refresh token when it would expire before the access token.
     */
    private boolean shouldRotateRefreshToken(ConnectorRefreshToken refreshToken,
                                             Instant accessTokenExpiresAt) {
        return refreshToken.expiresAt().isBefore(accessTokenExpiresAt);
    }

    /**
     * Creates a new refresh token for the given user.
     *
     * @param user user to create the refresh token for
     *
     * @return newly created refresh token
     */
    private ConnectorRefreshToken saveRefreshToken(ConnectorUser user) {
        var refreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(clock
                .instant()
                .plus(authenticationTokenProvider.getRefreshTokenExpiresIn()))
            .build();

        return repository.save(refreshToken);
    }

    /**
     * Revokes all existing refresh tokens for the given user and creates a new one.
     *
     * @param user user to revoke refresh tokens for
     *
     * @return newly created refresh token
     */
    private ConnectorRefreshToken rotateRefreshToken(ConnectorUser user) {
        var revoked = repository.findByUserUuidAndRevoked(user.uuid(), Boolean.FALSE);
        if (!revoked.isEmpty()) {
            repository.revokeByUserUuid(user.uuid());
        }
        return saveRefreshToken(user);
    }
}
