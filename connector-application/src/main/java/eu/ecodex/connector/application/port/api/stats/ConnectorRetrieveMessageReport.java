/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.stats;

import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;

/**
 * Service interface for retrieving message reports.
 */
public interface ConnectorRetrieveMessageReport {
    /**
     * Executes a request to retrieve a summary of connector message reports for the specified date
     * range and business domain. The summary includes aggregated and sorted data about messages
     * grouped by services, parties, months, and years.
     *
     * @param from           the start date (inclusive) in ISO-8601 format (e.g., "yyyy-MM-dd")
     * @param to             the end date (inclusive) in ISO-8601 format (e.g., "yyyy-MM-dd")
     * @param businessDomain the identifier of the target business domain for which message reports
     *                       are retrieved
     *
     * @return a {@code ConnectorMessageReportSummary} instance containing the compiled summary of
     *     message reports for the specified parameters
     */
    ConnectorMessageReportSummary execute(String from, String to, String businessDomain);
}
