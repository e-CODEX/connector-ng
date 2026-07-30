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

/**
 * Represents an interface for handling user logout functionality within the connector system.
 * Provides a contract for invalidating the authentication session associated with a given access token.
 * <p>
 * Method:
 * - {@link #logout(String)}: Invalidates the user's active session, ensuring the access token can no longer be used.
 * <p>
 * Implementations of this interface should ensure proper security practices, such as securely revoking
 * the access token. This is critical to prevent further unauthorized access after the user logs out.
 */
public interface ConnectorLogoutUser {

    void logout(String token);
}
