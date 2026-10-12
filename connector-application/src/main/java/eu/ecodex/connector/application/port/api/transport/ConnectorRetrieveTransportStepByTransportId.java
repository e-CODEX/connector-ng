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

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;

/**
 * Service interface for retrieving transport step by its unique identifier.
 */
public interface ConnectorRetrieveTransportStepByTransportId {
    /**
     * Executes the retrieval of a transport step by its unique identifier.
     *
     * @param transportIdentifier the unique identifier of the transport step. Must not be null.
     *
     * @return a {@link ConnectorMessageTransportStep} instance representing the transport step
     *     associated with the specified transport identifier.
     */
    ConnectorMessageTransportStep execute(@Nonnull String transportIdentifier);
}
