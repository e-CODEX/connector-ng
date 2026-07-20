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

import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;

/**
 * Defines a use case for retrieving statistical data regarding messages processed by a connector
 * over a specific time range. This interface allows querying aggregated metrics for all, outbound,
 * and inbound messages within a given period.
 */
public interface ConnectorRetrieveMessageStats {
    /**
     * Executes the retrieval of statistical data related to messages processed by a connector
     * within a specified time range and business domain. The method provides aggregated metrics for
     * all messages, as well as for outbound and inbound messages separately.
     *
     * @param from           The starting timestamp of the time range (inclusive) in ISO 8601
     *                       format.
     * @param to             The ending timestamp of the time range (exclusive) in ISO 8601 format.
     * @param businessDomain The unique identifier of the business domain for which the statistics
     *                       are to be retrieved.
     *
     * @return A {@code ConnectorMessageStats} object containing aggregated metrics for the
     *     specified time range and business domain.
     */
    ConnectorMessageStats execute(String from, String to, String businessDomain);
}
