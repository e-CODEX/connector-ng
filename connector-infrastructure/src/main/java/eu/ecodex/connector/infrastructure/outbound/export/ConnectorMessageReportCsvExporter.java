/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.export;

import com.opencsv.CSVWriter;
import eu.ecodex.connector.application.port.spi.ConnectorMessageReportExporter;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import lombok.NonNull;

/**
 * Exports the connector message report as CSV.
 */
public class ConnectorMessageReportCsvExporter implements ConnectorMessageReportExporter {
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
                        writer.writeNext(row(
                            year.year(),
                            month.label(),
                            report.party(),
                            report.service(),
                            report.inbound(),
                            report.outbound(),
                            report.total()
                        ));
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

    private String[] row(Object... cells) {
        return Arrays.stream(cells)
                     .map(cell -> Objects.toString(cell, ""))
                     .toArray(String[]::new);
    }
}
