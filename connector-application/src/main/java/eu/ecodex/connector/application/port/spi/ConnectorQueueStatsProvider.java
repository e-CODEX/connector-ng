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

import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import java.util.List;

/**
 * Provider (port) giving access to the statistics of the connector's JMS queues.
 */
public interface ConnectorQueueStatsProvider {
    /**
     * Retrieves statistical information for all connector queues.
     *
     * @return a list of {@link ConnectorQueueStats} objects, each containing the name of the queue,
     *     the count of pending messages and the count of messages in the dead letter queue; never
     *     {@code null}, and empty if there are no queues
     */
    List<ConnectorQueueStats> getAllStats();

    /**
     * Tells whether the given queue is one of the queues monitored by the connector.
     *
     * @param queueName the name of the queue
     *
     * @return {@code true} if the queue is monitored
     */
    boolean isMonitored(String queueName);

    /**
     * Tells whether the dead letter queue of the given queue is managed by the connector, which is
     * the case for the queues it owns but not for the gateway queues, whose dead letter queue can
     * only be listed: its messages can neither be replayed nor deleted.
     *
     * @param queueName the name of a monitored queue
     *
     * @return {@code true} if the messages of the dead letter queue can be replayed and deleted
     */
    boolean isDlqManaged(String queueName);

    /**
     * Replays messages of the dead letter queue of a monitored queue: each message is sent back to
     * the queue, then removed from the dead letter queue. A failure leaves the message in the dead
     * letter queue; a failure while removing it may leave a duplicate there (at-least-once).
     *
     * @param queueName  the name of a monitored queue
     * @param messageIds the JMS identifiers of the messages to replay
     *
     * @return the outcome for each identifier; never {@code null}
     */
    ConnectorQueueReplayResult replayDlqMessages(String queueName, List<String> messageIds);

    /**
     * Deletes a message of the dead letter queue of a monitored queue.
     *
     * @param queueName the name of a monitored queue
     * @param messageId the JMS identifier of the message to delete
     *
     * @return {@code true} if the message was deleted, {@code false} if it is not in the dead
     *     letter queue
     *
     * @throws eu.ecodex.connector.application.exception.queue
     *     .ConnectorQueueMessageDeleteException if the message could not be deleted
     */
    boolean deleteDlqMessage(String queueName, String messageId);

    /**
     * Lists, page by page, the pending messages of a monitored queue, oldest first. Messages are
     * only browsed, never consumed.
     *
     * @param queueName   the name of a monitored queue
     * @param pageRequest the requested page
     *
     * @return the requested page of messages; never {@code null}
     */
    ConnectorPageResult<ConnectorQueueMessage> listMessages(
        String queueName, ConnectorPageRequest pageRequest
    );

    /**
     * Lists, page by page, the messages of the dead letter queue of a monitored queue, oldest
     * first. Messages are only browsed, never consumed.
     *
     * @param queueName   the name of a monitored queue
     * @param pageRequest the requested page
     *
     * @return the requested page of DLQ messages; never {@code null}
     */
    ConnectorPageResult<ConnectorQueueMessage> listDlqMessages(
        String queueName, ConnectorPageRequest pageRequest
    );
}
