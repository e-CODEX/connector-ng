/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.iam.auth;

import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.domain.model.user.ConnectorUser;

/**
 * Defines methods for managing and verifying refresh tokens in the Connector system.
 * <p>
 * This interface provides operations to create, verify, and revoke refresh tokens associated
 * with user authentication sessions. It is designed to handle secure token management
 * and lifecycle operations as part of the Connector authentication flow.
 * <p>
 * Responsibilities of this interface include:
 * - Generating a refresh token for a given user.
 * - Validating the authenticity and integrity of an existing token.
 * - Revoking an issued token to invalidate it, enhancing security by preventing its further use.
 */
public interface ConnectorRefreshUserToken {

    /**
     * Creates a new refresh token for the specified user in the Connector system.
     * This method generates a secure, unique refresh token associated with the
     * provided user, which can be used to manage authentication sessions.
     *
     * @param token the {@code ConnectorUser} instance representing the user
     *              for whom the refresh token will be created.
     * @return a {@code ConnectorRefreshToken} representing the newly created
     *         refresh token, which includes details such as the user, token
     *         identifier, expiration time, and creation time.
     */
    ConnectorRefreshToken create(ConnectorUser token);

    /**
     * Verifies the authenticity and validity of a given refresh token within the Connector system.
     * This method checks whether the provided token is valid, not expired, and has not been revoked.
     *
     * @param token the refresh token to be verified
     * @return a {@code ConnectorRefreshToken} instance representing the details of the verified token,
     *         including user information, expiration, and revocation status
     */
    ConnectorRefreshToken verify(String token);

    /**
     * Revokes the specified refresh token within the Connector system.
     * This method invalidates the provided refresh token, making it unusable for future authentication operations.
     * Typically used to enhance security by ensuring the token can no longer be utilized after explicit revocation.
     *
     * @param refreshToken the {@code ConnectorRefreshToken} instance to be revoked.
     *                     This token contains details such as its unique identifier, associated user,
     *                     and current revocation status.
     */
    void revoke(ConnectorRefreshToken refreshToken);

    /**
     * Refreshes the authentication session by generating a new access token using
     * the provided refresh token. This method validates the given refresh token
     * and, if valid, issues a new access token while preserving the user's session.
     *
     * @param token the refresh token used to obtain a new access token
     * @return a {@code LoginResponse} containing the new access token,
     *         refresh token, and expiration details of the session
     */
    LoginResponse refresh(String token) ;

}
