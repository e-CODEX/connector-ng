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
import eu.ecodex.connector.domain.spi.ConnectorQueueStatsProvider;
import java.util.List;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorQueueStatsProvider} interface for retrieving statistics.
 */
@Component
public class ConnectorJmsQueueStatsProvider implements ConnectorQueueStatsProvider {
    private static final List<String> MONITORED_QUEUES = List.of(
        "connector.queues.outbound-message-staging-queue",
        "connector.queues.outbound-evidence-trigger-queue",
        "connector.queues.outbound-message-processing-queue",
        "connector.queues.inbound-message-processing-queue",
        "connector.queues.inbound-evidence-trigger-queue",
        "connector.queues.backend-delivery-queue",
        "domibus.backend.jms.inQueue",
        "domibus.backend.jms.replyQueue"
    );

    private final JmsTemplate jmsTemplate;

    public ConnectorJmsQueueStatsProvider(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public List<ConnectorQueueStats> getAllStats() {
        return MONITORED_QUEUES.stream().map((this::getStats)).toList();
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

    private ConnectorQueueStats getStats(String queue) {
        return ConnectorQueueStats.of(queue, count(queue), count(dlqNameOf(queue)));
    }
}
