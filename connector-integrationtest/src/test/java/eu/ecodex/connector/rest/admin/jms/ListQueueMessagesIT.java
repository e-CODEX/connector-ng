/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.jms;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessageType;
import jakarta.jms.ConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ListQueueMessagesIT REST")
public class ListQueueMessagesIT extends AbstractIntegrationTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";
    private static final String PENDING_QUEUE = "connector.queues.inbound-evidence-trigger-queue";

    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_list_the_dlq_messages_of_a_monitored_queue() {
        send("ActiveMQ.DLQ." + QUEUE);

        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue={queue}&dlq=true&page=0&size=10", QUEUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(
                new ParameterizedTypeReference<ConnectorPageResult<ConnectorQueueMessage>>() {
                })
            .value(result -> {
                assertThat(result).isNotNull();
                assertThat(result.totalElements()).isGreaterThanOrEqualTo(1);
                assertThat(result.content()).isNotEmpty();
                assertThat(result.content().getFirst().messageType())
                    .isEqualTo(ConnectorQueueMessageType.TEXT);
            });
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_for_a_queue_which_is_not_monitored() {
        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue=not.a.monitored.queue&dlq=true")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void should_reject_unauthenticated_requests() {
        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue={queue}&dlq=true", QUEUE)
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_list_the_pending_messages_of_a_monitored_queue() {
        send(PENDING_QUEUE);

        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue={queue}&page=0&size=10", PENDING_QUEUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(
                new ParameterizedTypeReference<ConnectorPageResult<ConnectorQueueMessage>>() {
                })
            .value(result -> {
                assertThat(result).isNotNull();
                assertThat(result.totalElements()).isGreaterThanOrEqualTo(1);
                assertThat(result.content().getFirst().messageType())
                    .isEqualTo(ConnectorQueueMessageType.TEXT);
                assertThat(result.content().getFirst().failureCause()).isNull();
            });
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_for_pending_messages_of_a_queue_which_is_not_monitored() {
        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue=not.a.monitored.queue")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    private void send(String queue) {
        try (var context = connectionFactory.createContext()) {
            context.createProducer().send(context.createQueue(queue), "payload-not-exposed");
        }
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_list_the_dlq_of_a_gateway_queue() {
        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue=domibus.backend.jms.inQueue&dlq=true")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk();
    }
}
