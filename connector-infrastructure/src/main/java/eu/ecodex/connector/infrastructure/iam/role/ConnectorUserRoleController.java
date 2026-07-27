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

import eu.ecodex.connector.application.port.api.iam.role.ConnectorListUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRegisterUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRemoveUserRole;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRetrieveUserRole;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing connector's users.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRoleController implements ConnectorUserRoleApi {
    ConnectorRegisterUserRole connectorRegisterUserRole;
    ConnectorRetrieveUserRole connectorRetrieveUserRole;
    ConnectorRemoveUserRole connectorRemoveUserRole;
    ConnectorListUserRole connectorListUserRole;



    @Override
    public ConnectorUserRoleDto register(ConnectorUserRoleDto userDto) {
        log.info("Registering new user role");

        var registered = connectorRegisterUserRole.register(ConnectorUserRoleDto.toDomain(userDto));
        log.info("New user registered");
        return ConnectorUserRoleDto.from(registered);
    }

    @Override
    public ConnectorUserRoleDto update(Long id, ConnectorUserRoleDto userRoleDto) {
        log.info("Updating existing user");
        var updated = connectorRegisterUserRole.update(id, ConnectorUserRoleDto.toDomain(userRoleDto));
        log.info("Existing user updated");
        return ConnectorUserRoleDto.from(updated);
    }


    @Override
    public ConnectorUserRoleDto getById(Long identifier) {
        ConnectorUserRole byId = connectorRetrieveUserRole.getById(identifier);
        return ConnectorUserRoleDto.from(byId);
    }

    @Override
    public List<ConnectorUserRoleDto> getAll() {
        return connectorListUserRole.findAll().stream().map(ConnectorUserRoleDto::from).toList();
    }

    @Override
    public void delete(ConnectorUserRoleDto userDto) {
        connectorRemoveUserRole.delete(ConnectorUserRoleDto.toDomain(userDto));
        log.info("User deleted");
    }

    @Override
    public void deleteById(Long userIdentifier) {
        connectorRemoveUserRole.deleteById(userIdentifier);
        log.info("User deleted by id");
    }

}
