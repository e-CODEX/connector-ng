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

import eu.ecodex.connector.application.port.spi.ConnectorMessageReportExporter;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import org.springframework.stereotype.Component;

/**
 * Factory for creating {@link ConnectorMessageReportExporter} instances.
 */
@Component
public class ConnectorMessageReportExporterFactory {
    /**
     * Creates a {@link ConnectorMessageReportExporter} instance based on the specified export
     * format.
     *
     * @param format the desired export format for the message report; must be of type
     *               {@link ConnectorMessageReportExportFormat}
     *
     * @return an instance of {@link ConnectorMessageReportExporter} corresponding to the specified
     *     format
     *
     * @throws IllegalArgumentException if the provided format is unknown or unsupported
     */
    public ConnectorMessageReportExporter create(ConnectorMessageReportExportFormat format) {
        switch (format) {
            case CSV -> {
                return new ConnectorMessageReportCsvExporter();
            }
            case JSON -> {
                return new ConnectorMessageReportJsonExporter();
            }
            case XLSX -> {
                return new ConnectorMessageReportXlsxExporter();
            }
            default -> throw new IllegalArgumentException("Unknown message report exporter");
        }
    }
}
