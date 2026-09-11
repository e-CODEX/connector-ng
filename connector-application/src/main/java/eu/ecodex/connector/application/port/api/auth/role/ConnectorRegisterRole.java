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

import eu.ecodex.connector.application.exception.ConnectorRoleAlreadyExistsException;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import jakarta.annotation.Nonnull;

/**
 * Defines a contract for registering user roles within the Connector system.
 * Implementations of this interface handle the creation of new {@link ConnectorRole}
 * instances.
 *
 */
public interface ConnectorRegisterRole {
    /**
     * Registers a new user role in the Connector system.
     * This method persists the provided {@link ConnectorRole} instance
     * in the underlying data store and returns the registered instance.
     *
     * @param userRole the {@link ConnectorRole} object to be registered.
     *                 It must contain valid role information to be persisted.
     *
     * @return the registered {@link ConnectorRole} instance, including any
     *     additional fields populated during the registration process (e.g., identifier,
     *     timestamps).
     */
    ConnectorRole execute(@Nonnull ConnectorRole userRole) throws
        ConnectorRoleAlreadyExistsException;
}
