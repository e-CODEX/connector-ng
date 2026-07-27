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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API interface for managing connector user roles.
 *
 * <p>This interface exposes endpoints for creating, updating, retrieving, and deleting
 * roles assigned to connector users. It allows for management of the roles within
 * the connector's authorization and role-based access control system.
 *
 * <p>All endpoints consume and produce JSON data.
 */
@RequestMapping(path = "/api/v1/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Assign Users roles", description = "API for managing connector's users")
public interface ConnectorRoleAssignmentAdminApi {

    @Operation(summary = "Assign an existing role to user.")
    @PostMapping(path = "/{uuid}/roles")
    @ApiResponses(@ApiResponse(responseCode = "400", description = "Bad Request"))
    ConnectorUserDto register(@PathVariable("uuid") String identifier, @RequestBody String role);

    @Operation(summary = "Unassign a user role.")
    @DeleteMapping(path = "/{uuid}/roles")
    @ApiResponses(@ApiResponse(responseCode = "404", description = "Not Found"))
    ConnectorUserDto delete(@PathVariable("uuid") String identifier, @RequestBody String role);

}
