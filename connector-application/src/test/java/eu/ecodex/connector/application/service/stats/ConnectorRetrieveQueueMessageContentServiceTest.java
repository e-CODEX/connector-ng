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

import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.spi.ConnectorQueueStatsProvider;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessageContent;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessageType;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRetrieveQueueMessageContentService")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorRetrieveQueueMessageContentServiceTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";
    private static final String ID = "ID:1";

    @Mock
    private ConnectorQueueStatsProvider queueStatsProvider;

    @InjectMocks
    private ConnectorRetrieveQueueMessageContentService service;

    private static ConnectorQueueMessageContent content() {
        var message = new ConnectorQueueMessage(
            ID, null, null, ConnectorQueueMessageType.TEXT, false, 0, null, Map.of()
        );
        return new ConnectorQueueMessageContent(message, null, "payload", false);
    }

    @Test
    void should_return_the_content_of_a_dlq_message() {
        var expected = content();
        when(queueStatsProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueStatsProvider.findMessageContent(QUEUE, ID, true))
            .thenReturn(Optional.of(expected));

        assertThat(service.execute(QUEUE, ID, true)).isSameAs(expected);
    }

    @Test
    void should_return_the_content_of_a_pending_message() {
        var expected = content();
        when(queueStatsProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueStatsProvider.findMessageContent(QUEUE, ID, false))
            .thenReturn(Optional.of(expected));

        assertThat(service.execute(QUEUE, ID, false)).isSameAs(expected);
    }

    @Test
    void should_read_the_dlq_of_a_gateway_queue() {
        var expected = content();
        when(queueStatsProvider.isMonitored("domibus.backend.jms.inQueue")).thenReturn(true);
        when(queueStatsProvider.findMessageContent("domibus.backend.jms.inQueue", ID, true))
            .thenReturn(Optional.of(expected));

        assertThat(service.execute("domibus.backend.jms.inQueue", ID, true)).isSameAs(expected);
    }

    @Test
    void should_fail_when_the_message_is_not_found() {
        when(queueStatsProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueStatsProvider.findMessageContent(QUEUE, ID, true)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(QUEUE, ID, true))
            .isInstanceOf(ConnectorQueueMessageNotFoundException.class)
            .hasMessageContaining(ID);
    }

    @Test
    void should_reject_a_queue_which_is_not_monitored() {
        when(queueStatsProvider.isMonitored("unknown")).thenReturn(false);

        assertThatThrownBy(() -> service.execute("unknown", ID, true))
            .isInstanceOf(ConnectorQueueNotFoundException.class);

        verify(queueStatsProvider, never()).findMessageContent(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
            ArgumentMatchers.anyBoolean()
        );
    }
}
