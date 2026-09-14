/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.auth.login;

import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import jakarta.annotation.Nonnull;

/**
 * Interface for handling user authentication operations within the connector system.
 * This includes functionalities such as user login to generate authentication tokens
 * and user logout to securely terminate sessions.
 */
public interface ConnectorUserAuthenticationProvider {
    /**
     * Authenticates a user based on their provided username and password.
     * Upon successful authentication, returns a {@link ConnectorUserAuthenticationResult}
     * containing the
     * generated access token, token type, and expiration details.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     *
     * @return a {@link ConnectorUserAuthenticationResult} object containing the authentication
     *     token details
     */
    ConnectorUserAuthenticationResult login(@Nonnull String username, @Nonnull String password);

    /**
     * Executes the logout process for a specific user.
     * This method handles operations related to invalidating the refresh token
     * associated with the given user to ensure secure termination of the user's session.
     *
     * @param userIdentifier the identifier of the user initiating the logout
     * @param refreshToken   the refresh token to be invalidated
     */
    void logout(@Nonnull String userIdentifier, @Nonnull String refreshToken);
}
