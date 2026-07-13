/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats.report.summary;

/**
 * Represents message statistics for a specific party and service combination.
 *
 * @param party    the party associated with the message counts
 * @param service  the service associated with the message counts
 * @param inbound  the number of inbound messages
 * @param outbound the number of outbound messages
 * @param total    the total number of messages
 */
public record MessageReportSummaryItem(
        String party,
        String service,
        long inbound,
        long outbound,
        long total
) {
}
