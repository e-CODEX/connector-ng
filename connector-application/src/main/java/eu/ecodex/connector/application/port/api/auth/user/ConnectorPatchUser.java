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
import eu.ecodex.connector.application.exception.ConnectorUserIdentifierMismatchException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import org.jspecify.annotations.NonNull;

/**
 * Interface for managing the patch of {@link ConnectorUser} entities in the
 * system.
 * Provides methods for partially updating user information.
 */
public interface ConnectorPatchUser {
    /**
     * Partially updates an existing {@link ConnectorUser} in the system based on the provided
     * identifier.
     * If the identifier and user information do not match, an exception is thrown. Additionally,
     * exceptions are thrown if the user already exists or if the user corresponding to the
     * identifier does not exist.
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to be updated.
     * @param user       the {@link ConnectorUser} object containing the updated details.
     *
     * @return the updated {@link ConnectorUser} after applying the changes.
     *
     * @throws ConnectorUserAlreadyExistsException      if a user with the same details already
     *                                                  exists in
     *                                                  the system.
     * @throws ConnectorUserNotFoundException           if no user is found for the provided
     *                                                  identifier.
     * @throws ConnectorUserIdentifierMismatchException if the identifier does not match the user's
     *                                                  identifier.
     */
    ConnectorUser execute(@NonNull String identifier, @NonNull ConnectorUser user);
}
