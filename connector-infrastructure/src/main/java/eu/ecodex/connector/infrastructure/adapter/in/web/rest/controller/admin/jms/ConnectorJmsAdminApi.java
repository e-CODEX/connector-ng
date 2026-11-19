/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller.admin.jms;

import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface defining the administrative API for managing JMS queues within the connector system.
 */
@Tag(
    name = "JmsAdmin",
    description = "API for managing JMS queues within the connector system for administrative"
        + " purposes."
)
@RequestMapping(value = "/api/v1/admin/jms/queues")
public interface ConnectorJmsAdminApi {
    @GetMapping("/stats")
    @Operation(summary = "List JMS Queues statistics")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    List<ConnectorQueueStats> retrieveQueuesStats();

    @GetMapping("/messages")
    @Operation(
        summary = "List the messages of a queue or of its dead letter queue",
        description = "Browses, without consuming them, the pending messages of a monitored"
            + " queue, or its dead letter queue (DLQ) when `dlq` is true, oldest first. Only"
            + " message headers and properties are returned, never the body."
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "The queue is not monitored")
    ConnectorPageResult<ConnectorQueueMessage> listQueueMessages(
        @RequestParam(name = "queue") String queue,
        @RequestParam(name = "dlq", defaultValue = "false") boolean dlq,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    );
}
