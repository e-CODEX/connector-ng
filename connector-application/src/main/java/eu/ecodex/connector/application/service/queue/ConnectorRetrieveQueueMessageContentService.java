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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.api.queue.ConnectorRetrieveQueueMessageContent;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorRetrieveQueueMessageContent}.
 *
 * <p>Only monitored queues can be read, so that the endpoint cannot be used to read arbitrary
 * queues of the broker. The body is never logged.
 */
@Slf4j
@Service
public class ConnectorRetrieveQueueMessageContentService
    implements ConnectorRetrieveQueueMessageContent {
    private final ConnectorQueueManagementProvider queueManagementProvider;

    public ConnectorRetrieveQueueMessageContentService(
        ConnectorQueueManagementProvider queueManagementProvider) {
        this.queueManagementProvider = queueManagementProvider;
    }

    @Override
    public ConnectorQueueMessageContent execute(
        String queueName,
        String jmsMessageId,
        boolean dlq) {
        if (!queueManagementProvider.isMonitored(queueName)) {
            throw new ConnectorQueueNotFoundException(queueName);
        }

        var content = queueManagementProvider.findMessageContent(queueName, jmsMessageId, dlq)
                                             .orElseThrow(
                                                 () -> new ConnectorQueueMessageNotFoundException(
                                                     queueName,
                                                     jmsMessageId,
                                                     dlq
                                                 ));

        log.info(
            "Read the body of the JMS message [{}] of the queue [{}], dlq={}",
            jmsMessageId, queueName, dlq
        );

        return content;
    }
}
