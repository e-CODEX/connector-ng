/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.iam.role;

import eu.ecodex.connector.application.port.api.iam.role.ConnectorListUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRegisterUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRemoveUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRetrieveUserRole;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserRoleDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for managing user roles within the connector system.
 * Provides functionality to create, update, retrieve, list, and delete user roles.
 * Implements the API defined in {@link ConnectorUserRoleAdminApi}.
 * Uses service-level components to execute business logic related to user roles.
 * <p>
 * Annotations:
 * - {@code @Slf4j}: For logging purposes.
 * - {@code @RestController}: Indicates that this class is a REST controller.
 * - {@code @RequiredArgsConstructor}: Generates a constructor with required dependencies.
 * - {@code @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)}: Ensures immutability and encapsulation of fields.
 * <p>
 * Dependencies:
 * - {@link ConnectorRegisterUserRole}: Handles the registration and updating of user roles.
 * - {@link ConnectorRetrieveUserRole}: Retrieves user roles by identifier.
 * - {@link ConnectorRemoveUserRole}: Manages the deletion of user roles.
 * - {@link ConnectorListUserRole}: Provides functionality to list all user roles.
 * <p>
 * Methods:
 * - {@link #register(ConnectorUserRoleDto)}: Registers a new user role in the system.
 * - {@link #update(String, ConnectorUserRoleDto)}: Updates an existing user role by its identifier.
 * - {@link #getById(String)}: Retrieves a specific user role by its unique identifier.
 * - {@link #getAll()}: Fetches all user roles from the system.
 * - {@link #deleteById(String)}: Deletes a user role by its identifier.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRoleAdminController implements ConnectorUserRoleAdminApi {
    ConnectorRegisterUserRole connectorRegisterUserRole;
    ConnectorRetrieveUserRole connectorRetrieveUserRole;
    ConnectorRemoveUserRole connectorRemoveUserRole;
    ConnectorListUserRole connectorListUserRole;


    @Override
    public ConnectorUserRoleDto register(ConnectorUserRoleDto usrRoleDto) {
        log.info("Registering new user role");

        var registered =
                connectorRegisterUserRole.register(ConnectorUserRoleDto.toDomain(usrRoleDto));
        log.info("New user registered");
        return ConnectorUserRoleDto.from(registered);
    }

    @Override
    public ConnectorUserRoleDto update(String identifier, ConnectorUserRoleDto userRoleDto) {
        log.info("Updating existing user");
        var updated = connectorRegisterUserRole.update(identifier,
                ConnectorUserRoleDto.toDomain(userRoleDto));
        log.info("Existing user updated");
        return ConnectorUserRoleDto.from(updated);
    }

    @Override
    public ConnectorUserRoleDto getById(String identifier) {
        ConnectorUserRole byId = connectorRetrieveUserRole.getById(identifier);
        return ConnectorUserRoleDto.from(byId);
    }

    @Override
    public List<ConnectorUserRoleDto> getAll() {
        return connectorListUserRole.findAll().stream()
                .map(ConnectorUserRoleDto::from)
                .toList();
    }

    @Override
    public void deleteById(String identifier) {
        connectorRemoveUserRole.deleteById(identifier);
        log.info("User deleted by id");
    }

}
