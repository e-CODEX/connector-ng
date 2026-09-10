/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.token;

import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import jakarta.annotation.Nonnull;

/**
 * Defines methods for refreshing user tokens in the Connector system.
 */
public interface ConnectorRefreshUserRefreshToken {
    /**
     * Refreshes the authentication session by generating a new access token using
     * the provided refresh token. This method validates the given refresh token
     * and, if valid, issues a new access token while preserving the user's session.
     *
     * @param token       the refresh token used to obtain a new access token
     * @param accessToken the current access token associated with the user's session
     *
     * @return a {@code LoginResponse} containing the new access token,
     *     refresh token, and expiration details of the session
     */
    ConnectorLoginResponse execute(@Nonnull String accessToken, @Nonnull String token);
}
