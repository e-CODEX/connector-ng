/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.refreshtoken;

import eu.ecodex.connector.application.exception.user.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import jakarta.annotation.Nonnull;

/**
 * Use case for verifying refresh tokens of users in the connector system.
 */
public interface ConnectorVerifyUserRefreshToken {

    /**
     * Verifies the authenticity and validity of the given refresh token.
     *
     * <p>The method checks that the token is genuine, has not expired and has not been revoked.
     *
     * @param refreshToken the refresh token to verify (must not be null)
     *
     * @return the {@link ConnectorRefreshToken} matching the given token if it is valid
     *
     * @throws ConnectorUserBadCredentialsException if the token is unknown, expired or revoked
     */
    ConnectorRefreshToken execute(@Nonnull String refreshToken);
}
