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
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import lombok.NonNull;
import tools.jackson.databind.ObjectMapper;

/**
 * Exports the connector message report as JSON.
 */
public class ConnectorMessageReportJsonExporter implements ConnectorMessageReportExporter {
    private final ObjectMapper objectMapper;

    public ConnectorMessageReportJsonExporter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ConnectorMessageReportExportFormat getFormat() {
        return ConnectorMessageReportExportFormat.JSON;
    }

    @Override
    public byte[] export(@NonNull ConnectorMessageReportSummary summary) {
        return this.objectMapper.writeValueAsBytes(summary);
    }
}
