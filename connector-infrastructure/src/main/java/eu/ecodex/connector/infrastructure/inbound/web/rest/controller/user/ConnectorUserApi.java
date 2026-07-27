/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.user;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interface for managing connector users via REST APIs.
 * Provides operations for patching and retrieving a user.
 * This interface defines the contract for user management-related endpoints.
 */
@RequestMapping(path = "/api/v1/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Account or User profile", description = "API for managing connector's current user")
public interface ConnectorUserApi {

    @Operation(summary = "Update partially the currently connected user profile.")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto patch(@AuthenticationPrincipal ConnectorUserDetails userDetails,
                           @Valid @RequestBody ConnectorUserRequest userRequest);

    @Operation(summary = "Get the currently authenticated user account.")
    @GetMapping
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    ConnectorUserDto getByIdentifier(@AuthenticationPrincipal ConnectorUserDetails userDetails);

}
