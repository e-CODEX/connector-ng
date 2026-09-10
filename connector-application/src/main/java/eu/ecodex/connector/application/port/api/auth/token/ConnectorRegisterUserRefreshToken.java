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

import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Defines methods for creating refresh tokens in the Connector system.
 */
public interface ConnectorRegisterUserRefreshToken {
    /**
     * Creates a new refresh token for the specified user in the Connector system.
     * This method generates a secure, unique refresh token associated with the
     * provided user, which can be used to manage authentication sessions.
     *
     * @param token the {@code ConnectorUser} instance representing the user
     *              for whom the refresh token will be created.
     *
     * @return a {@code ConnectorRefreshToken} representing the newly created
     *     refresh token, which includes details such as the user, token
     *     identifier, expiration time, and creation time.
     */
    ConnectorRefreshToken execute(@Nonnull ConnectorUser token);
}
