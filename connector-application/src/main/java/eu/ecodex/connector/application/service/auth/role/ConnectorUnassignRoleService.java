/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.role;

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRoleByName;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorUnassignRole;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for unassigning roles to users within the Connector system.
 * This class is responsible for creating new user roles or updating existing ones based on the
 * provided identifier and role name. It serves as the bridge between the application's business
 * logic
 * and the persistence layer through the {@link ConnectorRoleRepository}.
 *
 * <p>Responsibilities:
 * - Handles the creation and registration of new user roles in the data source.
 * - Updates existing user role assignments when applicable.
 * - Ensures that the provided role and user identifiers are valid, throwing exceptions
 * if the required entities are not found.
 *
 * <p>Exceptions:
 * - {@link ConnectorRoleNotFoundException}: Thrown if the specified role does not exist.
 * - {@link ConnectorUserNotFoundException}: Thrown if the specified user identifier does not
 * exist.
 */
@Slf4j
@Component
@Transactional
public class ConnectorUnassignRoleService implements ConnectorUnassignRole {
    private final ConnectorRetrieveRoleByName retrieveRoleByName;
    private final ConnectorUserRepository userRepository;

    public ConnectorUnassignRoleService(ConnectorRetrieveRoleByName retrieveRoleByName,
                                        ConnectorUserRepository userRepository) {
        this.retrieveRoleByName = retrieveRoleByName;
        this.userRepository = userRepository;
    }

    @Override
    public ConnectorUser execute(@NonNull String identifier, @NonNull String roleName)
        throws ConnectorRoleNotFoundException, ConnectorUserNotFoundException {
        var user = getUserByIdentifier(identifier);
        if (user.roles() == null) {
            return user;
        }
        var role = retrieveRoleByName.execute(roleName);
        var updated = user.removeRole(role);
        return (user.equals(updated)) ? user : userRepository.save(updated);
    }

    private ConnectorUser getUserByIdentifier(String identifier)
        throws ConnectorUserNotFoundException {
        return userRepository.findByUuid(identifier)
            .orElseThrow(() -> new ConnectorUserNotFoundException(identifier));
    }
}
