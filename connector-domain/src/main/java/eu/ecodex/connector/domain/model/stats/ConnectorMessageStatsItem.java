/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats;

import lombok.Builder;

/**
 * Represents statistical data for message processing within a connector in a specific context. This
 * class encapsulates metrics such as the total number of messages processed, the number of
 * successfully delivered messages, and the number of rejected messages.
 *
 * @param total     The total number of messages processed.
 * @param delivered The number of messages successfully delivered.
 * @param rejected  The number of messages that were rejected.
 * @param pending   The number of messages that are pending processing.
 */
@Builder
public record ConnectorMessageStatsItem(
        long total,
        long delivered,
        long rejected,
        long pending
) {
}
