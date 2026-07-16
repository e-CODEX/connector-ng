/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats.queue;

/**
 * Represents statistical information about a connector queue. This record holds the name of the
 * queue and its associated metrics, including the count of pending messages and messages in the
 * dead letter queue.
 *
 * @param queueName    The name of the queue.
 * @param pendingCount The number of messages pending processing in the queue.
 * @param dlqCount     The number of messages in the dead letter queue.
 */
public record ConnectorQueueStats(String queueName, long pendingCount, long dlqCount) {
    public static ConnectorQueueStats of(String queueName, long pendingCount, long dlqCount) {
        return new ConnectorQueueStats(queueName, pendingCount, dlqCount);
    }
}
