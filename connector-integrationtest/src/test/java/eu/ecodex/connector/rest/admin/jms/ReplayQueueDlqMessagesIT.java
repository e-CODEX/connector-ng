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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ReplayQueueDlqMessagesIT REST")
public class ReplayQueueDlqMessagesIT extends AbstractIntegrationTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";

    @Autowired
    private RestTestClient apiClient;

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_replay_nothing_when_the_message_is_not_in_the_dlq() {
        apiClient.post()
            .uri("/api/v1/admin/jms/queues/dlq/replay")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"queue\":\"" + QUEUE + "\",\"messageIds\":[\"ID:unknown:1:1:1:1\"]}")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.notFound[0]").isEqualTo("ID:unknown:1:1:1:1")
            .jsonPath("$.replayed").isEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_refuse_to_replay_the_dlq_of_a_gateway_queue() {
        apiClient.post()
            .uri("/api/v1/admin/jms/queues/dlq/replay")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"queue\":\"domibus.backend.jms.inQueue\",\"messageIds\":[\"ID:1\"]}")
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void should_reject_unauthenticated_replay_requests() {
        apiClient.post()
            .uri("/api/v1/admin/jms/queues/dlq/replay")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"queue\":\"" + QUEUE + "\",\"messageIds\":[\"ID:1\"]}")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
