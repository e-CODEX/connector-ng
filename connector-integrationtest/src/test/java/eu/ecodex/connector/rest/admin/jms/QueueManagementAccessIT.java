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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("QueueManagementAccessIT REST")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class QueueManagementAccessIT extends AbstractIntegrationTest {
    private static final String QUEUE = "connector.queues.backend-delivery-queue";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_forbid_a_user_without_the_admin_role_to_replay_or_delete_a_message() {
        var token = "Bearer " + generateDefaultSimpleUserToken();

        apiClient.post()
            .uri("/api/v1/admin/jms/queues/dlq/messages/{id}/replay?queue={queue}",
                "ID:1", QUEUE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isForbidden();

        apiClient.post()
            .uri("/api/v1/admin/jms/queues/dlq/replay")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"queue\":\"" + QUEUE + "\",\"messageIds\":[\"ID:1\"]}")
            .exchange()
            .expectStatus()
            .isForbidden();

        apiClient.delete()
            .uri("/api/v1/admin/jms/queues/dlq/messages/{id}?queue={queue}", "ID:1", QUEUE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_forbid_a_user_without_the_admin_role_to_read_the_messages() {
        var token = "Bearer " + generateDefaultSimpleUserToken();

        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages?queue={queue}", QUEUE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isForbidden();

        apiClient.get()
            .uri("/api/v1/admin/jms/queues/messages/{id}?queue={queue}", "ID:1", QUEUE)
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_forbid_a_user_without_the_admin_role_to_read_the_queue_statistics() {
        apiClient.get()
            .uri("/api/v1/admin/jms/queues/stats")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultSimpleUserToken())
            .exchange()
            .expectStatus()
            .isForbidden();
    }
}
