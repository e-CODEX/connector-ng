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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessageContent;

/**
 * Use case for reading one message of a monitored queue, or of its dead letter queue (DLQ), with
 * its body. The message is only browsed, never consumed.
 */
public interface ConnectorRetrieveQueueMessageContent {
    /**
     * Reads the given message.
     *
     * @param queueName    the name of a monitored queue, as returned by the queues statistics
     * @param jmsMessageId the JMS message identifier, as listed by the messages listing
     * @param dlq          {@code true} to look in the dead letter queue, {@code false} to look in
     *                     the queue itself
     *
     * @return the message with its body; never {@code null}
     *
     * @throws ConnectorQueueNotFoundException        if the queue is not monitored by the
     *                                                connector
     * @throws ConnectorQueueMessageNotFoundException if the message is not there
     */
    ConnectorQueueMessageContent execute(String queueName, String jmsMessageId, boolean dlq);
}
