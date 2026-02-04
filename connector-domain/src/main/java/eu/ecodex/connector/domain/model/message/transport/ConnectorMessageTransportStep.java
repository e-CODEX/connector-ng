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

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.time.Instant;
import java.util.PriorityQueue;
import lombok.Builder;

/**
 * Represents a single transport step within the connector message delivery process.
 *
 * <p>This record encapsulates all relevant information about a specific step,
 * including its uuid, associated link partner, message details, status information, and
 * timestamps for auditing and state tracking purposes.
 *
 * <p>The transport step provides a structured representation of progress and
 * status changes in the message transport lifecycle.
 *
 * @param identifier              A unique uuid representing this transport step.
 * @param linkPartnerName         The name of the associated link partner for this transport step.
 * @param numberOfAttempts        The count of attempts made to process this transport step.
 * @param systemMessageIdentifier The uuid of the system message tied to this transport step.
 * @param remoteMessageIdentifier The uuid of the remote message tied to this transport step.
 * @param message                 The {@link ConnectorMessage} instance that is being transported in
 *                                this step.
 * @param statuses                A priority queue of {@link ConnectorMessageTransportStepStatus}
 *                                instances, each representing the status history of this step.
 * @param createdAt               The timestamp indicating when this transport step was created.
 * @param updatedAt               The timestamp representing the last time this transport step was
 *                                updated.
 * @param finalStateReachedAt     The timestamp indicating when the transport step reached its final
 *                                state, if applicable.
 */
@Builder
public record ConnectorMessageTransportStep(
        @Nonnull String identifier,
        ConnectorLinkPartnerName linkPartnerName,
        int numberOfAttempts,
        String systemMessageIdentifier,
        String remoteMessageIdentifier,
        ConnectorMessage message,
        PriorityQueue<ConnectorMessageTransportStepStatus> statuses,
        Instant createdAt,
        Instant updatedAt,
        Instant finalStateReachedAt
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{uuid=%s, linkPartnerName=%s, numberOfAttempts=%d, statues=%s, "
                + "systemMessageIdentifier=%s, remoteMessageIdentifier=%s, createdAt=%s, "
                + "updatedAt=%s, finalStateReachedAt=%s}",
                identifier, linkPartnerName, numberOfAttempts, statuses, systemMessageIdentifier,
                remoteMessageIdentifier, createdAt, updatedAt, finalStateReachedAt
        );
    }
}
