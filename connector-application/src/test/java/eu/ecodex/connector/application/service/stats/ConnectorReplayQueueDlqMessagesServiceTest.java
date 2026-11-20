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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.spi.ConnectorQueueStatsProvider;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorReplayQueueDlqMessagesService")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorReplayQueueDlqMessagesServiceTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";

    @Mock
    private ConnectorQueueStatsProvider queueStatsProvider;

    @InjectMocks
    private ConnectorReplayQueueDlqMessagesService service;

    @Test
    void should_replay_the_distinct_messages_of_a_connector_queue() {
        var expected = new ConnectorQueueReplayResult(List.of("ID:1"), List.of("ID:2"), List.of());
        when(queueStatsProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueStatsProvider.isDlqReplayable(QUEUE)).thenReturn(true);
        when(queueStatsProvider.replayDlqMessages(QUEUE, List.of("ID:1", "ID:2")))
            .thenReturn(expected);

        var result = service.execute(QUEUE, List.of("ID:1", "ID:2", "ID:1"));

        assertThat(result).isSameAs(expected);
    }

    @Test
    void should_reject_a_queue_which_is_not_monitored() {
        when(queueStatsProvider.isMonitored("unknown")).thenReturn(false);

        assertThatThrownBy(() -> service.execute("unknown", List.of("ID:1")))
            .isInstanceOf(ConnectorQueueNotFoundException.class);

        verify(queueStatsProvider, never()).replayDlqMessages(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyList()
        );
    }

    @Test
    void should_reject_the_replay_on_a_gateway_queue() {
        when(queueStatsProvider.isMonitored("domibus.backend.jms.inQueue")).thenReturn(true);
        when(queueStatsProvider.isDlqReplayable("domibus.backend.jms.inQueue")).thenReturn(false);

        assertThatThrownBy(() -> service.execute("domibus.backend.jms.inQueue", List.of("ID:1")))
            .isInstanceOf(ConnectorQueueDlqNotSupportedException.class);

        verify(queueStatsProvider, never()).replayDlqMessages(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyList()
        );
    }
}
