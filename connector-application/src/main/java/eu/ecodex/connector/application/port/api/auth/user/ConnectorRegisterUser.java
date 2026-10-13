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
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;


/**
 * Use case for registering a new user in the connector system.
 */
public interface ConnectorRegisterUser {
    /**
     * Registers a new {@link ConnectorUser} in the system.
     *
     * @param user the {@link ConnectorUser} to register; must not be null and must contain valid
     *             user information
     *
     * @return the registered {@link ConnectorUser} with updated metadata (for example the generated
     *     UUID and the creation timestamp)
     *
     * @throws ConnectorUserAlreadyExistsException if a user with the same identity (for example the
     *                                             same username) already exists
     */
    ConnectorUser execute(@Nonnull ConnectorUser user);
}
