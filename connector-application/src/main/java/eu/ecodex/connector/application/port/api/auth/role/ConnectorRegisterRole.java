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

import eu.ecodex.connector.application.exception.role.ConnectorRoleAlreadyExistsException;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import jakarta.annotation.Nonnull;

/**
 * Use case for registering a new role in the connector system.
 */
public interface ConnectorRegisterRole {
    /**
     * Registers a new {@link ConnectorRole} in the system.
     *
     * @param userRole the {@link ConnectorRole} to register (must not be null)
     *
     * @return the registered {@link ConnectorRole}, including any system-assigned metadata such as
     *     timestamps or unique identifiers
     *
     * @throws ConnectorRoleAlreadyExistsException if a role with the same name already exists
     */
    ConnectorRole execute(@Nonnull ConnectorRole userRole);
}
