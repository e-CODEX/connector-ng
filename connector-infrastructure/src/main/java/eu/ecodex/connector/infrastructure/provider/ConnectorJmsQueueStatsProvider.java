/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.provider;

import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import eu.ecodex.connector.domain.model.stats.queue.MonitoredQueue;
import eu.ecodex.connector.domain.spi.ConnectorQueueStatsProvider;
import java.util.List;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorQueueStatsProvider} interface for retrieving statistics.
 */
@Component
public class ConnectorJmsQueueStatsProvider implements ConnectorQueueStatsProvider {
    private static final List<MonitoredQueue> MONITORED_QUEUES = List.of(
        new MonitoredQueue(
            "connector.queues.outbound-message-staging-queue",
            "Staging area for outbound messages awaiting processing"
        ),
        new MonitoredQueue(
            "connector.queues.outbound-evidence-trigger-queue",
            "Triggers REM evidence generation for outbound messages"
        ),
        new MonitoredQueue(
            "connector.queues.outbound-message-processing-queue",
            "Outbound messages being processed toward the gateway"
        ),
        new MonitoredQueue(
            "connector.queues.inbound-message-processing-queue",
            "Inbound messages being processed from the gateway"
        ),
        new MonitoredQueue(
            "connector.queues.inbound-evidence-trigger-queue",
            "Triggers REM evidence generation for inbound messages"
        ),
        new MonitoredQueue(
            "connector.queues.backend-delivery-queue",
            "Messages ready for delivery to the backend"
        ),
        new MonitoredQueue(
            "domibus.backend.jms.inQueue",
            "Domibus backend inbound JMS queue"
        ),
        new MonitoredQueue(
            "domibus.backend.jms.replyQueue",
            "Domibus backend reply JMS queue"
        )
    );

    private final JmsTemplate jmsTemplate;

    public ConnectorJmsQueueStatsProvider(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public List<ConnectorQueueStats> getAllStats() {
        return MONITORED_QUEUES.stream().map((monitoredQueue -> getStats(
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
                    e.nextElement();
                    i++;
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
}
