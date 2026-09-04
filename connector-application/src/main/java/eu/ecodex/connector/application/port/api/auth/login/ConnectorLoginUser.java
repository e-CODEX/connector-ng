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

import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;

/**
 * Interface for handling user login functionality in the connector system.
 *
 * <p>This interface defines the contract for authenticating users by validating their
 * credentials and providing authentication tokens upon successful login.
 * It facilitates secure access control by issuing tokens required for
 * subsequent authenticated operations in the system.
 *
 * <p>Responsibilities:
 * - Authenticate users based on their credentials (username and password).
 * - Generate and return a {@link ConnectorLoginResponse} object containing the
 * access token, refresh token, and expiration details.
 *
 * <p>Typical Use Cases:
 * - Facilitates user login for accessing secured endpoints.
 * - Provides the foundational mechanism for managing authentication and
 * token issuance workflows.
 *
 * <p>Security:
 * - Ensures proper validation of user credentials, allowing only legitimate
 * users to access the system.
 * - Tokens issued should be securely stored and transmitted to avoid
 * unauthorized access.
 */
public interface ConnectorLoginUser {
    /**
     * Authenticates a user based on their provided username and password.
     * Upon successful authentication, returns a {@link ConnectorLoginResponse} containing the
     * generated access token, token type, and expiration details.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     *
     * @return a {@link ConnectorLoginResponse} object containing the authentication token details
     */
    ConnectorLoginResponse login(String username, String password);
}
