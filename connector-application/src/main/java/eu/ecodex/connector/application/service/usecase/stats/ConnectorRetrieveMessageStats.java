/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.stats;

import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;

/**
 * Defines a use case for retrieving statistical data regarding messages processed by a connector
 * over a specific time range. This interface allows querying aggregated metrics for all, outbound,
 * and inbound messages within a given period.
 */
public interface ConnectorRetrieveMessageStats {
    ConnectorMessageStats execute(String from, String to);
}
