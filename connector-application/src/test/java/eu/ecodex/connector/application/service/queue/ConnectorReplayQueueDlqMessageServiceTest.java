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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageReplayException;
import eu.ecodex.connector.application.port.api.queue.ConnectorReplayQueueDlqMessages;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueReplayResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorReplayQueueDlqMessageService")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorReplayQueueDlqMessageServiceTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";
    private static final String ID = "ID:1";

    @Mock
    private ConnectorReplayQueueDlqMessages replayQueueDlqMessages;

    @InjectMocks
    private ConnectorReplayQueueDlqMessageService service;

    private void givenResult(List<String> replayed, List<String> notFound, List<String> failed) {
        when(replayQueueDlqMessages.execute(QUEUE, List.of(ID)))
            .thenReturn(new ConnectorQueueReplayResult(replayed, notFound, failed));
    }

    @Test
    void should_replay_the_message() {
        givenResult(List.of(ID), List.of(), List.of());

        assertThatCode(() -> service.execute(QUEUE, ID)).doesNotThrowAnyException();
    }

    @Test
    void should_fail_when_the_message_is_not_in_the_dlq() {
        givenResult(List.of(), List.of(ID), List.of());

        assertThatThrownBy(() -> service.execute(QUEUE, ID))
            .isInstanceOf(ConnectorQueueMessageNotFoundException.class)
            .hasMessageContaining(ID);
    }

    @Test
    void should_fail_when_the_message_could_not_be_replayed() {
        givenResult(List.of(), List.of(), List.of(ID));

        assertThatThrownBy(() -> service.execute(QUEUE, ID))
            .isInstanceOf(ConnectorQueueMessageReplayException.class)
            .hasMessageContaining(ID);
    }

    @Test
    void should_propagate_the_rejection_of_the_queue() {
        when(replayQueueDlqMessages.execute(QUEUE, List.of(ID)))
            .thenThrow(new ConnectorQueueDlqNotSupportedException(QUEUE));

        assertThatThrownBy(() -> service.execute(QUEUE, ID))
            .isInstanceOf(ConnectorQueueDlqNotSupportedException.class);
    }
}
