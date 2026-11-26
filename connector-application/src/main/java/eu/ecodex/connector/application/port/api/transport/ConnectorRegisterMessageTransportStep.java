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
     * Registers the given transport status for the given message. A message has a single
     * transport step, so the registration depends on the step it already has:
     * <ul>
     *     <li>none: a new step is created, with one attempt;
     *     <li>{@code FAILED} or {@code READY_FOR_PULL}: a new attempt is recorded, with the given
     *     status;
     *     <li>{@code SUBMITTED}: the attempt is under way, so its outcome is recorded without
     *     counting another attempt, and repeating {@code SUBMITTED} changes nothing;
     *     <li>{@code DELIVERED} or {@code PULLED}: the step is final and is left as it is.
     * </ul>
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
