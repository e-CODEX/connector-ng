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

import eu.ecodex.connector.application.exception.user.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.user.ConnectorUserIdentifierMismatchException;
import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;


/**
 * Use case for fully updating an existing user of the connector system.
 */
public interface ConnectorUpdateUser {
    /**
     * Updates the {@link ConnectorUser} identified by the given identifier with the information
     * carried by the given user object.
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to update (must not be
     *                   null)
     * @param user       the {@link ConnectorUser} containing the updated information (must not be
     *                   null)
     *
     * @return the updated {@link ConnectorUser} after the changes have been applied
     *
     * @throws ConnectorUserAlreadyExistsException      if the update would give the user details
     *                                                  (for example a username) that are already
     *                                                  used by another user
     * @throws ConnectorUserNotFoundException           if no user is found for the given
     *                                                  identifier
     * @throws ConnectorUserIdentifierMismatchException if the given identifier does not match the
     *                                                  identifier of the given user
     */
    ConnectorUser execute(@Nonnull String identifier, @Nonnull ConnectorUser user);
}
