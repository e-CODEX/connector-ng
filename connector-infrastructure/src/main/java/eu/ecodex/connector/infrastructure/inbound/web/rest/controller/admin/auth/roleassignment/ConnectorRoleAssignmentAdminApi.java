/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.roleassignment;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * API interface for managing connector user roles.
 * 
 * <p>This interface exposes endpoints for creating, updating, retrieving, and deleting
 * roles assigned to connector users. It allows for management of the roles within
 * the connector's authorization and role-based access control system.
 * 
 * <p>All endpoints consume and produce JSON data.
 */
@PreAuthorize("hasRole(T(eu.ecodex.connector.domain.model.user.ConnectorRoleName.ADMIN))")
@RequestMapping(path = "/api/v1/admin/users", consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "API for managing connector's users")
public interface ConnectorRoleAssignmentAdminApi {

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Persist a connector user role assignments.")
    @PostMapping(path = "/{uuid}/roles")
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto register(@PathVariable("uuid") String identifier, @RequestBody String role);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a connector user role assignment.")
    @DeleteMapping(path = "/{uuid}/roles")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    void delete(@PathVariable("uuid") String identifier, @RequestBody String role);

}
