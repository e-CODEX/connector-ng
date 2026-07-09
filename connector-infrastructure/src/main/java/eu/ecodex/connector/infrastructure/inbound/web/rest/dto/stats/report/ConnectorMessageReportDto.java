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

import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageReport;
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
 * Data transfer object representing a connector message report aggregated for reporting purposes.
 *
 * <p>The report contains the distinct services, parties, and months present in the source data,
 * along with yearly report data grouped by year.
 *
 * @param services the distinct service names included in the report
 * @param parties  the distinct party names included in the report
 * @param months   the distinct month keys represented in the report
 * @param years    the yearly report data, ordered by ascending year
 */
public record ConnectorMessageReportDto(
        List<String> services,
        List<String> parties,
        List<MonthKey> months,
        List<YearReport> years
) {

    private static final ConnectorMessageDirection INBOUND =
            ConnectorMessageDirection.GATEWAY_TO_BACKEND;
    private static final ConnectorMessageDirection OUTBOUND =
            ConnectorMessageDirection.BACKEND_TO_GATEWAY;

    /**
     * Creates a {@code ConnectorMessageReportDto} from a collection of
     * {@link ConnectorMessageReport} instances.
     *
     * @param reports the source connector message reports
     *
     * @return a DTO containing the aggregated reporting data
     */
    public static ConnectorMessageReportDto of(List<ConnectorMessageReport> reports) {
        return new ConnectorMessageReportDto(
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

    private static List<YearReport> toYearReports(List<ConnectorMessageReport> reports) {
        return reports.stream()
                      .collect(Collectors.groupingBy(
                              ConnectorMessageReport::year,
                              TreeMap::new,
                              Collectors.toList()
                      ))
                      .entrySet()
                      .stream()
                      .map(ConnectorMessageReportDto::toYearReport)
                      .toList();
    }

    private static YearReport toYearReport(
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
                              .map(ConnectorMessageReportDto::toMonthReport)
                              .toList();

        return new YearReport(yearEntry.getKey(), months);
    }

    private static MonthReport toMonthReport(
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

        return new MonthReport(
                month,
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                totalInbound,
                totalOutbound,
                totalInbound + totalOutbound,
                items
        );
    }

    private static MessageReportItem toMessageReportItem(
            String party,
            String service,
            List<ConnectorMessageReport> reports) {
        long inbound = sumByDirection(reports, INBOUND);
        long outbound = sumByDirection(reports, OUTBOUND);
        return new MessageReportItem(party, service, inbound, outbound, inbound + outbound);
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
