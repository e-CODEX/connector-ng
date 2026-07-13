/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.stats;

import eu.ecodex.connector.application.service.usecase.stats.ConnectorRetrieveMessageReport;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReport;
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import eu.ecodex.connector.domain.spi.ConnectorMessageStatsRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveMessageReport} service.
 */
@Service
public class ConnectorRetrieveMessageReportService implements ConnectorRetrieveMessageReport {
    private final ConnectorMessageStatsRepository statsRepository;

    public ConnectorRetrieveMessageReportService(ConnectorMessageStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    private static Instant parseInstant(String value) {
        return (value == null || value.isBlank()) ? null : Instant.parse(value);
    }

    @Override
    public ConnectorMessageReportSummary execute(String from, String to) {
        var computedReport = statsRepository.computeReport(parseInstant(from), parseInstant(to));

        return ConnectorMessageReportSummary.of(computedReport);
    }
}
