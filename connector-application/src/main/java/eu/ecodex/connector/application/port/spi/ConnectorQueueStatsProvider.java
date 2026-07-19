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

import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import java.util.List;

/**
 * Provides a contract for retrieving statistics related to connector queues.
 */
public interface ConnectorQueueStatsProvider {
    /**
     * Retrieves statistical information for all connector queues.
     *
     * @return a list of {@link ConnectorQueueStats} objects, each containing the name of the queue,
     *     the count of pending messages, and the count of messages in the dead letter queue
     */
    List<ConnectorQueueStats> getAllStats();
}
