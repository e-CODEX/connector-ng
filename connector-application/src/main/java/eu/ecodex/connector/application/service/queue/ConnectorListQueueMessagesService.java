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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.api.queue.ConnectorListQueueMessages;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueMessage;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorListQueueMessages}.
 *
 * <p>Only monitored queues can be browsed, so that the endpoint cannot be used to read arbitrary
 * queues of the broker.
 */
@Service
public class ConnectorListQueueMessagesService implements ConnectorListQueueMessages {
    private final ConnectorQueueManagementProvider queueManagementProvider;

    public ConnectorListQueueMessagesService(
        ConnectorQueueManagementProvider queueManagementProvider) {
        this.queueManagementProvider = queueManagementProvider;
    }

    @Override
    public ConnectorPageResult<ConnectorQueueMessage> execute(
        String queueName, boolean dlq, ConnectorPageRequest pageRequest) {
        if (!queueManagementProvider.isMonitored(queueName)) {
            throw new ConnectorQueueNotFoundException(queueName);
        }

        return dlq
            ? queueManagementProvider.listDlqMessages(queueName, pageRequest)
            : queueManagementProvider.listMessages(queueName, pageRequest);
    }
}
