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
 * Thrown when the dead letter queue of a queue is not managed by the connector, as for the queues
 * owned by the gateway: it can be listed but its messages can be neither replayed nor deleted.
 */
public class ConnectorQueueDlqNotSupportedException extends RuntimeException {
    /**
     * Creates the exception for the given queue name.
     *
     * @param queueName the name of the queue
     */
    public ConnectorQueueDlqNotSupportedException(String queueName) {
        super(
            "The dead letter queue of [%s] is not managed by the connector".formatted(queueName)
        );
    }
}
