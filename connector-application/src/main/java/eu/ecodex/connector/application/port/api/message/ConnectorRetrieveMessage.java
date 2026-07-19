/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Represents a contract for retrieving a specific connector message using an identifier.
 * Implementations of this interface are responsible for processing the retrieval logic.
 */
public interface ConnectorRetrieveMessage {
    /**
     * Executes the retrieval of a connector message based on the provided identifier.
     *
     * @param identifier the unique identifier used to locate and retrieve the specific connector
     *                   message
     *
     * @return the retrieved ConnectorMessage associated with the given identifier
     */
    ConnectorMessage execute(@Nonnull String identifier);
}
