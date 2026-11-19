/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.stats;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;

/**
 * Use case for listing the messages held in a monitored queue, or in its dead letter queue (DLQ).
 */
public interface ConnectorListQueueMessages {
    /**
     * Lists, page by page, the messages of the given queue or of its DLQ, oldest first. Messages
     * are only browsed, never consumed.
     *
     * @param queueName   the name of a monitored queue, as returned by the queues statistics
     * @param dlq         {@code true} to list the messages of the dead letter queue, {@code false}
     *                    to list the pending messages of the queue itself
     * @param pageRequest the requested page
     *
     * @return the requested page of messages; never {@code null}
     *
     * @throws ConnectorQueueNotFoundException if the queue is not monitored by the connector
     */
    ConnectorPageResult<ConnectorQueueMessage> execute(
        String queueName, boolean dlq, ConnectorPageRequest pageRequest
    );
}
