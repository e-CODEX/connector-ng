/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.stats.report;

import java.util.List;

/**
 * Represents message statistics for a single calendar month.
 *
 * @param month         the month of the year, from {@code 1} (January) to {@code 12} (December)
 * @param label         the display label for the month
 * @param totalInbound  the total number of inbound messages for the month
 * @param totalOutbound the total number of outbound messages for the month
 * @param total         the total number of messages for the month
 * @param reports       the message statistics grouped by party and service for the month
 */
public record MonthReport(
        int month,
        String label,
        long totalInbound,
        long totalOutbound,
        long total,
        List<MessageReportItem> reports
) {
}
