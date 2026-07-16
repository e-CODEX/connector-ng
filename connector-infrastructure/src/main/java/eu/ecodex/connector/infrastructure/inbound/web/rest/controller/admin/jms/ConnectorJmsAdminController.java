/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.jms;

import eu.ecodex.connector.application.service.usecase.stats.ConnectorRetrieveQueuesStats;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing JMS queues within the connector system.
 */
@RestController
public class ConnectorJmsAdminController implements ConnectorJmsAdminApi {
    private final ConnectorRetrieveQueuesStats retrieveQueuesStatsService;

    public ConnectorJmsAdminController(ConnectorRetrieveQueuesStats retrieveQueuesStatsService) {
        this.retrieveQueuesStatsService = retrieveQueuesStatsService;
    }

    @Override
    public List<ConnectorQueueStats> retrieveQueuesStats() {
        return retrieveQueuesStatsService.execute();
    }
}
