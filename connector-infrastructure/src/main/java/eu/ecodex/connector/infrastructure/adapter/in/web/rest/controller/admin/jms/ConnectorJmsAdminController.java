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

import eu.ecodex.connector.application.port.api.stats.ConnectorDeleteQueueDlqMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorListQueueMessages;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessages;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveQueuesStats;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.queue.ConnectorReplayQueueDlqMessagesRequest;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing JMS queues within the connector system.
 */
@RestController
public class ConnectorJmsAdminController implements ConnectorJmsAdminApi {
    private final ConnectorRetrieveQueuesStats retrieveQueuesStats;

    private final ConnectorListQueueMessages listQueueMessages;

    private final ConnectorReplayQueueDlqMessages replayQueueDlqMessages;

    private final ConnectorReplayQueueDlqMessage replayQueueDlqMessage;

    private final ConnectorDeleteQueueDlqMessage deleteQueueDlqMessage;

    /**
     * Creates the controller.
     *
     * @param retrieveQueuesStats    the use case retrieving the queues statistics
     * @param listQueueMessages      the use case listing the messages of a queue or of its DLQ
     * @param replayQueueDlqMessages the use case replaying a list of DLQ messages
     * @param replayQueueDlqMessage  the use case replaying one DLQ message
     * @param deleteQueueDlqMessage  the use case deleting one DLQ message
     */
    public ConnectorJmsAdminController(
        ConnectorRetrieveQueuesStats retrieveQueuesStats,
        ConnectorListQueueMessages listQueueMessages,
        ConnectorReplayQueueDlqMessages replayQueueDlqMessages,
        ConnectorReplayQueueDlqMessage replayQueueDlqMessage,
        ConnectorDeleteQueueDlqMessage deleteQueueDlqMessage) {
        this.retrieveQueuesStats = retrieveQueuesStats;
        this.listQueueMessages = listQueueMessages;
        this.replayQueueDlqMessages = replayQueueDlqMessages;
        this.replayQueueDlqMessage = replayQueueDlqMessage;
        this.deleteQueueDlqMessage = deleteQueueDlqMessage;
    }

    @Override
    public List<ConnectorQueueStats> retrieveQueuesStats() {
        return retrieveQueuesStats.execute();
    }

    @Override
    public ConnectorPageResult<ConnectorQueueMessage> listQueueMessages(
        String queue, boolean dlq, int page, int size) {
        return listQueueMessages.execute(queue, dlq, ConnectorPageRequest.of(page, size));
    }

    @Override
    public ConnectorQueueReplayResult replayQueueDlqMessages(
        ConnectorReplayQueueDlqMessagesRequest request) {
        return replayQueueDlqMessages.execute(request.queue(), request.messageIds());
    }

    @Override
    public void replayQueueDlqMessage(String messageId, String queue) {
        replayQueueDlqMessage.execute(queue, messageId);
    }

    @Override
    public void deleteQueueDlqMessage(String messageId, String queue) {
        deleteQueueDlqMessage.execute(queue, messageId);
    }
}
