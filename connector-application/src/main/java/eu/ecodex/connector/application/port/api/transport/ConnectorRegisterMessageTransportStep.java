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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;

/**
 * Use case for registering a transport step of a connector message.
 *
 * <p>A transport step records the status of a message at a given point of its transport, for
 * example when it is submitted to or received from a link partner.
 */
public interface ConnectorRegisterMessageTransportStep {
    /**
     * Registers a new transport step for the given message with the given transport status.
     *
     * @param message the connector message the transport step relates to (must not be null)
     * @param status  the transport status to record for the message (must not be null)
     *
     * @return the registered {@link ConnectorMessageTransportStep}, as persisted
     */
    ConnectorMessageTransportStep execute(
        @Nonnull ConnectorMessage message,
        @Nonnull ConnectorMessageTransportStatus status);
}
