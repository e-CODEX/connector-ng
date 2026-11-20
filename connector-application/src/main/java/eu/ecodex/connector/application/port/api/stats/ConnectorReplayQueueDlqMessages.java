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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import java.util.List;

/**
 * Use case for replaying failed messages: sends messages of the dead letter queue (DLQ) of a
 * connector queue back to that queue, and removes them from the DLQ.
 */
public interface ConnectorReplayQueueDlqMessages {
    /**
     * Replays the given DLQ messages. A message which cannot be replayed stays in the DLQ.
     *
     * @param queueName  the name of a monitored queue owned by the connector
     * @param messageIds the JMS message identifiers, as listed by the DLQ listing
     *
     * @return the identifiers which were replayed, not found or failed; never {@code null}
     *
     * @throws ConnectorQueueNotFoundException        if the queue is not monitored by the
     *                                                connector
     * @throws ConnectorQueueDlqNotSupportedException if the DLQ of the queue is not managed by the
     *                                                connector
     */
    ConnectorQueueReplayResult execute(String queueName, List<String> messageIds);
}
