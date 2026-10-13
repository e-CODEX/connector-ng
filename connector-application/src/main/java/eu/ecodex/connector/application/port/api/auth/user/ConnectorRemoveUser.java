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

import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Use case for removing a user from the connector system.
 */
public interface ConnectorRemoveUser {
    /**
     * Deletes the {@link ConnectorUser} identified by the given unique identifier.
     *
     * @param userIdentifier the unique identifier of the {@link ConnectorUser} to delete (must not
     *                       be null)
     *
     * @throws ConnectorUserNotFoundException if no user matches the given identifier
     */
    void execute(@Nonnull String userIdentifier);
}
