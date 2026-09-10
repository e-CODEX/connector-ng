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

import eu.ecodex.connector.application.port.api.auth.token.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorLoginUser;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorLogoutUser;
import eu.ecodex.connector.application.service.auth.token.ConnectorRefreshUserRefreshTokenService;
import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorLoginUserService;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorLogoutUserService;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
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
    private final ConnectorLoginUser loginUserService;
    private final ConnectorRefreshUserRefreshToken refreshUserTokenService;
    private final ConnectorLogoutUser logoutUserService;

    /**
     * Constructs a {@code ConnectorAuthenticationController} with the necessary services for
     * handling user authentication, refreshing tokens, and logging out.
     *
     * @param loginUserService        The {@link ConnectorLoginUserService} responsible for
     *                                managing
     *                                user login operations, including credential validation
     *                                and token
     *                                generation.
     * @param refreshUserTokenService The {@link ConnectorRefreshUserRefreshTokenService}
     *                                used to handle
     *                                user
     *                                token refresh operations, ensuring the access token
     *                                remains valid.
     * @param logoutUserService       The {@link ConnectorLogoutUserService} handling logout
     *                                functionality,
     *                                including revoking user refresh tokens.
     */
    public ConnectorAuthenticationController(
        ConnectorLoginUser loginUserService,
        ConnectorRefreshUserRefreshToken refreshUserTokenService,
        ConnectorLogoutUserService logoutUserService) {
        this.loginUserService = loginUserService;
        this.refreshUserTokenService = refreshUserTokenService;
        this.logoutUserService = logoutUserService;
    }

    @Override
    public ConnectorLoginResponse login(ConnectorLoginRequest request) {
        var loginResponse = loginUserService.execute(request.username(), request.password());
        log.info("User {} successfully logged", request.username());
        return loginResponse;
    }

    @Override
    public ConnectorLoginResponse refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
        ConnectorRefreshTokenRequest request) {

        var accessToken = authorizationHeader.replaceFirst("^Bearer ", "");
        var refreshed = refreshUserTokenService.execute(accessToken, request.refreshToken());

        log.info("Successfully refreshed token");
        return refreshed;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void logout(@AuthenticationPrincipal ConnectorUserDetails userDetails,
                       @RequestBody ConnectorLogoutRequest request) {
        logoutUserService.execute(userDetails.getUserId(), request.refreshToken());
        log.info("Successfully logged out");
    }
}
