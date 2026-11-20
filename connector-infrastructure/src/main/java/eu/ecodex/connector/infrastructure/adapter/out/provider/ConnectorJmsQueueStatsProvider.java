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

import eu.ecodex.connector.application.port.spi.ConnectorQueueStatsProvider;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import eu.ecodex.connector.domain.model.stats.queue.MonitoredQueue;
import eu.ecodex.connector.domain.model.stats.queue.MonitoredQueueCategory;
import eu.ecodex.connector.infrastructure.config.property.ActiveMQAddressSettingsProperties;
import eu.ecodex.connector.infrastructure.config.property.ConnectorQueueProperties;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionDetails;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ConnectorQueueStatsProvider}.
 */
@Slf4j
@Component
public class ConnectorJmsQueueStatsProvider implements ConnectorQueueStatsProvider {
    private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("ID:[A-Za-z0-9._:\\-]+");
    private static final long REMOVE_TIMEOUT_MILLIS = 2_000L;
    private static final String DELIVERY_COUNT_PROPERTY = "JMSXDeliveryCount";
    private static final String DLQ_FAILURE_CAUSE_PROPERTY = "dlqDeliveryFailureCause";
    private static final int MAX_PROPERTY_VALUE_LENGTH = 500;
    private static final List<String> TRUSTED_PACKAGES = List.of("eu.ecodex.connector");

    private final JmsTemplate jmsTemplate;
    private final List<MonitoredQueue> monitoredQueues;
    private final String dlqPrefix;

    /**
     * Creates the provider for the queues named in the connector configuration. It uses its own
     * plain connection to the broker, not the application's JTA/XA one: through an XA session the
     * queue browser returns {@code null} instead of the messages, so nothing would be listed.
     *
     * @param connectionDetails the broker connection details
     * @param queueProperties   the configured names of the connector and gateway queues
     * @param addressSettings   the broker address settings, providing the DLQ name prefix
     */
    @Autowired
    public ConnectorJmsQueueStatsProvider(
        ActiveMQConnectionDetails connectionDetails,
        ConnectorQueueProperties queueProperties,
        ActiveMQAddressSettingsProperties addressSettings) {
        this(plainJmsTemplate(connectionDetails), queueProperties, addressSettings);
    }

    /**
     * Creates the provider for the queues named in the connector configuration.
     *
     * @param jmsTemplate     the template used to browse the queues, over a non-XA connection
     * @param queueProperties the configured names of the connector and gateway queues
     * @param addressSettings the broker address settings, providing the DLQ name prefix
     */
    public ConnectorJmsQueueStatsProvider(
        JmsTemplate jmsTemplate,
        ConnectorQueueProperties queueProperties,
        ActiveMQAddressSettingsProperties addressSettings) {
        this.jmsTemplate = jmsTemplate;
        this.dlqPrefix = addressSettings.dlqPrefix();
        this.monitoredQueues = List.of(
            new MonitoredQueue(
                queueProperties.outboundMessageStagingQueue(),
                "Staging area for outbound messages awaiting processing",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.outboundEvidenceTriggerQueue(),
                "Triggers REM evidence generation for outbound messages",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.outboundMessageProcessingQueue(),
                "Outbound messages being processed toward the gateway",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.inboundMessageStagingQueue(),
                "Staging area for inbound messages awaiting processing",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.inboundMessageProcessingQueue(),
                "Inbound messages being processed from the gateway",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.inboundEvidenceTriggerQueue(),
                "Triggers REM evidence generation for inbound messages",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.backendDeliveryQueue(),
                "Messages ready for delivery to the backend",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.resolvedMessageCleanupProcessingQueue(),
                "Cleanup of the attachments of resolved (confirmed or rejected) messages",
                MonitoredQueueCategory.CONNECTOR
            ),
            new MonitoredQueue(
                queueProperties.gatewaySubmissionQueue(),
                "Gateway JMS queue receiving the messages submitted by the connector",
                MonitoredQueueCategory.GATEWAY
            ),
            new MonitoredQueue(
                queueProperties.gatewaySubmissionReplyQueue(),
                "Gateway JMS queue carrying the submission replies to the connector",
                MonitoredQueueCategory.GATEWAY
            ),
            new MonitoredQueue(
                queueProperties.gatewayReceptionQueue(),
                "Gateway JMS queue delivering the incoming messages to the connector",
                MonitoredQueueCategory.GATEWAY
            )
        );
    }

    private static JmsTemplate plainJmsTemplate(ActiveMQConnectionDetails connectionDetails) {
        var factory = new ActiveMQConnectionFactory(connectionDetails.getBrokerUrl());
        factory.setTrustedPackages(TRUSTED_PACKAGES);
        factory.setTrustAllPackages(false);

        if (connectionDetails.getUser() != null) {
            factory.setUserName(connectionDetails.getUser());
            factory.setPassword(connectionDetails.getPassword());
        }

        return new JmsTemplate(factory);
    }

    private static String truncate(String value) {
        return value.length() <= MAX_PROPERTY_VALUE_LENGTH
            ? value
            : value.substring(0, MAX_PROPERTY_VALUE_LENGTH) + "...";
    }

    private static int parseDeliveryCount(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.debug("Invalid {} value [{}]", DELIVERY_COUNT_PROPERTY, value);
            return 0;
        }
    }

    private static String messageTypeOf(Message message) {
        if (message instanceof MapMessage) {
            return "MapMessage";
        }

        if (message instanceof TextMessage) {
            return "TextMessage";
        }

        if (message instanceof BytesMessage) {
            return "BytesMessage";
        }

        if (message instanceof ObjectMessage) {
            return "ObjectMessage";
        }

        if (message instanceof StreamMessage) {
            return "StreamMessage";
        }

        return "Message";
    }

    @Override
    public List<ConnectorQueueStats> getAllStats() {
        return monitoredQueues.stream().map((monitoredQueue -> getStats(
            monitoredQueue.name(),
            monitoredQueue.description(),
            monitoredQueue.category()
        ))).toList();
    }

    private String dlqNameOf(String queue) {
        return dlqPrefix + queue;
    }

    private long count(String queue) {
        Long n = jmsTemplate.browse(
            queue, (session, browser) -> {
                long i = 0;
                var e = browser.getEnumeration();
                while (e.hasMoreElements()) {
                    if (e.nextElement() != null) {
                        i++;
                    }
                }
                return i;
            }
        );
        return n == null ? 0 : n;
    }

    private ConnectorQueueStats getStats(
        String queue,
        String queueDescription,
        MonitoredQueueCategory category) {
        return ConnectorQueueStats.of(
            queue,
            queueDescription,
            count(queue),
            count(dlqNameOf(queue)),
            category
        );
    }

    @Override
    public boolean isMonitored(String queueName) {
        return monitoredQueues.stream().anyMatch(queue -> queue.name().equals(queueName));
    }

    @Override
    public ConnectorPageResult<ConnectorQueueMessage> listMessages(
        String queueName, ConnectorPageRequest pageRequest) {
        return browse(queueName, pageRequest);
    }

    @Override
    public ConnectorPageResult<ConnectorQueueMessage> listDlqMessages(
        String queueName, ConnectorPageRequest pageRequest) {
        return browse(dlqNameOf(queueName), pageRequest);
    }

    private ConnectorPageResult<ConnectorQueueMessage> browse(
        String destination, ConnectorPageRequest pageRequest) {
        var offset = (long) pageRequest.page() * pageRequest.size();
        var size = pageRequest.size();

        var result = jmsTemplate.browse(
            destination, (session, browser) -> {
                var content = new ArrayList<ConnectorQueueMessage>(size);
                long total = 0;
                var messages = browser.getEnumeration();
                while (messages.hasMoreElements()) {
                    var message = (Message) messages.nextElement();
                    if (message == null) {
                        // the broker may return null when a message expires or is consumed
                        // while browsing
                        continue;
                    }
                    if (total >= offset && content.size() < size) {
                        content.add(toQueueMessage(message));
                    }
                    total++;
                }
                var totalPages = (int) ((total + size - 1) / size);
                return ConnectorPageResult.of(content, size, total, totalPages);
            }
        );

        return result == null ? ConnectorPageResult.of(List.of(), size, 0, 0) : result;
    }

    private ConnectorQueueMessage toQueueMessage(Message message) throws JMSException {
        var properties = readProperties(message);

        return new ConnectorQueueMessage(
            message.getJMSMessageID(),
            message.getJMSTimestamp() > 0 ? Instant.ofEpochMilli(message.getJMSTimestamp()) : null,
            message.getJMSCorrelationID(),
            messageTypeOf(message),
            message.getJMSRedelivered(),
            parseDeliveryCount(properties.get(DELIVERY_COUNT_PROPERTY)),
            properties.get(DLQ_FAILURE_CAUSE_PROPERTY),
            properties
        );
    }

    private Map<String, String> readProperties(Message message) throws JMSException {
        var properties = new LinkedHashMap<String, String>();
        for (var name : Collections.list(message.getPropertyNames())) {
            var key = String.valueOf(name);
            var value = message.getObjectProperty(key);
            properties.put(key, truncate(String.valueOf(value)));
        }
        return properties;
    }

    @Override
    public boolean isDlqReplayable(String queueName) {
        return monitoredQueues.stream().anyMatch(
            queue -> queue.name().equals(queueName)
                && queue.category() == MonitoredQueueCategory.CONNECTOR
        );
    }

    @Override
    public ConnectorQueueReplayResult replayDlqMessages(
        String queueName, List<String> messageIds) {
        var replayed = new ArrayList<String>();
        var notFound = new ArrayList<String>();
        var failed = new ArrayList<String>();

        for (var messageId : messageIds) {
            // the identifier ends up in a JMS selector: only accept the broker's id syntax
            if (messageId == null || !MESSAGE_ID_PATTERN.matcher(messageId).matches()) {
                failed.add(String.valueOf(messageId));
                continue;
            }

            try {
                if (replay(queueName, messageId)) {
                    replayed.add(messageId);
                } else {
                    notFound.add(messageId);
                }
            } catch (RuntimeException | JMSException e) {
                log.warn(
                    "Replay of the DLQ message [{}] of the queue [{}] failed",
                    messageId, queueName, e
                );
                failed.add(messageId);
            }
        }

        log.info(
            "Replayed {} message(s) of the DLQ of [{}], {} not found, {} failed",
            replayed.size(), queueName, notFound.size(), failed.size()
        );

        return new ConnectorQueueReplayResult(replayed, notFound, failed);
    }

    /**
     * Sends a copy of the DLQ message back to its queue, then removes it from the DLQ. The copy is
     * sent first so that a failure never loses the message: the worst case is a duplicate left in
     * the DLQ.
     */
    private boolean replay(String queueName, String messageId) throws JMSException {
        var dlq = dlqNameOf(queueName);
        var selector = "JMSMessageID = '" + messageId + "'";

        var copy = jmsTemplate.browseSelected(
            dlq, selector, (session, browser) -> {
                var messages = browser.getEnumeration();
                while (messages.hasMoreElements()) {
                    if (messages.nextElement() instanceof Message message) {
                        return ReplayableMessage.from(message);
                    }
                }
                return null;
            }
        );
        if (copy == null) {
            return false;
        }

        jmsTemplate.send(queueName, copy::toJmsMessage);

        // consume the original, which acknowledges it; the connection must be started to receive
        jmsTemplate.execute(
            (Session session) -> {
                try (var consumer = session.createConsumer(session.createQueue(dlq), selector)) {
                    return consumer.receive(REMOVE_TIMEOUT_MILLIS);
                }
            }, true
        );

        return true;
    }

    /**
     * Content of a DLQ message, detached from the session which read it so that it can be sent
     * again. System properties and the broker's failure cause are dropped.
     */
    private record ReplayableMessage(
        String text,
        Map<String, Object> map,
        Map<String, Object> properties,
        String correlationId,
        String type
    ) {
        static ReplayableMessage from(Message message) throws JMSException {
            String text = null;
            Map<String, Object> map = null;
            switch (message) {
                case TextMessage textMessage -> text = textMessage.getText();
                case MapMessage mapMessage -> {
                    map = new LinkedHashMap<>();
                    for (var name : Collections.list(mapMessage.getMapNames())) {
                        map.put(String.valueOf(name), mapMessage.getObject(String.valueOf(name)));
                    }
                }
                default -> throw new UnsupportedOperationException(
                    "Unsupported message type [%s]".formatted(messageTypeOf(message))
                );
            }

            var properties = new LinkedHashMap<String, Object>();
            for (var name : Collections.list(message.getPropertyNames())) {
                var key = String.valueOf(name);
                if (!key.startsWith("JMSX") && !key.equals(DLQ_FAILURE_CAUSE_PROPERTY)) {
                    properties.put(key, message.getObjectProperty(key));
                }
            }

            return new ReplayableMessage(
                text,
                map,
                properties,
                message.getJMSCorrelationID(),
                message.getJMSType()
            );
        }

        Message toJmsMessage(Session session) throws JMSException {
            Message message;
            if (map != null) {
                var mapMessage = session.createMapMessage();
                for (var entry : map.entrySet()) {
                    mapMessage.setObject(entry.getKey(), entry.getValue());
                }
                message = mapMessage;
            } else {
                message = session.createTextMessage(text);
            }

            for (var entry : properties.entrySet()) {
                message.setObjectProperty(entry.getKey(), entry.getValue());
            }

            message.setJMSCorrelationID(correlationId);
            message.setJMSType(type);

            return message;
        }
    }
}
