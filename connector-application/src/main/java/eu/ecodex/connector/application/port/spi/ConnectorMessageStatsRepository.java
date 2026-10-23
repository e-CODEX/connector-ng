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

import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import eu.ecodex.connector.domain.model.stats.report.ConnectorMessageReport;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for retrieving statistics and reports about connector messages.
 */
public interface ConnectorMessageStatsRepository {
    /**
     * Retrieves the statistics of the connector messages within the given period, optionally
     * restricted to a business domain.
     *
     * @param dateFilter     the {@link ConnectorDateRangeFilter} defining the period (must not be
     *                       null); the bounds are inclusive
     * @param businessDomain the identifier of the business domain to restrict the statistics to;
     *                       all business domains are covered if {@code null} or empty
     *
     * @return the {@link ConnectorMessageStats} of the period
     */
    ConnectorMessageStats findAll(
        @Nonnull ConnectorDateRangeFilter dateFilter,
        String businessDomain);

    /**
     * Computes the message report for the given period, optionally restricted to a business
     * domain.
     *
     * @param dateFilter     the {@link ConnectorDateRangeFilter} defining the period (must not be
     *                       null); the bounds are inclusive
     * @param businessDomain the identifier of the business domain to restrict the report to; all
     *                       business domains are covered if {@code null} or empty
     *
     * @return the {@link ConnectorMessageReport} entries computed for the period (for example one
     *     per day, direction or status); never {@code null}, and empty if no messages match
     */
    List<ConnectorMessageReport> computeReport(
        @Nonnull ConnectorDateRangeFilter dateFilter,
        String businessDomain);
}
