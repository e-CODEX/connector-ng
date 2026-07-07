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
 * Represents statistical data regarding messages processed by a connector. This class provides
 * aggregated metrics for all messages (all), as well as separate metrics for outbound and inbound
 * messages.
 *
 * @param all      Statistical data for all messages processed, regardless of direction.
 * @param outbound Statistical data specifically for outbound messages.
 * @param inbound  Statistical data specifically for inbound messages.
 */
@Builder
public record ConnectorMessageStats(
        ConnectorMessageStatsItem all,
        ConnectorMessageStatsItem outbound,
        ConnectorMessageStatsItem inbound
) {
    /**
     * Creates an instance of {@code ConnectorMessageStats} where all statistical values for all,
     * outbound, and inbound metrics are initialized to zero.
     *
     * @return a new {@code ConnectorMessageStats} instance with all metrics set to zero.
     */
    public static ConnectorMessageStats ofZero() {
        return new ConnectorMessageStats(
                new ConnectorMessageStatsItem(0, 0, 0, 0),
                new ConnectorMessageStatsItem(0, 0, 0, 0),
                new ConnectorMessageStatsItem(0, 0, 0, 0)
        );
    }
}
