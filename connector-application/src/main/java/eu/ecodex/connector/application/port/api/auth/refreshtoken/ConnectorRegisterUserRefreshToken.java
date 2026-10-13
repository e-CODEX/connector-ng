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

import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;


/**
 * Use case for creating and registering a refresh token for a user of the connector system.
 */
public interface ConnectorRegisterUserRefreshToken {
    /**
     * Creates a new refresh token for the given user.
     *
     * <p>The method generates a secure, unique refresh token associated with the user, which can
     * be used to manage authentication sessions.
     *
     * @param user the {@link ConnectorUser} for whom the refresh token is created (must not be
     *             null)
     *
     * @return the newly created {@link ConnectorRefreshToken}, including details such as the user,
     *     the token identifier, the expiration time and the creation time
     */
    ConnectorRefreshToken execute(@Nonnull ConnectorUser user);
}
