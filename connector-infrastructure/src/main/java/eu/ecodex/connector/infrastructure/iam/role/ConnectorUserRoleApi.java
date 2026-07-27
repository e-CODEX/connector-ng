/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.iam.role;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines the API for managing connector's users.
 */
@RequestMapping(path = "/api/v1/users/roles", consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "API for managing connector's users")
public interface ConnectorUserRoleApi {

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Persist a connector user role.")
    @PostMapping
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserRoleDto register(@Valid @RequestBody ConnectorUserRoleDto userDto);

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update a connector user.")
    @PutMapping("/{id}")
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserRoleDto update(@PathVariable("id") Long id,
                                @Valid @RequestBody ConnectorUserRoleDto userDto);

    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Retrieve a connector user role.")
    @GetMapping(path = "/{id}")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    ConnectorUserRoleDto getById(@PathVariable("id") Long identifier);

    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Retrieve all connector's user roles.")
    @GetMapping
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    List<ConnectorUserRoleDto> getAll();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a connector user role.")
    @DeleteMapping
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    void delete(@RequestBody ConnectorUserRoleDto userDto);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a connector user role by Id.")
    @DeleteMapping(path = "/{id}")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    void deleteById(@PathVariable("id") Long userIdentifier);

}
