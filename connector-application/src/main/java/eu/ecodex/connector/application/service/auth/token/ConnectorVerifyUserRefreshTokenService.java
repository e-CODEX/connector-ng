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
import eu.ecodex.connector.application.port.api.auth.token.ConnectorVerifyUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import java.time.Clock;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
public class ConnectorVerifyUserRefreshTokenService implements ConnectorVerifyUserRefreshToken {
    private final ConnectorRefreshTokenRepository repository;
    private final Clock clock;

    public ConnectorVerifyUserRefreshTokenService(
        ConnectorRefreshTokenRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public ConnectorRefreshToken execute(@NonNull String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new ConnectorUserBadCredentialsException("Invalid refresh token");
        }
        var foundToken = repository.findByToken(refreshToken)
            .orElseThrow(() -> new ConnectorUserBadCredentialsException("Invalid refresh token"));

        if (foundToken.user().uuid() == null) {
            throw new ConnectorUserBadCredentialsException("Invalid refresh token");
        }

        if (foundToken.revoked()) {
            throw new ConnectorUserBadCredentialsException("Refresh token revoked");
        }

        if (!foundToken.expiresAt().isAfter(clock.instant())) {
            throw new ConnectorUserBadCredentialsException("Refresh token expired");
        }
        return foundToken;
    }
}
