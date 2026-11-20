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

/**
 * Use case for replaying one failed message: sends a message of the dead letter queue (DLQ) of a
 * connector queue back to that queue, and removes it from the DLQ.
 *
 * @see ConnectorReplayQueueDlqMessages
 */
public interface ConnectorReplayQueueDlqMessage {
    /**
     * Replays the given DLQ message.
     *
     * @param queueName the name of a monitored queue owned by the connector
     * @param messageId the JMS message identifier, as listed by the DLQ listing
     *
     * @throws eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException if
     *     the queue is not monitored by the connector
     * @throws eu.ecodex.connector.application.exception.queue
     *     .ConnectorQueueDlqNotSupportedException if the DLQ of the queue cannot be replayed by the
     *     connector
     * @throws eu.ecodex.connector.application.exception.queue
     *     .ConnectorQueueMessageNotFoundException if the message is not in the DLQ
     * @throws eu.ecodex.connector.application.exception.queue
     *     .ConnectorQueueMessageReplayException if the message could not be replayed; it is left
     *     in the DLQ
     */
    void execute(String queueName, String messageId);
}
