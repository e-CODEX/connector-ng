/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.exception.queue;

/**
 * Thrown when a message of a dead letter queue could not be deleted. The message is left in the
 * dead letter queue.
 */
public class ConnectorQueueMessageDeleteException extends RuntimeException {
    /**
     * Creates the exception for the given queue and message identifier.
     *
     * @param queueName the name of the queue
     * @param messageId the JMS identifier of the message
     * @param cause     the failure which prevented the deletion
     */
    public ConnectorQueueMessageDeleteException(
        String queueName, String messageId, Throwable cause) {
        super("Message [%s] of the dead letter queue of [%s] could not be deleted"
            .formatted(messageId, queueName), cause);
    }
}
