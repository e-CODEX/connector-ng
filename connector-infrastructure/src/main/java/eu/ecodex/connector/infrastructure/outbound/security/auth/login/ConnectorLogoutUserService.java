/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.auth.login;

import eu.ecodex.connector.application.port.api.auth.login.ConnectorLogoutUser;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshUserTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

/**
 * A service implementation for managing user logout functionality in the connector system.
 *
 * <p>
 * This class handles user authentication via provided credentials and generates
 * authentication tokens upon successful login. It ensures that users are authenticated
 * securely and provides access and refresh tokens necessary for accessing protected resources.
 *
 * <p>
 * Responsibilities:
 * - Authenticate users via the provided username and password.
 * - Generate access and refresh tokens for authenticated users.
 * - Encapsulate the mechanisms for token creation and user token refresh workflows.
 *
 * <p>
 * Thread-safety:
 * This class is designed to be used in a multi-threaded environment and operates
 * within the application scope defined by the Spring framework.
 *
 * <p>
 * Dependencies:
 * - {@link AuthenticationManager} for authenticating user credentials.
 * - {@link ConnectorAuthenticationTokenProvider} for generating and managing access tokens.
 * - {@link ConnectorRefreshUserTokenService} for creating and handling refresh token functionality.
 *
 * <p>
 * Exceptions:
 * Throws a {@link RuntimeException} if the principal cannot be retrieved after authentication.
 *
 * <p>
 * Implementation Details:
 * - Uses the {@link AuthenticationManager} to authenticate the user based on their credentials.
 * - Retrieves user details from the authenticated principal.
 * - Leverages the authentication token provider to generate a new access token.
 * - Delegates the refresh token creation process to the configured refresh token service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorLogoutUserService implements ConnectorLogoutUser {

    ConnectorRefreshUserTokenService refreshTokenService;

    @Override
    public void logout(String userId, String refreshToken) {
        refreshTokenService.revoke(userId, refreshToken);
    }
}
