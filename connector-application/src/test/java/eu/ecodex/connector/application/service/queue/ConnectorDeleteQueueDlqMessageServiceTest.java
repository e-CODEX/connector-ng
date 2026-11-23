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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageDeleteException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.spi.ConnectorQueueManagementProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorDeleteQueueDlqMessageService")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorDeleteQueueDlqMessageServiceTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";
    private static final String ID = "ID:1";

    @Mock
    private ConnectorQueueManagementProvider queueManagementProvider;

    @InjectMocks
    private ConnectorDeleteQueueDlqMessageService service;

    @Test
    void should_delete_the_message() {
        when(queueManagementProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueManagementProvider.isDlqManaged(QUEUE)).thenReturn(true);
        when(queueManagementProvider.deleteDlqMessage(QUEUE, ID)).thenReturn(true);

        assertThatCode(() -> service.execute(QUEUE, ID)).doesNotThrowAnyException();

        verify(queueManagementProvider).deleteDlqMessage(QUEUE, ID);
    }

    @Test
    void should_fail_when_the_message_is_not_in_the_dlq() {
        when(queueManagementProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueManagementProvider.isDlqManaged(QUEUE)).thenReturn(true);
        when(queueManagementProvider.deleteDlqMessage(QUEUE, ID)).thenReturn(false);

        assertThatThrownBy(() -> service.execute(QUEUE, ID))
            .isInstanceOf(ConnectorQueueMessageNotFoundException.class)
            .hasMessageContaining(ID);
    }

    @Test
    void should_reject_a_queue_which_is_not_monitored() {
        when(queueManagementProvider.isMonitored("unknown")).thenReturn(false);

        assertThatThrownBy(() -> service.execute("unknown", ID))
            .isInstanceOf(ConnectorQueueNotFoundException.class);

        verify(queueManagementProvider, never()).deleteDlqMessage("unknown", ID);
    }

    @Test
    void should_never_delete_from_the_dlq_of_a_gateway_queue() {
        when(queueManagementProvider.isMonitored("domibus.backend.jms.inQueue")).thenReturn(true);
        when(queueManagementProvider.isDlqManaged("domibus.backend.jms.inQueue")).thenReturn(false);

        assertThatThrownBy(() -> service.execute("domibus.backend.jms.inQueue", ID))
            .isInstanceOf(ConnectorQueueDlqNotSupportedException.class);

        verify(queueManagementProvider, never()).deleteDlqMessage(
            "domibus.backend.jms.inQueue",
            ID
        );
    }

    @Test
    void should_let_a_failed_deletion_through() {
        when(queueManagementProvider.isMonitored(QUEUE)).thenReturn(true);
        when(queueManagementProvider.isDlqManaged(QUEUE)).thenReturn(true);
        when(queueManagementProvider.deleteDlqMessage(QUEUE, ID))
            .thenThrow(new ConnectorQueueMessageDeleteException(QUEUE, ID, new RuntimeException()));

        assertThatThrownBy(() -> service.execute(QUEUE, ID))
            .isInstanceOf(ConnectorQueueMessageDeleteException.class);
    }
}
