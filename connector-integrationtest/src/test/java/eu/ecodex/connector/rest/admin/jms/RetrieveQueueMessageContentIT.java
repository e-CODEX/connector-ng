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

import eu.ecodex.connector.AbstractIntegrationTest;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("RetrieveQueueMessageContentIT REST")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class RetrieveQueueMessageContentIT extends AbstractIntegrationTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";
    private static final String PENDING_QUEUE = "connector.queues.inbound-evidence-trigger-queue";
    private static final String URI = "/api/v1/admin/jms/queues/messages/{id}?queue={queue}";

    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_read_the_body_of_a_dlq_message() {
        var messageId = send("ActiveMQ.DLQ." + QUEUE, "{\"hello\":\"dlq\"}");

        apiClient.get()
                 .uri(URI + "&dlq=true", messageId, QUEUE)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody()
                 .jsonPath("$.payload.hello").isEqualTo("dlq")
                 .jsonPath("$.text").doesNotExist()
                 .jsonPath("$.truncated").isEqualTo(false)
                 .jsonPath("$.message.messageId").isEqualTo(messageId)
                 .jsonPath("$.message.messageType").isEqualTo("TEXT");
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_read_the_body_of_a_pending_message() {
        var messageId = send(PENDING_QUEUE, "pending-body");

        apiClient.get()
                 .uri(URI, messageId, PENDING_QUEUE)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody()
                 .jsonPath("$.text").isEqualTo("pending-body");
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_for_a_message_which_is_not_in_the_dlq() {
        apiClient.get()
                 .uri(URI + "&dlq=true", "ID:unknown:1:1:1:1", QUEUE)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isNotFound();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_for_a_queue_which_is_not_monitored() {
        apiClient.get()
                 .uri(URI, "ID:1", "not.a.monitored.queue")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isNotFound();
    }

    @Test
    void should_reject_unauthenticated_requests() {
        apiClient.get()
                 .uri(URI, "ID:1", QUEUE)
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    private String send(String queue, String text) {
        try (var context = connectionFactory.createContext()) {
            var message = context.createTextMessage(text);
            context.createProducer().send(context.createQueue(queue), message);
            return message.getJMSMessageID();
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }
}
