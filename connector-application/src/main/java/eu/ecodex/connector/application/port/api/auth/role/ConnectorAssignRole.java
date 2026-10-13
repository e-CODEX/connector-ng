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

import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Use case for assigning a role to a user of the connector system.
 */
public interface ConnectorAssignRole {
    /**
     * Assigns the role with the given name to the user identified by the given identifier.
     *
     * @param userIdentifier the unique identifier of the user (must not be null)
     * @param roleName       the name of the role to assign to the user (must not be null)
     *
     * @return the updated {@link ConnectorUser} after the role assignment
     *
     * @throws ConnectorUserNotFoundException if no user matches the given identifier
     */
    ConnectorUser execute(@Nonnull String userIdentifier, @Nonnull String roleName);
}
