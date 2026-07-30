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

/**
 * Provides a contract for removing user roles in the Connector system.
 * Implementations of this interface are responsible for deleting user roles
 * either through direct object references or by their unique identifier.
 * <p>
 * This interface is primarily used to manage user roles in the underlying
 * persistence mechanism by removing unwanted or obsolete entries.
 */
public interface ConnectorRemoveRole {
    /**
     * Deletes a user role identified by the specified unique identifier.
     * This method removes an entry from the underlying persistence mechanism
     * based on the provided UUID. If no entry corresponds to the given identifier,
     * the delete operation has no effect.
     *
     * @param uuid the unique identifier of the user role to be deleted.
     *             It must correspond to an existing user role in the system.
     */
    void deleteById(String uuid) throws ConnectorUserRoleNotFoundException;
}
