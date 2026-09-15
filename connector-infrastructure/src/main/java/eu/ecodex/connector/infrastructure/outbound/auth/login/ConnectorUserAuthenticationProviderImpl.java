/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.login;

import eu.ecodex.connector.application.exception.ConnectorUserAccountInactiveException;
import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.token.ConnectorRegisterUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.token.ConnectorRevokeUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.service.auth.token.ConnectorRegisterUserRefreshTokenService;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Service implementation responsible for handling user login operations within the connector
 * system.
 * This class provides functionality to authenticate users based on their credentials and generate
 * an authentication token upon successful login.
 *
 * <p>Dependencies:
 * - {@link AuthenticationManager}: Facilitates the authentication of user credentials.
 * - {@link ConnectorAuthenticationTokenProvider}: Responsible for generating authentication
 * tokens.
 *
 * <p>Core functionality:
 * - Verifies user credentials by authenticating through the {@link AuthenticationManager}.
 * - Retrieves the user details upon successful authentication.
 * - Generates an authentication token using the {@link ConnectorAuthenticationTokenProvider}.
 * - Returns a {@link ConnectorUserAuthenticationResult} containing the token details.
 */
@Slf4j
@Component
public class ConnectorUserAuthenticationProviderImpl
    implements ConnectorUserAuthenticationProvider {
    private final AuthenticationManager authenticationManager;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    private final ConnectorRegisterUserRefreshToken registerUserRefreshToken;
    private final ConnectorRevokeUserRefreshToken revokeUserRefreshToken;

    /**
     * Constructor for the {@code ConnectorLoginUserService}.
     * Initializes the service with dependencies required for user login operations.
     *
     * @param authenticationManager       The {@link AuthenticationManager} used to manage
     *                                    authentication processes such as validating user
     *                                    credentials.
     * @param authenticationTokenProvider The {@link ConnectorAuthenticationTokenProvider}
     *                                    responsible for generating and validating authentication
     *                                    tokens.
     * @param registerUserRefreshToken    The {@link ConnectorRegisterUserRefreshTokenService} used
     *                                    to create user refresh tokens and handle related
     *                                    operations.
     */
    public ConnectorUserAuthenticationProviderImpl(AuthenticationManager authenticationManager,
                                                   ConnectorAuthenticationTokenProvider
                                                       authenticationTokenProvider,
                                                   ConnectorRegisterUserRefreshTokenService
                                                       registerUserRefreshToken,
                                                   ConnectorRevokeUserRefreshToken
                                                       revokeUserRefreshToken) {
        this.authenticationManager = authenticationManager;
        this.authenticationTokenProvider = authenticationTokenProvider;
        this.registerUserRefreshToken = registerUserRefreshToken;
        this.revokeUserRefreshToken = revokeUserRefreshToken;
    }

    @Override
    public ConnectorUserAuthenticationResult login(@NonNull String username,
                                                   @NonNull String password) {
        try {
            var authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        username,
                        password
                    )
                );

            var user = (ConnectorUserDetails) authentication.getPrincipal();
            if (user == null) {
                throw new RuntimeException("Error reading user principal");
            }
            var authenticatedUser = user.connectorUser();
            var accessToken = authenticationTokenProvider.generateAccessToken(authenticatedUser);
            var refreshToken = registerUserRefreshToken.execute(authenticatedUser);

            return new ConnectorUserAuthenticationResult(accessToken, refreshToken.token(),
                authenticationTokenProvider.getAccessTokenExpiresIn().toSeconds(),
                authenticationTokenProvider.getRefreshTokenExpiresIn().toSeconds());

        } catch (DisabledException exception) {
            throw new ConnectorUserAccountInactiveException(
                "Your account is inactive. Please contact support.");
        } catch (AuthenticationException e) {
            throw new ConnectorUserBadCredentialsException("Invalid username or password");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(@NonNull String userIdentifier, @NonNull String refreshToken) {
        revokeUserRefreshToken.execute(userIdentifier, refreshToken);
    }
}
