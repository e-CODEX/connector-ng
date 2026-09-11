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

import eu.ecodex.connector.application.port.api.auth.role.ConnectorAssignRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorUnassignRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing connector role assignments.
 *
 * <p>This controller provides endpoints to register and delete user-role associations
 * within the system. It acts as an implementation of the {@link ConnectorRoleAssignmentAdminApi}
 * interface and utilizes the {@link ConnectorAssignRole} service for performing
 * the core business logic related to role management.
 *
 * <p>Responsibilities:
 * - Registering of roles assigned to a user in the connector system.
 * - Deleting of roles previously assigned to a user.
 *
 * <p>Each operation delegates the actual persistence-level actions to the
 * {@link ConnectorAssignRole} service.
 */
@Slf4j
@RestController
public class ConnectorRoleAssignmentController implements ConnectorRoleAssignmentAdminApi {
    private final ConnectorAssignRole assignRole;
    private final ConnectorUnassignRole unassignRole;

    public ConnectorRoleAssignmentController(ConnectorAssignRole assignRole,
                                             ConnectorUnassignRole unassignRole) {
        this.assignRole = assignRole;
        this.unassignRole = unassignRole;
    }

    @Override
    public ConnectorUserDto register(@NonNull String identifier, String role) {
        var connectorUser = assignRole.execute(identifier, role);
        return ConnectorUserDto.from(connectorUser);
    }

    @Override
    public ConnectorUserDto delete(@NonNull String identifier, @NonNull String role) {
        ConnectorUser connectorUser = unassignRole.execute(identifier, role);
        return ConnectorUserDto.from(connectorUser);
    }
}
