/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.role;

import eu.ecodex.connector.application.exception.ConnectorUserRoleNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.Set;

/**
 * Defines a contract for retrieving user roles within the Connector system.
 * Implementations of this interface are responsible for fetching user role details
 * based on either a unique identifier or a username.
 * <p>
 * This interface is primarily used to locate and retrieve specific {@link ConnectorUserRole}
 * entities from the underlying persistence mechanism, allowing system components to work
 * with role information associated with connector users.
 */
public interface ConnectorRetrieveRole {
    /**
     * Retrieves a user role identified by the specified unique identifier.
     * This method fetches the {@link ConnectorUserRole} entity from the
     * underlying data source using the provided identifier.
     *
     * @param identifier the unique identifier of the user role to be retrieved.
     *                   It must correspond to an existing user role in the system.
     * @return the {@link ConnectorUserRole} instance matching the given identifier.
     * @throws ConnectorUserRoleNotFoundException if no user role is found for the specified identifier.
     */
    ConnectorUserRole getById(String identifier) throws ConnectorUserRoleNotFoundException;

    /**
     * Retrieves a user role identified by the specified name.
     * This method fetches the {@link ConnectorUserRole} entity from the
     * underlying data source using the provided role name.
     *
     * @param roleName the name of the user role to be retrieved.
     *                 It must correspond to an existing user role in the system.
     * @return the {@link ConnectorUserRole} instance matching the given role name.
     * @throws ConnectorUserRoleNotFoundException if no user role is found for the specified name.
     */
    ConnectorUserRole getByName(String roleName) throws ConnectorUserRoleNotFoundException;

    /**
     * Retrieves a set of user roles based on the specified username.
     * This method fetches all {@link ConnectorUserRole} entities that are
     * associated with the given username from the underlying persistence source.
     *
     * @param username the username for which the user roles need to be retrieved.
     *                 It must correspond to a valid user in the system.
     * @return a set of {@link ConnectorUserRole} associated with the specified username.
     * The returned set may be empty if no roles are found.
     */
    Set<ConnectorUserRole> findByNameIn(Set<String> username);
}
