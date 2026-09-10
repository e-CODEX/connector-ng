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
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorLoginUser;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.service.auth.token.ConnectorRegisterUserRefreshTokenService;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

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
 * - Returns a {@link ConnectorLoginResponse} containing the token details.
 */
@Slf4j
@Service
public class ConnectorLoginUserService implements ConnectorLoginUser {
    private final AuthenticationManager authenticationManager;
    private final ConnectorAuthenticationTokenProvider tokenProvider;
    private final ConnectorRegisterUserRefreshToken registerRefreshToken;

    /**
     * Constructor for the {@code ConnectorLoginUserService}.
     * Initializes the service with dependencies required for user login operations.
     *
     * @param authenticationManager The {@link AuthenticationManager} used to manage
     *                              authentication processes such as validating user
     *                              credentials.
     * @param tokenProvider         The {@link ConnectorAuthenticationTokenProvider}
     *                              responsible
     *                              for generating and validating authentication tokens.
     * @param registerRefreshToken  The {@link ConnectorRegisterUserRefreshTokenService} used
     *                              to
     *                              create user
     *                              refresh tokens and handle related operations.
     */
    public ConnectorLoginUserService(AuthenticationManager authenticationManager,
                                     ConnectorAuthenticationTokenProvider tokenProvider,
                                     ConnectorRegisterUserRefreshTokenService
                                         registerRefreshToken) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.registerRefreshToken = registerRefreshToken;
    }

    @Override
    public ConnectorLoginResponse execute(@Nonnull String username, @Nonnull String password) {
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
            var accessToken = tokenProvider.generateAccessToken(authenticatedUser);
            var refreshToken = registerRefreshToken.execute(authenticatedUser);

            return new ConnectorLoginResponse(accessToken, refreshToken.token(),
                tokenProvider.getAccessTokenExpiresIn().toSeconds(),
                tokenProvider.getRefreshTokenExpiresIn().toSeconds());

        } catch (DisabledException exception) {
            throw new ConnectorUserAccountInactiveException(
                "Your account is inactive. Please contact support.");
        } catch (AuthenticationException e) {
            throw new ConnectorUserBadCredentialsException("Invalid username or password");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
