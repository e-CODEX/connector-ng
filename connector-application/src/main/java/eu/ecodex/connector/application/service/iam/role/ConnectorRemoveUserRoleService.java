/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRemoveUserRole;
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Service implementation for removing user roles associated with the Connector system.
 * This class provides the functionality to delete user roles based on their unique identifier (UUID),
 * interacting with the {@link ConnectorRoleRepository} to handle persistence operations.
 * <p>
 * Responsibilities:
 * - Validates the existence of a user role identified by a UUID before attempting deletion.
 * - Deletes a user role from the underlying data source using its unique identifier.
 * - Throws an exception if the specified user role is not found.
 * <p>
 * Exceptions:
 * - {@link ConnectorUserNotFoundException}: Thrown if no user role is found for the provided UUID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRemoveUserRoleService implements ConnectorRemoveUserRole {

    ConnectorRoleRepository repository;

    @Override
    public void deleteById(String uuid) {
        repository.deleteByUuid(uuid);
    }
}
