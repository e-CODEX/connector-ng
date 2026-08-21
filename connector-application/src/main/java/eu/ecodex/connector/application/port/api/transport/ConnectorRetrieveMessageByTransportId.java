/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.transport;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;

/**
 * Interface defining a contract for retrieving a {@link ConnectorMessage} using a specific
 * transport identifier.
 *
 * <p>Implementations of this interface are expected to provide a mechanism to locate and return a
 * {@link ConnectorMessage} instance based on the given transport ID.
 */
public interface ConnectorRetrieveMessageByTransportId {
    /**
     * Executes a retrieval process to obtain a {@link ConnectorMessage} associated with the
     * specified transport identifier.
     *
     * @param transportIdentifier the identifier used to locate the corresponding
     *                            {@link ConnectorMessage}
     *
     * @return the {@link ConnectorBusinessMessage} associated with the given transport identifier
     */
    ConnectorMessage execute(String transportIdentifier);
}
