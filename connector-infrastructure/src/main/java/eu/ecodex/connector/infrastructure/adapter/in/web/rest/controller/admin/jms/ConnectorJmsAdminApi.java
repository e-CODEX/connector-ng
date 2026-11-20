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
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.queue.ConnectorReplayQueueDlqMessagesRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @PostMapping("/dlq/messages/{messageId}/replay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Replay a failed message",
        description = "Sends the given dead letter queue (DLQ) message of a connector queue back"
            + " to that queue, and removes it from the DLQ. The replay is at-least-once: if"
            + " removing the message from the DLQ fails, it is delivered again."
    )
    @ApiResponse(responseCode = "204", description = "The message was replayed")
    @ApiResponse(responseCode = "400", description = "The DLQ of the queue is not managed")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "The queue or the message is not found")
    @ApiResponse(responseCode = "409", description = "The message could not be replayed")
    void replayQueueDlqMessage(
        @PathVariable("messageId") String messageId,
        @RequestParam(name = "queue") String queue
    );

    @DeleteMapping("/dlq/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a failed message",
        description = "Deletes the given message from the dead letter queue (DLQ) of a connector"
            + " queue. The message is discarded for good and is not processed again. Only the"
            + " DLQ of the queues owned by the connector can be modified, never the one of the"
            + " gateway queues."
    )
    @ApiResponse(responseCode = "204", description = "The message was deleted")
    @ApiResponse(responseCode = "400", description = "The DLQ of the queue is not managed")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "The queue or the message is not found")
    @ApiResponse(responseCode = "409", description = "The message could not be deleted")
    void deleteQueueDlqMessage(
        @PathVariable("messageId") String messageId,
        @RequestParam(name = "queue") String queue
    );

    @PostMapping("/dlq/replay")
    @Operation(
        summary = "Replay a list of failed messages",
        description = "Sends the given dead letter queue (DLQ) messages of a connector queue back"
            + " to that queue, and removes them from the DLQ. A message which cannot be replayed"
            + " stays in the DLQ. The replay is at-least-once: if removing a message from the"
            + " DLQ fails, it is delivered again."
    )
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "The queue is not monitored")
    ConnectorQueueReplayResult replayQueueDlqMessages(
        @Valid @RequestBody ConnectorReplayQueueDlqMessagesRequest request
    );
}
