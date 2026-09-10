/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.user;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserIdMismatchException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Interface for managing the patch of {@link ConnectorUser} entities in the
 * system.
 * Provides methods for partially updating user information.
 */
public interface ConnectorPatchUser {
    /**
     * Partially updates an existing {@link ConnectorUser} entity identified by the given ID with
     * the provided updates.
     * Only the non-null fields in the provided user object will be updated in the existing entity.
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to be patched; must not
     *                   be null
     * @param user       the {@link ConnectorUser} object containing the fields to update; must not
     *                   be null
     *
     * @return the updated {@link ConnectorUser} object after applying the specified changes
     */
    ConnectorUser execute(@Nonnull String identifier, @Nonnull ConnectorUser user)
        throws ConnectorUserAlreadyExistsException, ConnectorUserNotFoundException,
        ConnectorUserIdMismatchException;
}
