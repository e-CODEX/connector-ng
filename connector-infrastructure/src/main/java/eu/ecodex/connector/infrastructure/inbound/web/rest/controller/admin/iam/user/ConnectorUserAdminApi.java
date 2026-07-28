/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.iam.user;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Interface for managing connector users via REST APIs.
 * Provides operations for creating, updating, patching, retrieving, listing, and deleting users.
 * This interface defines the contract for user management-related endpoints.
 */
@RequestMapping(path = "/api/v1/admin/users", consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "API for managing connector's users")
public interface ConnectorUserAdminApi {
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Persist a connector user.")
    @PostMapping
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto register(@Valid @RequestBody ConnectorUserRequest userRequest);

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update a connector user.")
    @PutMapping("/{id}")
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto update(@PathVariable("id") String identifier,
                            @Valid @RequestBody ConnectorUserRequest userRequest);

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update partially a connector user.")
    @PatchMapping("/{id}")
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto patch(@PathVariable("id") String identifier,
                           @Valid @RequestBody ConnectorUserRequest userRequest);

    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Retrieve a connector user.")
    @GetMapping(path = "/{id}")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    ConnectorUserDto getById(@PathVariable("id") String identifier);

    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Retrieve all connector's users.")
    @GetMapping
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    List<ConnectorUserDto> getAll();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a connector user by Id.")
    @DeleteMapping(path = "/{id}")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    void deleteById(@PathVariable("id") String userIdentifier);

}
