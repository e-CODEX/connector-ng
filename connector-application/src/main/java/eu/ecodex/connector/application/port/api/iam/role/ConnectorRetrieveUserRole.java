/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.iam.role;

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

/**
 * Defines a contract for retrieving user roles within the Connector system.
 * Implementations of this interface are responsible for fetching user role details
 * based on either a unique identifier or a username.
 * <p>
 * This interface is primarily used to locate and retrieve specific {@link ConnectorUserRole}
 * entities from the underlying persistence mechanism, allowing system components to work
 * with role information associated with connector users.
 */
public interface ConnectorRetrieveUserRole {
    /**
     * Retrieves a user role identified by the specified unique identifier.
     * This method fetches the {@link ConnectorUserRole} entity from the
     * underlying data source using the provided identifier.
     *
     * @param identifier the unique identifier of the user role to be retrieved.
     *                   It must correspond to an existing user role in the system.
     * @return the {@link ConnectorUserRole} instance matching the given identifier.
     * @throws NotFoundException if no user role is found for the specified identifier.
     */
    ConnectorUserRole getById(String identifier) throws NotFoundException;

    /**
     * Retrieves a user role identified by the specified name.
     * This method fetches the {@link ConnectorUserRole} entity from the
     * underlying data source using the provided role name.
     *
     * @param roleName the name of the user role to be retrieved.
     *                 It must correspond to an existing user role in the system.
     * @return the {@link ConnectorUserRole} instance matching the given role name.
     * @throws NotFoundException if no user role is found for the specified name.
     */
    ConnectorUserRole getByName(String roleName) throws NotFoundException;
}
