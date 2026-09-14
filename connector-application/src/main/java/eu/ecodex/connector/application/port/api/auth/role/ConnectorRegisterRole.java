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
import org.jspecify.annotations.NonNull;

/**
 * Defines a contract for registering user roles within the Connector system.
 * Implementations of this interface handle the creation of new {@link ConnectorRole}
 * instances.
 *
 */
public interface ConnectorRegisterRole {
    /**
     * Executes the registration of a new user role in the Connector system.
     *
     * @param userRole The {@link ConnectorRole} instance representing the role to be registered.
     *                 It must not be null.
     *
     * @return The registered {@link ConnectorRole} instance, including any system-assigned
     *     metadata, such as timestamps or unique identifiers.
     *
     * @throws ConnectorRoleAlreadyExistsException If a role with the same attributes already exists
     *                                             in the Connector system.
     */
    ConnectorRole execute(@NonNull ConnectorRole userRole);
}
