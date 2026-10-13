/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi;

import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReportExportFormat;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import jakarta.annotation.Nonnull;

/**
 * Exporter (port) for rendering a connector message report summary in a given file format.
 *
 * <p>Each implementation handles one {@link ConnectorMessageReportExportFormat}, for example XML,
 * XLSX or CSV, which it exposes through {@link #getFormat()}.
 */
public interface ConnectorMessageReportExporter {
    /**
     * Returns the export format handled by this exporter.
     *
     * @return the {@link ConnectorMessageReportExportFormat} produced by {@link #export}
     */
    ConnectorMessageReportExportFormat getFormat();

    /**
     * Exports the given report summary in the format returned by {@link #getFormat()}.
     *
     * @param summary the {@link ConnectorMessageReportSummary} to export (must not be null)
     *
     * @return the content of the exported report as a byte array
     */
    byte[] export(@Nonnull ConnectorMessageReportSummary summary);
}
