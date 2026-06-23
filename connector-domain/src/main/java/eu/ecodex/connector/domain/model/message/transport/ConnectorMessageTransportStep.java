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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;

/**
 * Represents a single transport step within the connector message delivery process.
 *
 * <p>This record encapsulates all relevant information about a specific step,
 * including its identifier, associated link partner, message details, status information, and
 * timestamps for auditing and state tracking purposes.
 *
 * <p>The transport step provides a structured representation of progress and
 * status changes in the message transport lifecycle.
 *
 * @param identifier                   A unique identifier representing this transport step.
 * @param remoteSystemIdentifier       The identifier of the remote system involved in this
 *                                     transport (Gateway EBMSID or backend system identifier).
 * @param transportedMessageIdentifier The identifier of the message being transported in this
 *                                     step.
 * @param numberOfAttempts             The count of attempts made to process this transport step.
 * @param linkPartnerName              The name of the link partner involved in this transport
 * @param transportedMessage           The {@link ConnectorMessage} instance that is being
 *                                     transported in this step.
 * @param statuses                     A priority queue of
 *                                     {@link ConnectorMessageTransportStepStatus} instances, each
 *                                     representing the status history of this step.
 * @param createdAt                    The timestamp indicating when this transport step was
 *                                     created.
 * @param updatedAt                    The timestamp representing the last time this transport step
 *                                     was updated.
 */
@Builder(toBuilder = true)
public record ConnectorMessageTransportStep(
        @Nonnull String identifier,
        String remoteSystemIdentifier,
        String transportedMessageIdentifier,
        int numberOfAttempts,
        String linkPartnerName,
        ConnectorMessage transportedMessage,
        ConnectorMessageTransportStatus status,
        Set<ConnectorMessageTransportStepStatus> statuses,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{identifier=%s, numberOfAttempts=%d, statues=%s, createdAt=%s, updatedAt=%s}",
                identifier, numberOfAttempts, statuses, createdAt, updatedAt
        );
    }
}
