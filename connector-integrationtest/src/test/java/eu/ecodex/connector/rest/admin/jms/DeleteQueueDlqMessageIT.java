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
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("DeleteQueueDlqMessageIT REST")
public class DeleteQueueDlqMessageIT extends AbstractIntegrationTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";

    private static final String DLQ = "ActiveMQ.DLQ." + QUEUE;

    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_delete_a_message_of_the_dlq() {
        var deletedId = sendToDlq("to-delete");
        var keptId = sendToDlq("to-keep");

        apiClient.delete()
                 .uri("/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}", deletedId, QUEUE)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isNoContent();

        apiClient.get()
                 .uri(
                     "/api/v1/admin/jms/queues/messages?queue={queue}&dlq=true&page=0&size=100",
                     QUEUE
                 )
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(
                     new ParameterizedTypeReference<ConnectorPageResult<ConnectorQueueMessage>>() {
                     })
                 .value(result -> assertThat(result.content())
                     .extracting(ConnectorQueueMessage::messageId)
                     .contains(keptId)
                     .doesNotContain(deletedId));

        // deleting twice: the message is gone
        apiClient.delete()
                 .uri("/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}", deletedId, QUEUE)
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isNotFound();
    }

    private String sendToDlq(String text) {
        try (var context = connectionFactory.createContext()) {
            var message = context.createTextMessage(text);
            context.createProducer().send(context.createQueue(DLQ), message);
            return message.getJMSMessageID();
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_when_deleting_a_message_which_is_not_in_the_dlq() {
        apiClient.delete()
                 .uri(
                     "/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}",
                     "ID:unknown:1:1:1:1", QUEUE
                 )
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isNotFound();
    }

    @Test
    void should_reject_unauthenticated_delete_requests() {
        apiClient.delete()
                 .uri("/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}", "ID:1", QUEUE)
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_refuse_to_delete_a_message_of_a_gateway_queue_dlq() {
        apiClient.delete()
                 .uri(
                     "/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}",
                     "ID:1", "domibus.backend.jms.inQueue"
                 )
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus()
                 .isBadRequest();
    }
}
