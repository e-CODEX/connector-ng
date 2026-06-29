/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.transport;

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;

/**
 * Service interface for retrieving transport steps for a specific connector message.
 */
public interface ConnectorRetrieveTransportStep {
    /**
     * Executes the retrieval of a transport step for a specific connector message identified by its
     * unique message identifier. This method is responsible for obtaining the transport step
     * details for the provided message identifier, allowing further processing or status tracking
     * of the transport step.
     *
     * @param messageIdentifier the unique identifier of the connector message for which the
     *                          transport step is to be retrieved. Must not be null.
     *
     * @return a {@link ConnectorMessageTransportStep} instance representing the transport step
     *         associated with the specified message identifier.
     */
    ConnectorMessageTransportStep execute(@Nonnull String messageIdentifier);
}
