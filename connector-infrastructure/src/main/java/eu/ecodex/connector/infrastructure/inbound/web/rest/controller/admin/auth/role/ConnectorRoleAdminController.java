/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.role;

import eu.ecodex.connector.application.port.api.auth.role.ConnectorEditRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorListRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRemoveRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRoleByIdentifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for managing user roles within the connector system. Provides
 * functionality to create, update, retrieve, list, and delete user roles. Implements the API
 * defined in {@link ConnectorRoleAdminApi}. Uses service-level components to execute business logic
 * related to user roles.
 */
@Slf4j
@RestController
public class ConnectorRoleAdminController implements ConnectorRoleAdminApi {
    private final ConnectorRegisterRole connectorRegisterRole;
    private final ConnectorRetrieveRoleByIdentifier connectorRetrieveRole;
    private final ConnectorEditRole connectorEditRole;
    private final ConnectorRemoveRole connectorRemoveRole;
    private final ConnectorListRole connectorListRole;

    /**
     * Creates a new instance of {@link ConnectorRoleAdminController}.
     *
     * @param connectorRegisterRole The role registration service.
     * @param connectorRetrieveRole The role retrieval service.
     * @param connectorEditRole     The role update service.
     * @param connectorRemoveRole   The role removal service.
     * @param connectorListRole     The role listing service.
     */
    public ConnectorRoleAdminController(
        ConnectorRegisterRole connectorRegisterRole,
        ConnectorRetrieveRoleByIdentifier connectorRetrieveRole,
        ConnectorEditRole connectorEditRole,
        ConnectorRemoveRole connectorRemoveRole,
        ConnectorListRole connectorListRole) {
        this.connectorRegisterRole = connectorRegisterRole;
        this.connectorRetrieveRole = connectorRetrieveRole;
        this.connectorEditRole = connectorEditRole;
        this.connectorRemoveRole = connectorRemoveRole;
        this.connectorListRole = connectorListRole;
    }

    @Override
    public ConnectorRoleDto registerRole(@NonNull ConnectorRoleDto usrRoleDto) {
        log.debug("Registering new user role");
        var registered =
            connectorRegisterRole.execute(ConnectorRoleDto.toDomain(usrRoleDto));
        log.debug("New user registered");
        return ConnectorRoleDto.from(registered);
    }

    @Override
    public ConnectorRoleDto editRole(@NonNull String identifier,
                                     @Valid ConnectorRoleDto userRoleDto) {
        log.debug("Updating existing user");
        var updated =
            connectorEditRole.execute(identifier, ConnectorRoleDto.toDomain(userRoleDto));
        log.debug("Existing user updated");
        return ConnectorRoleDto.from(updated);
    }

    @Override
    public ConnectorRoleDto retrieveRole(@NonNull String identifier) {
        var found = connectorRetrieveRole.execute(identifier);
        return ConnectorRoleDto.from(found);
    }

    @Override
    public List<ConnectorRoleDto> listRoles() {
        return connectorListRole.execute().stream()
            .map(ConnectorRoleDto::from)
            .toList();
    }

    @Override
    public Void removeRole(@NonNull String identifier) {
        connectorRemoveRole.execute(identifier);
        log.debug("User deleted by id");

        return null;
    }
}
