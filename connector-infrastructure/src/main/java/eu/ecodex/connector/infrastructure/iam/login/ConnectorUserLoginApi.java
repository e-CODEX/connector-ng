/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.iam.login;

import eu.ecodex.connector.domain.model.login.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines the API for managing connector's users login.
 */
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API for managing connector's users")
public interface ConnectorUserLoginApi {
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Login a connector user.")
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            @ApiResponse(responseCode = "400", description = "Bad Request")
    )
    LoginResponse login(@RequestBody LoginRequestDto loginRequestDto);

}
