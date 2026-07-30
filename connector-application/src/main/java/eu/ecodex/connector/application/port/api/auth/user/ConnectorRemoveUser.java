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
 * Interface for removing {@link ConnectorUser} entities from the system.
 * <p>
 * Defines methods to delete a user by providing the {@link ConnectorUser} instance or by specifying its unique identifier.
 * Facilitates the removal of user entities while ensuring type safety and consistency in handling user operations.
 */
public interface ConnectorRemoveUser {
    /**
     * Deletes a {@link ConnectorUser} entity identified by its unique identifier.
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to be deleted; must not be null
     */
    void deleteById(String identifier) throws ConnectorUserNotFoundException;
}
