/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.infrastructure.config.property.ConnectorQueueProperties;
import jakarta.jms.Message;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorJmsQueueStatsProvider")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "unchecked"})
class ConnectorJmsQueueStatsProviderTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";

    @Mock
    private JmsTemplate jmsTemplate;

    private static ConnectorQueueProperties queueProperties() {
        return new ConnectorQueueProperties(
            "q.out-staging", "q.out-processing", "q.out-evidence", "q.in-staging",
            "q.in-processing", "q.in-evidence", QUEUE, "gw.in", "gw.reply", "q.cleanup", "gw.out"
        );
    }

    private void browsing(List<Message> elements) throws Exception {
        var browser = mock(QueueBrowser.class);
        when(browser.getEnumeration()).thenReturn(Collections.enumeration(elements));
        when(jmsTemplate.browse(anyString(), (BrowserCallback<Object>) org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> ((BrowserCallback<Object>) invocation.getArgument(1))
                .doInJms(mock(Session.class), browser));
    }

    @Test
    void should_skip_null_elements_returned_by_the_browser() throws Exception {
        var message = mock(Message.class);
        when(message.getJMSMessageID()).thenReturn("ID:1");
        when(message.getPropertyNames()).thenReturn(Collections.emptyEnumeration());
        browsing(java.util.Arrays.asList(null, message, null));

        var provider = new ConnectorJmsQueueStatsProvider(jmsTemplate, queueProperties());
        var result = provider.listMessages(QUEUE, ConnectorPageRequest.of(0, 10));

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().messageId()).isEqualTo("ID:1");
    }

    @Test
    void should_monitor_every_queue_from_the_configuration() throws Exception {
        var provider = new ConnectorJmsQueueStatsProvider(jmsTemplate, queueProperties());

        assertThat(List.of(
            "q.out-staging", "q.out-processing", "q.out-evidence", "q.in-staging",
            "q.in-processing", "q.in-evidence", QUEUE, "gw.in", "gw.reply", "q.cleanup", "gw.out"
        )).allMatch(provider::isMonitored);
        assertThat(provider.isMonitored("connector.queues.outbound-message-staging-queue"))
            .isFalse();
    }
}
