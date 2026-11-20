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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.infrastructure.config.property.ActiveMQAddressSettingsProperties;
import eu.ecodex.connector.infrastructure.config.property.ConnectorQueueProperties;
import jakarta.jms.Connection;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static ActiveMQAddressSettingsProperties addressSettings(String dlqPrefix) {
        return new ActiveMQAddressSettingsProperties(5, Duration.ofSeconds(60), 2.0, dlqPrefix);
    }

    private void browsing(List<Message> elements) throws Exception {
        var browser = mock(QueueBrowser.class);
        when(browser.getEnumeration()).thenReturn(Collections.enumeration(elements));
        when(jmsTemplate.browse(anyString(), (BrowserCallback<Object>) any()))
            .thenAnswer(invocation -> ((BrowserCallback<Object>) invocation.getArgument(1))
                .doInJms(mock(Session.class), browser));
    }

    @Nested
    @DisplayName("listing the messages")
    class Listing {
        @Test
        void should_skip_null_elements_returned_by_the_browser() throws Exception {
            var message = mock(Message.class);
            when(message.getJMSMessageID()).thenReturn("ID:1");
            when(message.getPropertyNames()).thenReturn(Collections.emptyEnumeration());
            browsing(Arrays.asList(null, message, null));

            var provider = new ConnectorJmsQueueStatsProvider(
                jmsTemplate, queueProperties(), addressSettings("ActiveMQ.DLQ.")
            );
            var result = provider.listMessages(QUEUE, ConnectorPageRequest.of(0, 10));

            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().messageId()).isEqualTo("ID:1");
        }

        @Test
        void should_browse_the_dlq_named_after_the_configured_prefix() throws Exception {
            browsing(List.of());
            var provider = new ConnectorJmsQueueStatsProvider(
                jmsTemplate, queueProperties(), addressSettings("custom.DLQ.")
            );

            provider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10));
            provider.listMessages(QUEUE, ConnectorPageRequest.of(0, 10));

            verify(jmsTemplate).browse(eq("custom.DLQ." + QUEUE), any(BrowserCallback.class));
            verify(jmsTemplate).browse(eq(QUEUE), any(BrowserCallback.class));
        }
    }

    @Nested
    @DisplayName("monitored queues")
    class MonitoredQueues {
        @Test
        void should_monitor_every_queue_from_the_configuration() throws Exception {
            var provider = new ConnectorJmsQueueStatsProvider(
                jmsTemplate, queueProperties(), addressSettings("ActiveMQ.DLQ.")
            );

            assertThat(List.of(
                "q.out-staging", "q.out-processing", "q.out-evidence", "q.in-staging",
                "q.in-processing", "q.in-evidence", QUEUE, "gw.in", "gw.reply", "q.cleanup", "gw.out"
            )).allMatch(provider::isMonitored);
            assertThat(provider.isMonitored("connector.queues.outbound-message-staging-queue"))
                .isFalse();
        }
    }

    /**
     * Replays and deletes messages through a private in-memory broker, to check what really ends up in the
     * queue and in the dead letter queue.
     */
    @Nested
    @DisplayName("replaying and deleting the dead letter queue messages")
    class DlqMessages {
        private static final String DLQ = "ActiveMQ.DLQ." + QUEUE;

        private JmsTemplate brokerTemplate;
        private ConnectorJmsQueueStatsProvider brokerProvider;
        private Connection keepAliveConnection;

        @BeforeEach
        void setUp() throws Exception {
            var brokerConnectionFactory = new ActiveMQConnectionFactory(
                "vm://replay-" + UUID.randomUUID() + "?broker.persistent=false&broker.useJmx=false"
            );
            // an in-memory broker stops, and loses its messages, when its last connection is closed
            keepAliveConnection = brokerConnectionFactory.createConnection();
            keepAliveConnection.start();
            brokerTemplate = new JmsTemplate(brokerConnectionFactory);
            brokerTemplate.setReceiveTimeout(1_000);
            brokerProvider = new ConnectorJmsQueueStatsProvider(
                brokerTemplate,
                queueProperties(),
                addressSettings("ActiveMQ.DLQ.")
            );
        }

        @AfterEach
        void tearDown() throws Exception {
            keepAliveConnection.close();
        }

        private void sendToDlq(String text) {
            brokerTemplate.send(DLQ, session -> {
                var message = session.createTextMessage(text);
                message.setStringProperty("_type", "ConnectorMessage");
                message.setStringProperty("dlqDeliveryFailureCause", "java.lang.Throwable: boom");
                message.setJMSCorrelationID("correlation-1");
                return message;
            });
        }

        private String dlqMessageId() {
            var page = brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10));
            assertThat(page.content()).hasSize(1);
            return page.content().getFirst().messageId();
        }

        @Test
        void should_move_the_message_back_to_its_queue_and_remove_it_from_the_dlq() throws Exception {
            sendToDlq("{\"payload\":1}");
            var messageId = dlqMessageId();

            var result = brokerProvider.replayDlqMessages(QUEUE, List.of(messageId));

            assertThat(result.replayed()).containsExactly(messageId);
            assertThat(result.notFound()).isEmpty();
            assertThat(result.failed()).isEmpty();
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10)).content())
                .isEmpty();

            var replayed = brokerTemplate.receive(QUEUE);
            assertThat(replayed).isInstanceOf(TextMessage.class);
            assertThat(((TextMessage) replayed).getText()).isEqualTo("{\"payload\":1}");

            assertThat(replayed.getStringProperty("_type")).isEqualTo("ConnectorMessage");
            assertThat(replayed.propertyExists("dlqDeliveryFailureCause")).isFalse();
            assertThat(replayed.getJMSCorrelationID()).isEqualTo("correlation-1");

            assertThat(brokerTemplate.receive(QUEUE)).isNull();
        }

        @Test
        void should_replay_a_map_message() throws Exception {
            brokerTemplate.send(DLQ, (Session session) -> {
                MapMessage message = session.createMapMessage();
                message.setString("name", "value");
                message.setInt("count", 3);
                return message;
            });
            var messageId = dlqMessageId();

            var result = brokerProvider.replayDlqMessages(QUEUE, List.of(messageId));

            assertThat(result.replayed()).containsExactly(messageId);
            var replayed = (MapMessage) brokerTemplate.receive(QUEUE);
            assertThat(replayed.getString("name")).isEqualTo("value");
            assertThat(replayed.getInt("count")).isEqualTo(3);
        }

        @Test
        void should_report_the_messages_which_are_not_in_the_dlq() {
            var result = brokerProvider.replayDlqMessages(QUEUE, List.of("ID:unknown-1:1:1:1:1"));

            assertThat(result.notFound()).containsExactly("ID:unknown-1:1:1:1:1");
            assertThat(result.replayed()).isEmpty();
            assertThat(brokerTemplate.receive(QUEUE)).isNull();
        }

        @Test
        void should_refuse_identifiers_which_are_not_broker_message_ids() {
            sendToDlq("kept");

            var result = brokerProvider.replayDlqMessages(
                QUEUE, List.of("x' OR JMSMessageID LIKE '%", "ID:1' OR 'a'='a")
            );

            assertThat(result.failed()).hasSize(2);
            assertThat(result.replayed()).isEmpty();
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10)).content())
                .hasSize(1);
            assertThat(brokerTemplate.receive(QUEUE)).isNull();
        }

        @Test
        void should_leave_an_unsupported_message_in_the_dlq() {
            brokerTemplate.send(DLQ, session -> session.createBytesMessage());
            var messageId = dlqMessageId();

            var result = brokerProvider.replayDlqMessages(QUEUE, List.of(messageId));

            assertThat(result.failed()).containsExactly(messageId);
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10)).content())
                .hasSize(1);
            assertThat(brokerTemplate.receive(QUEUE)).isNull();
        }

        @Test
        void should_only_replay_the_requested_messages() {
            sendToDlq("one");
            sendToDlq("two");
            var ids = brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10)).content()
                              .stream().map(m -> m.messageId()).toList();

            var result = brokerProvider.replayDlqMessages(QUEUE, List.of(ids.getFirst()));

            assertThat(result.replayed()).containsExactly(ids.getFirst());
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10)).content())
                .extracting(m -> m.messageId()).containsExactly(ids.getLast());
        }

        @Test
        void should_delete_only_the_requested_message_from_the_dlq() {
            sendToDlq("one");
            sendToDlq("two");
            var ids = brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10))
                                    .content().stream().map(m -> m.messageId()).toList();

            var deleted = brokerProvider.deleteDlqMessage(QUEUE, ids.getFirst());

            assertThat(deleted).isTrue();
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10))
                                     .content())
                .extracting(m -> m.messageId()).containsExactly(ids.getLast());
            // deleting is not replaying: nothing is sent back to the queue
            assertThat(brokerTemplate.receive(QUEUE)).isNull();
        }

        @Test
        void should_report_a_message_which_is_not_in_the_dlq() {
            assertThat(brokerProvider.deleteDlqMessage(QUEUE, "ID:unknown-1:1:1:1:1")).isFalse();
        }

        @Test
        void should_refuse_a_delete_identifier_which_is_not_a_broker_message_id() {
            sendToDlq("kept");

            assertThat(brokerProvider.deleteDlqMessage(QUEUE, "x' OR JMSMessageID LIKE '%"))
                .isFalse();
            assertThat(brokerProvider.listDlqMessages(QUEUE, ConnectorPageRequest.of(0, 10))
                                     .content()).hasSize(1);
        }
    }
}
