/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.stats;

import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.application.port.spi.ConnectorMessageStatsRepository;
import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorRetrieveMessageReport}.
 */
@Service
public class ConnectorRetrieveMessageReportService implements ConnectorRetrieveMessageReport {
    private final ConnectorMessageStatsRepository statsRepository;

    public ConnectorRetrieveMessageReportService(ConnectorMessageStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public ConnectorMessageReportSummary execute(
        @NonNull ConnectorDateRangeFilter dateFilter,
        String businessDomain) {
        var computedReport = statsRepository.computeReport(dateFilter, businessDomain);

        return ConnectorMessageReportSummary.of(computedReport);
    }
}
