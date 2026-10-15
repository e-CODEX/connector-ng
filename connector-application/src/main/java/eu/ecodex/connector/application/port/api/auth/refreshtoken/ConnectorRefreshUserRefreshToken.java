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
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import jakarta.annotation.Nonnull;

/**
 * Use case for refreshing the authentication session of a user in the connector system.
 */
public interface ConnectorRefreshUserRefreshToken {
    /**
     * Refreshes the authentication session by issuing a new access token from the given refresh
     * token.
     *
     * <p>The method validates the given refresh token and, if it is valid, issues a new access
     * token while preserving the user's session.
     *
     * @param refreshToken the refresh token used to obtain a new access token (must not be null)
     *
     * @return a {@link ConnectorUserAuthenticationResult} containing the new access token, the
     *     refresh token and the expiration details of the session
     *
     * @throws ConnectorUserBadCredentialsException if the refresh token is unknown, expired or
     *                                              revoked
     */
    ConnectorUserAuthenticationResult execute(@Nonnull String refreshToken);
}
