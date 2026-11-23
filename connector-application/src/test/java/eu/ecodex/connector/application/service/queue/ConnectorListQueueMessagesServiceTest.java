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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.queue.ConnectorQueueMessageType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorListQueueMessagesService")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorListQueueMessagesServiceTest {
    private static final String QUEUE = "connector.queues.outbound-message-staging-queue";

    @Mock
    private ConnectorQueueManagementProvider queueManagementProvider;

    @InjectMocks
    private ConnectorListQueueMessagesService service;

    @Test
    void should_list_the_dlq_messages_of_a_monitored_queue() {
        var pageRequest = ConnectorPageRequest.of(0, 20);
        var message = new ConnectorQueueMessage(
            "ID:1",
            Instant.parse("2026-11-19T10:00:00Z"),
            null,
            ConnectorQueueMessageType.MAP,
            true,
            6,
            "Exceeded redelivery policy",
            Map.of("JMSXDeliveryCount", "6")
        );
        var expected = ConnectorPageResult.of(List.of(message), 20, 1, 1);
        when(queueManagementProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueManagementProvider.listDlqMessages(QUEUE, pageRequest)).thenReturn(expected);

        var result = service.execute(QUEUE, true, pageRequest);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void should_list_the_pending_messages_of_a_monitored_queue() {
        var pageRequest = ConnectorPageRequest.of(0, 20);
        var expected = ConnectorPageResult.<ConnectorQueueMessage>of(List.of(), 20, 0, 0);
        when(queueManagementProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueManagementProvider.listMessages(QUEUE, pageRequest)).thenReturn(expected);

        var result = service.execute(QUEUE, false, pageRequest);

        assertThat(result).isSameAs(expected);
        verify(queueManagementProvider, never()).listDlqMessages(QUEUE, pageRequest);
    }

    @Test
    void should_reject_a_queue_which_is_not_monitored() {
        when(queueManagementProvider.isMonitored("unknown")).thenReturn(false);

        var pageRequest = ConnectorPageRequest.of(0, 20);

        assertThatThrownBy(() -> service.execute("unknown", false, pageRequest))
            .isInstanceOf(ConnectorQueueNotFoundException.class)
            .hasMessageContaining("unknown");

        verify(queueManagementProvider).isMonitored("unknown");
        verifyNoMoreInteractions(queueManagementProvider);
    }

    @Test
    void should_list_the_dlq_of_a_gateway_queue() {
        var pageRequest = ConnectorPageRequest.of(0, 20);
        var expected = ConnectorPageResult.<ConnectorQueueMessage>of(List.of(), 20, 0, 0);
        when(queueManagementProvider.isMonitored("domibus.backend.jms.inQueue")).thenReturn(true);
        when(queueManagementProvider.listDlqMessages("domibus.backend.jms.inQueue", pageRequest))
            .thenReturn(expected);

        assertThat(service.execute("domibus.backend.jms.inQueue", true, pageRequest))
            .isSameAs(expected);
    }
}
