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

import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshUserTokenService;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshRequest;
import eu.ecodex.connector.infrastructure.outbound.security.auth.login.ConnectorLoginUserService;
import eu.ecodex.connector.infrastructure.outbound.security.auth.login.ConnectorLogoutUserService;
import eu.ecodex.connector.infrastructure.outbound.security.auth.login.ConnectorUserDetails;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling user login operations for the connector system.
 * This class implements the {@link ConnectorAuthenticationApi} interface, providing API functionality
 * for authenticating users and returning an access token upon successful login.
 *
 * <p>
 * The login process involves validating user credentials and generating a token
 * using the provided {@code ConnectorLoginUserService}.
 *
 * <p>
 * Annotations:
 * - {@code @Slf4j}: Enables logging within the class.
 * - {@code @RestController}: Marks the class as a REST controller, allowing it
 * to handle HTTP requests and return responses in a RESTful manner.
 * - {@code @RequiredArgsConstructor}: Automatically generates a constructor with
 * required arguments for final fields.
 * - {@code @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)}:
 * Ensures all fields are private and final by default.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorAuthenticationController implements ConnectorAuthenticationApi {

    ConnectorLoginUserService connectorLoginUserService;
    ConnectorRefreshUserTokenService connectorRefreshUserTokenService;
    ConnectorLogoutUserService connectorLogoutUserService;

    @Override
    public LoginResponse login(ConnectorLoginRequest connectorLoginRequest) {
        var loginResponse = connectorLoginUserService.login(connectorLoginRequest.username(),
                connectorLoginRequest.password());
        log.info("User {} successfully logged", connectorLoginRequest.username());
        return loginResponse;

    }

    @Override
    public LoginResponse refresh(ConnectorRefreshRequest request) {
        var userId = getAuthUserId();
        var refresh = connectorRefreshUserTokenService.refresh(userId, request.refreshToken());
        log.info("Successfully refreshed token");
        return refresh;
    }

    @Override
    public void logout(ConnectorRefreshRequest request) {
        var userId = getAuthUserId();
        connectorLogoutUserService.logout(userId, request.refreshToken());
        log.info("Successfully logged out");
    }

    private String getAuthUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ((ConnectorUserDetails) Objects.requireNonNull(
                authentication.getPrincipal())).getUserId();
    }

}
