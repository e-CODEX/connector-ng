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

import eu.ecodex.connector.domain.model.login.ConnectorLoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout.ConnectorLogoutRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Defines the API for managing user login functionality within the connector system.
 * Provides an operation for authenticating users and retrieving an access token upon successful
 * login. This interface serves as a contract for the implementation of user login services.
 *
 * <p>Endpoints:
 * - POST /api/v1/auth/login: Handles user login by accepting credentials in the request body
 * and responding with an authentication token.
 *
 * <p>Annotations:
 * - The class is annotated with @RequestMapping to define the base path for all endpoints.
 * - The @Tag annotation is used for grouping and describing the API in documentation generated
 * via OpenAPI.
 */
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthenticateUser", description = "API for managing connector's users "
    + "authentication")
public interface ConnectorAuthenticationApi {

    @Operation(summary = "Login a connector user.")
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized Request"),
        @ApiResponse(responseCode = "204", description = "Successfully logged in"),
    })
    ConnectorLoginResponse login(@RequestBody ConnectorLoginRequest connectorLoginRequest);


    @Operation(summary = "Refresh a user token.")
    @PostMapping(path = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "204", description = "Successfully refreshed"),
    })
    ConnectorLoginResponse refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
        @RequestBody ConnectorRefreshTokenRequest request);


    @Operation(summary = "Logout a user token.")
    @PostMapping("/logout")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "204", description = "Successfully logged out"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")

    })
    void logout(@AuthenticationPrincipal ConnectorUserDetails userDetails,
                @RequestBody ConnectorLogoutRequest request);

}
