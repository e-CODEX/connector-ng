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

import eu.ecodex.connector.domain.model.login.LoginResponse;

/**
 * Represents an interface for handling user login functionality within the connector system.
 * Provides a contract for authenticating users using their credentials and fetching an access token upon successful login.
 * <p>
 * Methods:
 * - {@link #login(String, String)}: Authenticates the user based on the provided username and password
 * and generates an authentication response containing access token details.
 * <p>
 * Implementations of this interface should ensure proper security measures, including password validation
 * and token generation, to safeguard user data and ensure secure access.
 */
public interface ConnectorLoginUser {
    /**
     * Authenticates a user based on their provided username and password.
     * Upon successful authentication, returns a {@link LoginResponse} containing the generated access token,
     * token type, and expiration details.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return a {@link LoginResponse} object containing the authentication token details
     */
    LoginResponse login(String username, String password);
}
