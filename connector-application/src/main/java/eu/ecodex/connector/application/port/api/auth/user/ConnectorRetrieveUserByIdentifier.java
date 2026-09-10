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
import jakarta.annotation.Nonnull;

/**
 * Service interface for retrieving {@link ConnectorUser} entities by identifier.
 * Provides methods to retrieve users by their unique identifier
 */
public interface ConnectorRetrieveUserByIdentifier {
    /**
     * Retrieves a {@link ConnectorUser} instance by its unique identifier.
     *
     * @param identifier the unique identifier of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the given identifier
     *
     * @throws ConnectorUserNotFoundException if no user is found with the specified identifier
     */
    ConnectorUser execute(@Nonnull String identifier) throws ConnectorUserNotFoundException;
}
