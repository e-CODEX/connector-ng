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

import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.application.service.auth.refreshtoken.ConnectorRefreshUserRefreshTokenService;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.identity.ConnectorUserDetails;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserAuthenticationProviderImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling user login operations for the connector system.
 * This class implements the {@link ConnectorAuthenticationApi} interface, providing API
 * functionality
 * for authenticating users and returning an access token upon successful login.
 *
 * <p>The login process involves validating user credentials and generating a token
 * using the provided {@code ConnectorLoginUserService}.
 *
 */
@Slf4j
@RestController
public class ConnectorAuthenticationController implements ConnectorAuthenticationApi {
    private final ConnectorUserAuthenticationProvider userAuthenticationProvider;
    private final ConnectorRefreshUserRefreshToken refreshUserTokenService;

    /**
     * Constructs a {@code ConnectorAuthenticationController} with the necessary services for
     * handling user authentication, refreshing tokens, and logging out.
     *
     * @param userAuthenticationProvider The {@link ConnectorUserAuthenticationProviderImpl}
     *                                   responsible for managing user login operations, including
     *                                   credential validation and token generation.
     * @param refreshUserTokenService    The {@link ConnectorRefreshUserRefreshTokenService}
     *                                   used to handle user token refresh operations, ensuring the
     *                                   access token remains valid.
     */
    public ConnectorAuthenticationController(
        ConnectorUserAuthenticationProvider userAuthenticationProvider,
        ConnectorRefreshUserRefreshToken refreshUserTokenService) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.refreshUserTokenService = refreshUserTokenService;
    }

    @Override
    public ConnectorUserAuthenticationResult login(@NonNull ConnectorLoginRequest request) {
        var loginResponse =
            userAuthenticationProvider.login(request.username(), request.password());
        log.info("User {} successfully logged", request.username());
        return loginResponse;
    }

    @Override
    public ConnectorUserAuthenticationResult refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) @NonNull String authorizationHeader,
        @NonNull ConnectorRefreshTokenRequest request) {
        var accessToken = authorizationHeader.replaceFirst("^Bearer ", "");
        var refreshed = refreshUserTokenService.execute(accessToken, request.refreshToken());
        log.info("Successfully refreshed token");
        return refreshed;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void logout(@AuthenticationPrincipal @NonNull ConnectorUserDetails userDetails,
                       @RequestBody @NonNull ConnectorLogoutRequest request) {
        userAuthenticationProvider.logout(userDetails.getUserId(), request.refreshToken());
        log.info("Successfully logged out");
    }
}
