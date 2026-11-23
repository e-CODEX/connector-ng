/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.queue;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.api.queue.ConnectorDeleteQueueDlqMessage;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorDeleteQueueDlqMessage}.
 *
 * <p>Only the DLQ of a monitored queue owned by the connector can be modified, never the one of
 * the gateway queues.
 */
@Service
public class ConnectorDeleteQueueDlqMessageService implements ConnectorDeleteQueueDlqMessage {
    private final ConnectorQueueManagementProvider queueManagementProvider;

    public ConnectorDeleteQueueDlqMessageService(
        ConnectorQueueManagementProvider queueManagementProvider) {
        this.queueManagementProvider = queueManagementProvider;
    }

    @Override
    public void execute(String queueName, String messageId) {
        if (!queueManagementProvider.isMonitored(queueName)) {
            throw new ConnectorQueueNotFoundException(queueName);
        }

        if (!queueManagementProvider.isDlqManaged(queueName)) {
            throw new ConnectorQueueDlqNotSupportedException(queueName);
        }

        if (!queueManagementProvider.deleteDlqMessage(queueName, messageId)) {
            throw new ConnectorQueueMessageNotFoundException(queueName, messageId);
        }
    }
}
