/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.auth;

import eu.ecodex.connector.application.port.api.auth.ConnectorLoginUser;
import eu.ecodex.connector.application.port.api.auth.ConnectorLogoutUser;
import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordAtFirstLogin;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordCommand;
import eu.ecodex.connector.application.service.auth.ConnectorLoginUserService;
import eu.ecodex.connector.application.service.auth.ConnectorLogoutUserService;
import eu.ecodex.connector.application.service.auth.refreshtoken.ConnectorRefreshUserRefreshTokenService;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorUpdateUserPasswordRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.identity.ConnectorUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling user login operations for the connector system. This class
 * implements the {@link ConnectorAuthenticationApi} interface, providing API functionality for
 * authenticating users and returning an access token upon successful login.
 *
 * <p>The login process involves validating user credentials and generating a token
 * using the provided {@code ConnectorLoginUserService}.
 *
 */
@Slf4j
@RestController
public class ConnectorAuthenticationController implements ConnectorAuthenticationApi {
    private final ConnectorLoginUser loginUser;
    private final ConnectorLogoutUser logoutUser;
    private final ConnectorUpdateUserPasswordAtFirstLogin updateUserPasswordAtFirstLogin;
    private final ConnectorRefreshUserRefreshToken refreshUserToken;

    /**
     * Constructs a {@code ConnectorAuthenticationController} with the necessary services for
     * handling user authentication, refreshing tokens, and logging out.
     *
     * @param loginUser                      The {@link ConnectorLoginUserService}
     *                                       responsible for managing user login operations
     * @param logoutUser                     The {@link ConnectorLogoutUserService} responsible for
     *                                       managing user logout operations
     * @param refreshUserToken               The {@link ConnectorRefreshUserRefreshTokenService}
     *                                       used to handle user token refresh operations, ensuring
     *                                       the access token remains valid.
     * @param updateUserPasswordAtFirstLogin The service that will process update of user password
     */
    public ConnectorAuthenticationController(
        ConnectorLoginUser loginUser,
        ConnectorLogoutUser logoutUser,
        ConnectorUpdateUserPasswordAtFirstLogin updateUserPasswordAtFirstLogin,
        ConnectorRefreshUserRefreshToken refreshUserToken) {
        this.loginUser = loginUser;
        this.logoutUser = logoutUser;
        this.updateUserPasswordAtFirstLogin = updateUserPasswordAtFirstLogin;
        this.refreshUserToken = refreshUserToken;
    }

    @Override
    public ConnectorUserAuthenticationResult login(@NonNull ConnectorLoginRequest request) {
        var loginResponse =
            loginUser.execute(request.username(), request.password());
        log.debug("User {} successfully logged in.", request.username());
        return loginResponse;
    }

    @Override
    public ConnectorUserAuthenticationResult refreshJwtToken(
        @RequestHeader(HttpHeaders.AUTHORIZATION) @NonNull String authorizationHeader,
        @NonNull ConnectorRefreshTokenRequest request) {
        var accessToken = authorizationHeader.replaceFirst("^Bearer ", "");

        return refreshUserToken.execute(accessToken, request.refreshToken());
    }

    @Override
    public void logout(
        @AuthenticationPrincipal @NonNull ConnectorUserDetails userDetails,
        @RequestBody @NonNull ConnectorLogoutRequest request) {
        logoutUser.execute(userDetails.getUserId(), request.refreshToken());
        log.debug("User {} successfully logged out.", userDetails.getUserId());
    }

    @Override
    public void updatePasswordAfterLogin(
        @NonNull ConnectorUserDetails userDetails,
        @NonNull ConnectorUpdateUserPasswordRequest userPasswordRequest) {
        try {
            var passwordUpdateData = ConnectorUpdateUserPasswordRequest.from(
                userDetails.getUserId(), userDetails.accessToken(), userPasswordRequest);

            updateUserPasswordAtFirstLogin.execute(passwordUpdateData);
        } catch (ExpiredJwtException e) {
            var authenticationResult = refreshUserToken.execute(
                userDetails.accessToken(),
                userPasswordRequest.refreshToken()
            );

            var passwordUpdateData = ConnectorUpdateUserPasswordCommand
                .builder()
                .uuid(userDetails.getUserId())
                .accessToken(authenticationResult.accessToken())
                .refreshToken(authenticationResult.refreshToken())
                .newPassword(userPasswordRequest.newPassword())
                .currentPassword(userPasswordRequest.currentPassword())
                .build();

            updateUserPasswordAtFirstLogin.execute(passwordUpdateData);
        }
        log.debug("User {} password successfully updated.", userDetails.getUserId());
    }
}
