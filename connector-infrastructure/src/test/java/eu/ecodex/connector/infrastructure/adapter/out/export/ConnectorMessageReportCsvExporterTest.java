/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.export;

import static org.assertj.core.api.Assertions.assertThat;

import com.opencsv.CSVReader;
import eu.ecodex.connector.domain.model.message.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.domain.model.message.stats.report.summary.MessageReportSummaryItem;
import eu.ecodex.connector.domain.model.message.stats.report.summary.MonthReportSummary;
import eu.ecodex.connector.domain.model.message.stats.report.summary.YearReportSummary;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ConnectorMessageReportCsvExporter")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorMessageReportCsvExporterTest {
    private final ConnectorMessageReportCsvExporter exporter =
        new ConnectorMessageReportCsvExporter();

    private List<String[]> exportRows(String party, String service) throws Exception {
        var item = new MessageReportSummaryItem(party, service, 3, 4, 7);
        var month = new MonthReportSummary(1, "January", 3, 4, 7, List.of(item));
        var summary = new ConnectorMessageReportSummary(
            Collections.singletonList(service), Collections.singletonList(party), List.of(),
            List.of(new YearReportSummary(2026, List.of(month)))
        );

        var csv = new String(exporter.export(summary), StandardCharsets.UTF_8);
        try (var reader = new CSVReader(new StringReader(csv))) {
            return reader.readAll();
        }
    }

    @Test
    void should_export_the_report_rows() throws Exception {
        var rows = exportRows("party-1", "service-1");

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0)).containsExactly(
            "Year", "Month", "Party", "Service", "Inbound", "Outbound", "Total"
        );
        assertThat(rows.get(1)).containsExactly(
            "2026", "January", "party-1", "service-1", "3", "4", "7"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "=HYPERLINK(\"http://evil.example\",\"x\")", "+1+1", "-2+3", "@SUM(A1)", "\tcmd"
    })
    void should_not_let_a_party_or_a_service_start_a_formula(String value) throws Exception {
        var row = exportRows(value, value).get(1);

        assertThat(row[2]).isEqualTo("'" + value);
        assertThat(row[3]).isEqualTo("'" + value);
    }

    @Test
    void should_not_let_a_value_starting_with_a_carriage_return_start_a_formula() {
        var item = new MessageReportSummaryItem("\r=1+1", "s", 1, 1, 2);
        var month = new MonthReportSummary(1, "January", 1, 1, 2, List.of(item));
        var summary = new ConnectorMessageReportSummary(
            List.of("s"), List.of("p"), List.of(),
            List.of(new YearReportSummary(2026, List.of(month)))
        );

        var csv = new String(exporter.export(summary), StandardCharsets.UTF_8);

        assertThat(csv).contains("\"'\r=1+1\"");
    }

    @Test
    void should_keep_the_numbers_as_they_are() throws Exception {
        var row = exportRows("p", "s").get(1);

        assertThat(row[0]).isEqualTo("2026");
        assertThat(row[4]).isEqualTo("3");
        assertThat(row[5]).isEqualTo("4");
        assertThat(row[6]).isEqualTo("7");
    }

    @Test
    void should_export_a_missing_name_as_an_empty_cell() throws Exception {
        var row = exportRows(null, "").get(1);

        assertThat(row[2]).isEmpty();
        assertThat(row[3]).isEmpty();
    }
}
