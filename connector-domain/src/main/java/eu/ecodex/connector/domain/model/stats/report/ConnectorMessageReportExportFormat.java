/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats.report;

import lombok.Getter;

/**
 * Represents the supported export formats for connector message reports.
 */
@Getter
public enum ConnectorMessageReportExportFormat {
    CSV("text/csv", "csv"),
    JSON("application/json", "json"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

    private final String contentType;
    private final String extension;


    ConnectorMessageReportExportFormat(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }
}
