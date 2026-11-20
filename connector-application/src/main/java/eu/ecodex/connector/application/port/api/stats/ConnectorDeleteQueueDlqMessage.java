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
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageDeleteException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;

/**
 * Use case for deleting one failed message from the dead letter queue (DLQ) of a connector queue.
 * The message is discarded for good: it is not processed again.
 */
public interface ConnectorDeleteQueueDlqMessage {
    /**
     * Deletes the given DLQ message.
     *
     * @param queueName the name of a monitored queue owned by the connector
     * @param messageId the JMS message identifier, as listed by the DLQ listing
     *
     * @throws ConnectorQueueNotFoundException        if the queue is not monitored by the
     *                                                connector
     * @throws ConnectorQueueDlqNotSupportedException if the DLQ of the queue is not managed by the
     *                                                connector
     * @throws ConnectorQueueMessageNotFoundException if the message is not in the DLQ
     * @throws ConnectorQueueMessageDeleteException   if the message could not be deleted; it is
     *                                                left in the DLQ
     */
    void execute(String queueName, String messageId);
}
