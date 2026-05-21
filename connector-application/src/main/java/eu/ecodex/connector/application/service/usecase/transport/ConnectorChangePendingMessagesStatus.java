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

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import jakarta.annotation.Nonnull;

/**
 * Service interface for changing the status of pending connector messages.
 */
public interface ConnectorChangePendingMessagesStatus {
    /**
     * Changes the status of pending connector messages for a specified backend system.
     *
     * @param backendName the name of the backend system for which the status of pending messages
     *                    needs to be updated. Must not be null.
     * @param status      the new {@link ConnectorMessageTransportStatus} to apply to the pending
     *                    connector messages. Must not be null.
     */
    void execute(@Nonnull String backendName, @Nonnull ConnectorMessageTransportStatus status);
}
