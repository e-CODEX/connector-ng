/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport;

import java.time.Instant;
import lombok.Builder;

/**
 * Data Transfer Object representing the status of a step in the transport process of a connector
 * message.
 *
 * @param status    The current status of the transport step, represented as a string.
 * @param createdAt The timestamp indicating when the status of the transport step was recorded.
 */
@Builder
public record ConnectorMessageTransportStepStatusDto(
        String status,
        Instant createdAt
) {
}
