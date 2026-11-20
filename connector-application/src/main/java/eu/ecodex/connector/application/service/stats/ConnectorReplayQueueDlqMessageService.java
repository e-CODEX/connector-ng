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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageReplayException;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessages;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorReplayQueueDlqMessage}, built on the replay of a list
 * of messages.
 */
@Service
public class ConnectorReplayQueueDlqMessageService implements ConnectorReplayQueueDlqMessage {
    private final ConnectorReplayQueueDlqMessages replayQueueDlqMessages;

    public ConnectorReplayQueueDlqMessageService(
        ConnectorReplayQueueDlqMessages replayQueueDlqMessages) {
        this.replayQueueDlqMessages = replayQueueDlqMessages;
    }

    @Override
    public void execute(String queueName, String messageId) {
        var result = replayQueueDlqMessages.execute(queueName, List.of(messageId));

        if (result.replayed().contains(messageId)) {
            return;
        }
        if (result.notFound().contains(messageId)) {
            throw new ConnectorQueueMessageNotFoundException(queueName, messageId);
        }

        throw new ConnectorQueueMessageReplayException(queueName, messageId);
    }
}
