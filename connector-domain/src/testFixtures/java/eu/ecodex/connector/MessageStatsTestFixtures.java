/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStatsItem;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class MessageStatsTestFixtures {
    public static ConnectorMessageStats createStats() {
        return ConnectorMessageStats.builder()
                                    .all(createAll())
                                    .outbound(createOutbound())
                                    .inbound(createInbound())
                                    .build();
    }

    private static ConnectorMessageStatsItem createAll() {
        return ConnectorMessageStatsItem.builder()
                                        .total(100)
                                        .delivered(90)
                                        .rejected(10)
                                        .pending(0)
                                        .build();
    }

    private static ConnectorMessageStatsItem createOutbound() {
        return ConnectorMessageStatsItem.builder()
                                        .total(80)
                                        .delivered(75)
                                        .rejected(5)
                                        .pending(0)
                                        .build();
    }

    private static ConnectorMessageStatsItem createInbound() {
        return ConnectorMessageStatsItem.builder()
                                        .total(20)
                                        .delivered(15)
                                        .rejected(5)
                                        .pending(0)
                                        .build();
    }
}
