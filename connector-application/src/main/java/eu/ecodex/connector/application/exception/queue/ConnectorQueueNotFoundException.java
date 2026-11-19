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

import eu.ecodex.connector.application.exception.NotFoundException;

/**
 * Thrown when the requested queue is not one of the queues monitored by the connector.
 */
public class ConnectorQueueNotFoundException extends NotFoundException {
    /**
     * Creates the exception for the given queue name.
     *
     * @param queueName the name of the unknown queue
     */
    public ConnectorQueueNotFoundException(String queueName) {
        super("Queue [%s] is not a monitored queue".formatted(queueName));
    }
}
