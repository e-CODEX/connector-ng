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

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.login.ConnectorLoginUser;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshUserTokenService;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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
 *
 * <p>Exceptions:
 * - Throws {@link RuntimeException} if the principal (user details) cannot be retrieved after
 * authentication.
 *
 * <p>Annotations:
 * - {@code @Slf4j}: Enables logging for debugging and monitoring purposes.
 * - {@code @Service}: Marks this class as a Spring service component.
 * - {@code @RequiredArgsConstructor}: Generates a constructor for final fields.
 * - {@code @FieldDefaults}: Ensures fields are private and final by default.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorLoginUserService implements ConnectorLoginUser {

    AuthenticationManager authenticationManager;
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    ConnectorRefreshUserTokenService refreshTokenService;


    @Override
    public ConnectorLoginResponse login(String username, String password) {
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
                throw new RuntimeException("Error reading principal");
            }
            var authenticatedUser = user.connectorUser();
            var accessToken = authenticationTokenProvider.generateToken(authenticatedUser);
            var refreshToken = refreshTokenService.create(authenticatedUser);

            return new ConnectorLoginResponse(accessToken, refreshToken.uuid(),
                    authenticationTokenProvider.accessTokenExpiresInSeconds());

        } catch (AuthenticationException exception) {
            throw new ConnectorUserBadCredentialsException("Invalid username or password");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
