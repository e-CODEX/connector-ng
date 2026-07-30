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

import eu.ecodex.connector.application.exception.ConnectorUserRoleAlreadyExistsException;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;

/**
 * Defines a contract for registering and updating user roles within the Connector system.
 * Implementations of this interface handle the creation of new {@link ConnectorUserRole}
 * instances and the modification of existing ones.
 * <p>
 * The primary purpose of this interface is to manage user roles in the underlying
 * persistence mechanism, ensuring that each role is correctly registered or updated
 * based on the provided data.
 */
public interface ConnectorRegisterRole {
    /**
     * Registers a new user role in the Connector system.
     * This method persists the provided {@link ConnectorUserRole} instance
     * in the underlying data store and returns the registered instance.
     *
     * @param userRole the {@link ConnectorUserRole} object to be registered.
     *                 It must contain valid role information to be persisted.
     * @return the registered {@link ConnectorUserRole} instance, including any
     * additional fields populated during the registration process (e.g., identifier, timestamps).
     */
    ConnectorUserRole register(ConnectorUserRole userRole) throws
            ConnectorUserRoleAlreadyExistsException;

    /**
     * Updates an existing user role in the Connector system.
     * This method modifies the details of a {@link ConnectorUserRole} object
     * identified by the given unique identifier. The updated user role information
     * is persisted in the underlying data store, and the resulting modified instance
     * is returned.
     *
     * @param id       the unique identifier of the {@link ConnectorUserRole} to be updated.
     *                 It must correspond to an existing user role in the system.
     * @param userRole the {@link ConnectorUserRole} object containing the updated role
     *                 information. It must include valid details for the update process.
     * @return the updated {@link ConnectorUserRole} instance, reflecting all modifications
     * made during the update process.
     */
    ConnectorUserRole update(String id, ConnectorUserRole userRole);
}
