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

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Defines a contract for unassigning user roles within the Connector system.
 */
public interface ConnectorUnassignRole {
    /**
     * Removes a role from a user in the Connector system.
     *
     * @param identifier The unique identifier of the user.
     * @param role       The role to be removed from the user.
     *
     * @return The updated {@link ConnectorUser} instance after the role removal.
     *
     * @throws ConnectorRoleNotFoundException If the specified role is not found.
     * @throws ConnectorUserNotFoundException If the specified user is not found.
     */
    ConnectorUser execute(@Nonnull String identifier, @Nonnull String role)
        throws ConnectorRoleNotFoundException, ConnectorUserNotFoundException;
}
