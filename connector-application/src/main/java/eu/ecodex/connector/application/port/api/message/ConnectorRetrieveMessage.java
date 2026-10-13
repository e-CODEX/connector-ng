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

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import jakarta.annotation.Nonnull;

/**
 * Use case for retrieving a connector business message by its identifier.
 */
public interface ConnectorRetrieveMessage {
    /**
     * Retrieves the {@link ConnectorBusinessMessage} identified by the given identifier.
     *
     * @param identifier identifier of the message to retrieve (must not be null)
     *
     * @return the {@link ConnectorBusinessMessage} associated with the given identifier
     */
    ConnectorBusinessMessage execute(@Nonnull String identifier);
}
