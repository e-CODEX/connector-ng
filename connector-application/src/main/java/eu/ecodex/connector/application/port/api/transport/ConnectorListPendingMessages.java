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
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for listing all pending connector messages.
 */
public interface ConnectorListPendingMessages {
    /**
     * Executes the operation to retrieve a list of pending connector messages. This method is
     * expected to interact with the underlying system or data source to fetch messages that are yet
     * to be submitted to the backend system.
     *
     * @param backendName the name of the backend system for which pending messages are requested.
     *
     * @return a list of {@link ConnectorBusinessMessage} representing the pending messages, or an
     *     empty list if there are no pending messages.
     */
    List<ConnectorBusinessMessage> execute(@Nonnull String backendName);
}
