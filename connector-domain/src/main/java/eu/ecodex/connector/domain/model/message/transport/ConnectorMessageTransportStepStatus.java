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

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

/**
 * Represents the status of a specific step in the connector message transport process.
 *
 * <p>The status provides detailed information about the current state of the transport step,
 * when this state was recorded, and any additional details or context relevant to the step's
 * status.
 *
 * @param status    The current status of the transport step, represented as an enum value of
 *                  {@link ConnectorMessageTransportStatus}.
 * @param createdAt The timestamp indicating when the current status of the transport step was
 *                  recorded.
 */
@Builder
public record ConnectorMessageTransportStepStatus(
        ConnectorMessageTransportStatus status,
        Instant createdAt
) implements Serializable {
}
