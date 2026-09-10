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

import eu.ecodex.connector.application.port.api.auth.role.ConnectorListRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRemoveRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRoleByIdentifier;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorUpdateRole;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorRoleDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for managing user roles within the connector system.
 * Provides functionality to create, update, retrieve, list, and delete user roles.
 * Implements the API defined in {@link ConnectorRoleAdminApi}.
 * Uses service-level components to execute business logic related to user roles.
 */
@Slf4j
@RestController
public class ConnectorRoleAdminController implements ConnectorRoleAdminApi {
    private final ConnectorRegisterRole connectorRegisterRole;
    private final ConnectorRetrieveRoleByIdentifier connectorRetrieveRole;
    private final ConnectorUpdateRole connectorUpdateRole;
    private final ConnectorRemoveRole connectorRemoveRole;
    private final ConnectorListRole connectorListRole;

    /**
     * Creates a new instance of {@link ConnectorRoleAdminController}.
     *
     * @param connectorRegisterRole The role registration service.
     * @param connectorRetrieveRole The role retrieval service.
     * @param connectorUpdateRole   The role update service.
     * @param connectorRemoveRole   The role removal service.
     * @param connectorListRole     The role listing service.
     */
    public ConnectorRoleAdminController(ConnectorRegisterRole connectorRegisterRole,
                                        ConnectorRetrieveRoleByIdentifier connectorRetrieveRole,
                                        ConnectorUpdateRole connectorUpdateRole,
                                        ConnectorRemoveRole connectorRemoveRole,
                                        ConnectorListRole connectorListRole) {
        this.connectorRegisterRole = connectorRegisterRole;
        this.connectorRetrieveRole = connectorRetrieveRole;
        this.connectorUpdateRole = connectorUpdateRole;
        this.connectorRemoveRole = connectorRemoveRole;
        this.connectorListRole = connectorListRole;
    }

    @Override
    public ConnectorRoleDto register(@Valid ConnectorRoleDto usrRoleDto) {
        log.info("Registering new user role");
        var registered =
            connectorRegisterRole.execute(ConnectorRoleDto.toDomain(usrRoleDto));
        log.info("New user registered");
        return ConnectorRoleDto.from(registered);
    }

    @Override
    public ConnectorRoleDto update(@NonNull String identifier,
                                   @Valid ConnectorRoleDto userRoleDto) {
        log.info("Updating existing user");
        var updated =
            connectorUpdateRole.execute(identifier, ConnectorRoleDto.toDomain(userRoleDto));
        log.info("Existing user updated");
        return ConnectorRoleDto.from(updated);
    }

    @Override
    public ConnectorRoleDto getByIdentifier(@NonNull String identifier) {
        var found = connectorRetrieveRole.execute(identifier);
        return ConnectorRoleDto.from(found);
    }

    @Override
    public List<ConnectorRoleDto> getAll() {
        return connectorListRole.execute().stream()
            .map(ConnectorRoleDto::from)
            .toList();
    }

    @Override
    public void deleteByIdentifier(@NonNull String identifier) {
        connectorRemoveRole.execute(identifier);
        log.info("User deleted by id");
    }
}
