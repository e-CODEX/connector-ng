/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.login;

/**
 * Defines methods for managing and verifying refresh tokens in the Connector system.
 *
 * <p>This interface provides operations to create, verify, and revoke refresh tokens associated
 * with user authentication sessions. It is designed to handle secure token management
 * and lifecycle operations as part of the Connector authentication flow.
 *
 * <p>Responsibilities of this interface include:
 * - Generating a refresh token for a given user.
 * - Validating the authenticity and integrity of an existing token.
 * - Revoking an issued token to invalidate it, enhancing security by preventing its further use.
 */
public interface ConnectorRevokeUserToken {

    /**
     * Revokes a specified refresh token, invalidating it and preventing further use.
     * This method ensures that the provided refresh token is disabled and
     * cannot be used to refresh an authentication session.
     *
     * @param userId       authenticated user
     * @param refreshToken the refresh token to be revoked
     */
    void revoke(String userId, String refreshToken);

}
