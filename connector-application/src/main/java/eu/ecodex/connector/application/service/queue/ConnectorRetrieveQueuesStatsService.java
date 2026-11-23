/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.queue;

import eu.ecodex.connector.application.port.api.queue.ConnectorRetrieveQueuesStats;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueStats;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorRetrieveQueuesStats}.
 */
@Service
public class ConnectorRetrieveQueuesStatsService implements ConnectorRetrieveQueuesStats {
    private final ConnectorQueueManagementProvider queueManagementProvider;

    public ConnectorRetrieveQueuesStatsService(
        ConnectorQueueManagementProvider queueManagementProvider) {
        this.queueManagementProvider = queueManagementProvider;
    }

    @Override
    public List<ConnectorQueueStats> execute() {
        return queueManagementProvider.getAllQueueStats();
    }
}
