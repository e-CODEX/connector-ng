/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.stats;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessages;
import eu.ecodex.connector.application.port.spi.ConnectorQueueStatsProvider;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorReplayQueueDlqMessages}.
 */
@Service
public class ConnectorReplayQueueDlqMessagesService implements ConnectorReplayQueueDlqMessages {
    private final ConnectorQueueStatsProvider queueStatsProvider;

    public ConnectorReplayQueueDlqMessagesService(ConnectorQueueStatsProvider queueStatsProvider) {
        this.queueStatsProvider = queueStatsProvider;
    }

    @Override
    public ConnectorQueueReplayResult execute(String queueName, List<String> messageIds) {
        if (!queueStatsProvider.isMonitored(queueName)) {
            throw new ConnectorQueueNotFoundException(queueName);
        }
        if (!queueStatsProvider.isDlqManaged(queueName)) {
            throw new ConnectorQueueDlqNotSupportedException(queueName);
        }

        var distinctIds = messageIds.stream().distinct().toList();

        return queueStatsProvider.replayDlqMessages(queueName, distinctIds);
    }
}
