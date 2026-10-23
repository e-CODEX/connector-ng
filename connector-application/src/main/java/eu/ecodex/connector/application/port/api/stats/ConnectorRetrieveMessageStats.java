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

import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
import jakarta.annotation.Nonnull;

/**
 * Use case for retrieving statistics about the connector messages over a period.
 */
public interface ConnectorRetrieveMessageStats {
    /**
     * Retrieves statistics about the connector messages processed within the given period,
     * optionally restricted to a business domain.
     *
     * @param dateFilter     the {@link ConnectorDateRangeFilter} defining the period (must not be
     *                       null); the bounds are inclusive
     * @param businessDomain identifier of the business domain to restrict the statistics to; may be
     *                       {@code null} or empty to cover all business domains
     *
     * @return a {@link ConnectorMessageStats} object holding the statistics of the period
     */
    ConnectorMessageStats execute(
        @Nonnull ConnectorDateRangeFilter dateFilter,
        String businessDomain);
}
