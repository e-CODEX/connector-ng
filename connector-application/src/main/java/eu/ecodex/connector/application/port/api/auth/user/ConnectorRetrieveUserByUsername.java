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

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;

/**
 * Service interface for retrieving {@link ConnectorUser} entities by different attributes.
 * Provides methods to retrieve users by their unique username.
 */
public interface ConnectorRetrieveUserByUsername {
    /**
     * Retrieves a {@link ConnectorUser} instance by its unique username.
     *
     * @param username the unique username of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the specified username
     *
     * @throws ConnectorUserNotFoundException if no user is found with the given username
     */
    ConnectorUser execute(String username) throws ConnectorUserNotFoundException;

    /**
     * Retrieves a {@link ConnectorUser} instance by its unique username and active status.
     *
     * @param username the unique username of the user to retrieve
     * @param active   the active status of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the specified username and active status
     *
     * @throws ConnectorUserNotFoundException if no user is found with the given username and active
     *                                        status
     */
    ConnectorUser execute(String username, boolean active)
        throws ConnectorUserNotFoundException;
}
