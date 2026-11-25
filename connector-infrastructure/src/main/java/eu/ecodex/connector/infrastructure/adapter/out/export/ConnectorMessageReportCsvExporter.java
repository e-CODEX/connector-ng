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

import com.opencsv.CSVWriter;
import eu.ecodex.connector.application.port.spi.ConnectorMessageReportExporter;
import eu.ecodex.connector.domain.model.message.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.message.stats.report.summary.ConnectorMessageReportSummary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;

/**
 * Exports the connector message report as CSV.
 */
public class ConnectorMessageReportCsvExporter implements ConnectorMessageReportExporter {
    private static final String FORMULA_PREFIXES = "=+-@\t\r";

    private static final String[] HEADERS = {
        "Year",
        "Month",
        "Party",
        "Service",
        "Inbound",
        "Outbound",
        "Total"
    };

    @Override
    public ConnectorMessageReportExportFormat getFormat() {
        return ConnectorMessageReportExportFormat.CSV;
    }

    @Override
    public byte[] export(@NonNull ConnectorMessageReportSummary summary) {
        return write(summary);
    }

    private byte[] write(ConnectorMessageReportSummary summary) {
        var out = new ByteArrayOutputStream();
        try (var streamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             var writer = new CSVWriter(streamWriter)) {

            writer.writeNext(HEADERS.clone());

            for (var year : summary.years()) {
                for (var month : year.months()) {
                    for (var report : month.reports()) {
                        writer.writeNext(new String[]{
                            String.valueOf(year.year()),
                            neutralize(month.label()),
                            neutralize(report.party()),
                            neutralize(report.service()),
                            String.valueOf(report.inbound()),
                            String.valueOf(report.outbound()),
                            String.valueOf(report.total())
                        });
                    }
                }
            }
        } catch (IOException e) {
            throw new ConnectorMessageReportExportException(
                "Failed to export connector message report as CSV", e
            );
        }
        return out.toByteArray();
    }

    /**
     * Prevents a text cell from being run as a formula when the file is opened in a spreadsheet: a
     * value starting with one of the characters spreadsheets treat as the start of a formula is
     * prefixed with a single quote, which makes them read it as text. The party and service names
     * come from the exchanged messages, so they are not trusted.
     */
    private String neutralize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return FORMULA_PREFIXES.indexOf(value.charAt(0)) >= 0 ? "'" + value : value;
    }
}
