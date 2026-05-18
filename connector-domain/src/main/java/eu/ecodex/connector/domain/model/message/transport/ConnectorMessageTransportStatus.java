/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.transport;

import lombok.Getter;

/**
 * Represents the different states that a connector message transport process can have.
 *
 * <p>This enum defines various states, each associated with a priority value
 * that determines its importance or execution order. The states are typically used to monitor and
 * manage the lifecycle of a transport operation in the context of the connector system.
 *
 * <ul>
 *     <li>ACCEPTED: Indicates that the transport process has been accepted and is ready
 *     to proceed.</li>
 *     <li>FAILED: Indicates that the transport process has encountered an error or
 *     failure and cannot proceed further.</li>
 *     <li>PENDING_DOWNLOADED: Indicates that the transport process is in a pending
 *     state and is marked as downloaded but not processed yet.</li>
 *     <li>PENDING: Indicates that the transport process is awaiting processing
 *     or a specific action.</li>
 * </ul>
 *
 * <p>Each state is assigned a priority value, encapsulated within the state itself, to
 * represent the relative importance or operational precedence of that state.
 */
@Getter
public enum ConnectorMessageTransportStatus {
    SUBMITTED(10),
    FAILED(10),
    DOWNLOADED(2),
    PENDING(1);

    final int priority;

    ConnectorMessageTransportStatus(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("{name=%s, priority=%s}", name(), priority);
    }
}
