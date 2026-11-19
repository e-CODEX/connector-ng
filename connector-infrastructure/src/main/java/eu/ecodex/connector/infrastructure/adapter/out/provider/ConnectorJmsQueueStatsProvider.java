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
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import eu.ecodex.connector.domain.model.stats.queue.MonitoredQueue;
import eu.ecodex.connector.infrastructure.config.property.ConnectorQueueProperties;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ConnectorQueueStatsProvider}.
 */
@Slf4j
@Component
public class ConnectorJmsQueueStatsProvider implements ConnectorQueueStatsProvider {
    private static final String DELIVERY_COUNT_PROPERTY = "JMSXDeliveryCount";
    private static final String DLQ_FAILURE_CAUSE_PROPERTY = "dlqDeliveryFailureCause";
    private static final int MAX_PROPERTY_VALUE_LENGTH = 500;

    private final JmsTemplate jmsTemplate;
    private final List<MonitoredQueue> monitoredQueues;

    /**
     * Creates the provider for the queues named in the connector configuration.
     *
     * @param jmsTemplate     the template used to browse the queues
     * @param queueProperties the configured names of the connector and gateway queues
     */
    public ConnectorJmsQueueStatsProvider(
        JmsTemplate jmsTemplate, ConnectorQueueProperties queueProperties) {
        this.jmsTemplate = jmsTemplate;
        this.monitoredQueues = List.of(
            new MonitoredQueue(
                queueProperties.outboundMessageStagingQueue(),
                "Staging area for outbound messages awaiting processing"
            ),
            new MonitoredQueue(
                queueProperties.outboundEvidenceTriggerQueue(),
                "Triggers REM evidence generation for outbound messages"
            ),
            new MonitoredQueue(
                queueProperties.outboundMessageProcessingQueue(),
                "Outbound messages being processed toward the gateway"
            ),
            new MonitoredQueue(
                queueProperties.inboundMessageStagingQueue(),
                "Staging area for inbound messages awaiting processing"
            ),
            new MonitoredQueue(
                queueProperties.inboundMessageProcessingQueue(),
                "Inbound messages being processed from the gateway"
            ),
            new MonitoredQueue(
                queueProperties.inboundEvidenceTriggerQueue(),
                "Triggers REM evidence generation for inbound messages"
            ),
            new MonitoredQueue(
                queueProperties.backendDeliveryQueue(),
                "Messages ready for delivery to the backend"
            ),
            new MonitoredQueue(
                queueProperties.resolvedMessageCleanupProcessingQueue(),
                "Cleanup of the attachments of resolved (confirmed or rejected) messages"
            ),
            new MonitoredQueue(
                queueProperties.gatewaySubmissionQueue(),
                "Gateway JMS queue receiving the messages submitted by the connector"
            ),
            new MonitoredQueue(
                queueProperties.gatewaySubmissionReplyQueue(),
                "Gateway JMS queue carrying the submission replies to the connector"
            ),
            new MonitoredQueue(
                queueProperties.gatewayReceptionQueue(),
                "Gateway JMS queue delivering the incoming messages to the connector"
            )
        );
    }

    @Override
    public List<ConnectorQueueStats> getAllStats() {
        return monitoredQueues.stream().map((monitoredQueue -> getStats(
            monitoredQueue.name(),
            monitoredQueue.description()
        ))).toList();
    }

    private String dlqNameOf(String queue) {
        return "ActiveMQ.DLQ." + queue;
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

    private ConnectorQueueStats getStats(String queue, String queueDescription) {
        return ConnectorQueueStats.of(
            queue,
            queueDescription,
            count(queue),
            count(dlqNameOf(queue))
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
        return switch (message) {
            case MapMessage ignored -> "MapMessage";
            case TextMessage ignored -> "TextMessage";
            case BytesMessage ignored -> "BytesMessage";
            case ObjectMessage ignored -> "ObjectMessage";
            case StreamMessage ignored -> "StreamMessage";
            default -> "Message";
        };
    }
}
