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

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReport;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a summary of message reports for a connector, including data grouped by services,
 * parties, months, and years.
 *
 * @param services a list of distinct and sorted service identifiers involved in the message
 *                 reports
 * @param parties  a list of distinct and sorted party identifiers involved in the message reports
 * @param months   a list of distinct and sorted {@code MonthKey} objects representing months where
 *                 message reports are available
 * @param years    a list of {@code YearReport} objects containing detailed yearly message report
 *                 data
 */
public record ConnectorMessageReportSummary(
        List<String> services,
        List<String> parties,
        List<MonthKey> months,
        List<YearReportSummary> years
) {
    private static final ConnectorMessageDirection INBOUND =
            ConnectorMessageDirection.GATEWAY_TO_BACKEND;
    private static final ConnectorMessageDirection OUTBOUND =
            ConnectorMessageDirection.BACKEND_TO_GATEWAY;

    /**
     * Constructs a new {@code ConnectorMessageReportSummary} instance based on the provided list of
     * {@code ConnectorMessageReport} objects. This method processes the input reports to extract
     * distinct values for services, parties, and month keys, and computes aggregated yearly
     * reports.
     *
     * @param reports the list of {@code ConnectorMessageReport} objects used to generate the
     *                summary. Each report contains statistical data about connector messages
     *                including service, party, year, month, direction, and message totals.
     *
     * @return a new {@code ConnectorMessageReportSummary} instance containing aggregated and sorted
     *         statistics derived from the provided reports.
     */
    public static ConnectorMessageReportSummary of(List<ConnectorMessageReport> reports) {
        return new ConnectorMessageReportSummary(
                distinctSorted(reports, ConnectorMessageReport::service),
                distinctSorted(reports, ConnectorMessageReport::party),
                toMonths(reports),
                toYearReports(reports)
        );
    }

    private static List<String> distinctSorted(
            List<ConnectorMessageReport> reports,
            Function<ConnectorMessageReport, String> extractor) {
        return reports.stream().map(extractor).distinct().sorted().toList();
    }

    private static List<MonthKey> toMonths(List<ConnectorMessageReport> reports) {
        return reports.stream()
                      .map(r -> new MonthKey(r.year(), r.month()))
                      .distinct()
                      .sorted(Comparator.comparing(MonthKey::year).thenComparing(MonthKey::month))
                      .toList();
    }

    private static List<YearReportSummary> toYearReports(List<ConnectorMessageReport> reports) {
        return reports.stream()
                      .collect(Collectors.groupingBy(
                              ConnectorMessageReport::year,
                              TreeMap::new,
                              Collectors.toList()
                      ))
                      .entrySet()
                      .stream()
                      .map(ConnectorMessageReportSummary::toYearReport)
                      .toList();
    }

    private static YearReportSummary toYearReport(
            Map.Entry<Integer, List<ConnectorMessageReport>> yearEntry) {
        var months = yearEntry.getValue()
                              .stream()
                              .collect(Collectors.groupingBy(
                                      ConnectorMessageReport::month,
                                      TreeMap::new,
                                      Collectors.toList()
                              ))
                              .entrySet()
                              .stream()
                              .map(ConnectorMessageReportSummary::toMonthReport)
                              .toList();

        return new YearReportSummary(yearEntry.getKey(), months);
    }

    private static MonthReportSummary toMonthReport(
            Map.Entry<Integer, List<ConnectorMessageReport>> monthEntry) {

        int month = monthEntry.getKey();
        var monthReports = monthEntry.getValue();

        long totalInbound = sumByDirection(monthReports, INBOUND);
        long totalOutbound = sumByDirection(monthReports, OUTBOUND);

        var items = monthReports
                .stream()
                .collect(Collectors.groupingBy(
                        ConnectorMessageReport::party,
                        TreeMap::new,
                        Collectors.groupingBy(
                                ConnectorMessageReport::service,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .flatMap(partyEntry -> partyEntry.getValue()
                                                 .entrySet()
                                                 .stream()
                                                 .map(serviceEntry ->
                                                              toMessageReportItem(
                                                                      partyEntry.getKey(),
                                                                      serviceEntry.getKey(),
                                                                      serviceEntry.getValue()
                                                              )))
                .toList();

        return new MonthReportSummary(
                month,
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                totalInbound,
                totalOutbound,
                totalInbound + totalOutbound,
                items
        );
    }

    private static MessageReportSummaryItem toMessageReportItem(
            String party,
            String service,
            List<ConnectorMessageReport> reports) {
        long inbound = sumByDirection(reports, INBOUND);
        long outbound = sumByDirection(reports, OUTBOUND);
        return new MessageReportSummaryItem(party, service, inbound, outbound, inbound + outbound);
    }

    private static long sumByDirection(
            List<ConnectorMessageReport> reports,
            ConnectorMessageDirection direction) {
        return reports.stream()
                      .filter(r -> r.direction() == direction)
                      .mapToLong(ConnectorMessageReport::total)
                      .sum();
    }
}
