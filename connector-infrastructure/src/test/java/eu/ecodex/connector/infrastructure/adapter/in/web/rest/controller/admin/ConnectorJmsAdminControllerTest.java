/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.QueuesStatsTestFixtures;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueDlqNotSupportedException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageDeleteException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageNotFoundException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueMessageReplayException;
import eu.ecodex.connector.application.exception.queue.ConnectorQueueNotFoundException;
import eu.ecodex.connector.application.port.api.stats.ConnectorDeleteQueueDlqMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorListQueueMessages;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessage;
import eu.ecodex.connector.application.port.api.stats.ConnectorReplayQueueDlqMessages;
import eu.ecodex.connector.application.port.api.stats.ConnectorRetrieveQueuesStats;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueMessage;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueReplayResult;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller.admin.jms.ConnectorJmsAdminController;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("ConnectorJmsAdminController")
@WebMvcTest(ConnectorJmsAdminController.class)
public class ConnectorJmsAdminControllerTest extends AbstractWebMvcTest {
    private static final String BASE_URL = "/api/v1/admin/jms/queues/stats";

    @MockitoBean
    private ConnectorRetrieveQueuesStats retrieveQueuesStats;
    @MockitoBean
    private ConnectorListQueueMessages listQueueMessages;
    @MockitoBean
    private ConnectorReplayQueueDlqMessages replayQueueDlqMessages;
    @MockitoBean
    private ConnectorReplayQueueDlqMessage replayQueueDlqMessage;
    @MockitoBean
    private ConnectorDeleteQueueDlqMessage deleteQueueDlqMessage;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("retrieving the queues statistics")
    class QueueStats {
        @Test
        void should_return_200_with_the_queue_stats() throws Exception {
            when(retrieveQueuesStats.execute())
                .thenReturn(List.of(QueuesStatsTestFixtures.create()));

            mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$[0].queueName").value(
                       "connector.queues.outbound-message-staging-queue"))
                   .andExpect(jsonPath("$[0].queueDescription")
                                  .value("Staging area for outbound messages awaiting processing"))
                   .andExpect(jsonPath("$[0].pendingCount").value(0))
                   .andExpect(jsonPath("$[0].dlqCount").value(1));
        }
    }

    @Nested
    @DisplayName("listing the messages of a queue or of its DLQ")
    class ListMessages {
        @Test
        void should_return_200_with_the_dlq_messages_of_a_queue() throws Exception {
            var message = new ConnectorQueueMessage(
                "ID:1", Instant.parse("2026-11-19T10:00:00Z"), null, "MapMessage", true, 6,
                "Exceeded redelivery policy", Map.of("JMSXDeliveryCount", "6")
            );
            when(listQueueMessages.execute(
                eq("connector.queues.backend-delivery-queue"),
                eq(true), any(ConnectorPageRequest.class)
            )).thenReturn(ConnectorPageResult.of(List.of(message), 20, 1, 1));

            mockMvc.perform(get("/api/v1/admin/jms/queues/messages")
                                .param("queue", "connector.queues.backend-delivery-queue")
                                .param("dlq", "true"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content[0].messageId").value("ID:1"))
                   .andExpect(jsonPath("$.content[0].messageType").value("MapMessage"))
                   .andExpect(jsonPath("$.content[0].deliveryCount").value(6))
                   .andExpect(jsonPath("$.content[0].failureCause")
                                  .value("Exceeded redelivery policy"))
                   .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void should_return_404_when_the_queue_is_not_monitored() throws Exception {
            when(listQueueMessages.execute(
                eq("unknown"), eq(true), any(ConnectorPageRequest.class)
            )).thenThrow(new ConnectorQueueNotFoundException("unknown"));

            mockMvc.perform(get("/api/v1/admin/jms/queues/messages")
                                .param("queue", "unknown").param("dlq", "true"))
                   .andExpect(status().isNotFound());
        }

        @Test
        void should_return_200_with_the_pending_messages_of_a_queue() throws Exception {
            var message = new ConnectorQueueMessage(
                "ID:2", Instant.parse("2026-11-19T10:00:00Z"), null, "TextMessage", false, 1,
                null, Map.of()
            );
            when(listQueueMessages.execute(
                eq("connector.queues.backend-delivery-queue"),
                eq(false), any(ConnectorPageRequest.class)
            )).thenReturn(ConnectorPageResult.of(List.of(message), 20, 1, 1));

            mockMvc.perform(get("/api/v1/admin/jms/queues/messages")
                                .param("queue", "connector.queues.backend-delivery-queue"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content[0].messageId").value("ID:2"))
                   .andExpect(jsonPath("$.content[0].messageType").value("TextMessage"))
                   .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void should_return_404_for_pending_messages_of_a_queue_which_is_not_monitored()
            throws Exception {
            when(listQueueMessages.execute(
                eq("unknown"),
                eq(false), any(ConnectorPageRequest.class)
            )).thenThrow(new ConnectorQueueNotFoundException("unknown"));

            mockMvc.perform(get("/api/v1/admin/jms/queues/messages").param("queue", "unknown"))
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("replaying a list of DLQ messages")
    class ListReplay {
        @Test
        void should_replay_the_dlq_messages_of_a_queue() throws Exception {
            when(replayQueueDlqMessages.execute(
                "connector.queues.backend-delivery-queue", List.of("ID:1", "ID:2")
            )).thenReturn(new ConnectorQueueReplayResult(
                List.of("ID:1"),
                List.of("ID:2"),
                List.of()
            ));

            mockMvc.perform(post("/api/v1/admin/jms/queues/dlq/replay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                             {"queue": "connector.queues.backend-delivery-queue",
                                              "messageIds": ["ID:1", "ID:2"]}
                                             """))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.replayed[0]").value("ID:1"))
                   .andExpect(jsonPath("$.notFound[0]").value("ID:2"))
                   .andExpect(jsonPath("$.failed").isEmpty());
        }

        @Test
        void should_return_400_when_no_message_identifier_is_given() throws Exception {
            mockMvc.perform(post("/api/v1/admin/jms/queues/dlq/replay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"queue\": \"q\", \"messageIds\": []}"))
                   .andExpect(status().isBadRequest());
        }

        @Test
        void should_return_400_when_the_dlq_of_the_queue_is_not_managed() throws Exception {
            when(replayQueueDlqMessages.execute(
                eq("domibus.backend.jms.inQueue"),
                anyList()
            )).thenThrow(new ConnectorQueueDlqNotSupportedException("domibus.backend.jms.inQueue"));

            mockMvc.perform(post("/api/v1/admin/jms/queues/dlq/replay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                             {"queue": "domibus.backend.jms.inQueue", "messageIds": ["ID:1"]}
                                             """))
                   .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("replaying one DLQ message")
    class SingleReplay {
        @Test
        void should_replay_one_dlq_message() throws Exception {
            mockMvc.perform(post(
                       "/api/v1/admin/jms/queues/dlq/messages/{id}/replay",
                       "ID:host-1:1:1:1:1"
                   )
                                .param("queue", "connector.queues.backend-delivery-queue"))
                   .andExpect(status().isNoContent());

            verify(replayQueueDlqMessage).execute(
                "connector.queues.backend-delivery-queue", "ID:host-1:1:1:1:1"
            );
        }

        @Test
        void should_return_404_when_the_message_is_not_in_the_dlq() throws Exception {
            doThrow(new ConnectorQueueMessageNotFoundException("q", "ID:1"))
                .when(replayQueueDlqMessage).execute("q", "ID:1");

            mockMvc.perform(post("/api/v1/admin/jms/queues/dlq/messages/{id}/replay", "ID:1")
                                .param("queue", "q"))
                   .andExpect(status().isNotFound());
        }

        @Test
        void should_return_409_when_the_message_could_not_be_replayed() throws Exception {
            doThrow(new ConnectorQueueMessageReplayException("q", "ID:1"))
                .when(replayQueueDlqMessage).execute("q", "ID:1");

            mockMvc.perform(post("/api/v1/admin/jms/queues/dlq/messages/{id}/replay", "ID:1")
                                .param("queue", "q"))
                   .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("deleting one DLQ message")
    class Delete {
        @Test
        void should_delete_one_dlq_message() throws Exception {
            mockMvc.perform(delete(
                       "/api/v1/admin/jms/queues/dlq/messages/{id}",
                       "ID:host-1:1:1:1:1"
                   )
                                .param("queue", "connector.queues.backend-delivery-queue"))
                   .andExpect(status().isNoContent());

            verify(deleteQueueDlqMessage).execute(
                "connector.queues.backend-delivery-queue", "ID:host-1:1:1:1:1"
            );
        }

        @Test
        void should_return_404_when_deleting_a_message_which_is_not_in_the_dlq() throws Exception {
            doThrow(new ConnectorQueueMessageNotFoundException("q", "ID:1"))
                .when(deleteQueueDlqMessage).execute("q", "ID:1");

            mockMvc.perform(delete("/api/v1/admin/jms/queues/dlq/messages/{id}", "ID:1")
                                .param("queue", "q"))
                   .andExpect(status().isNotFound());
        }

        @Test
        void should_return_400_when_deleting_from_a_dlq_which_is_not_managed() throws Exception {
            doThrow(new ConnectorQueueDlqNotSupportedException("domibus.backend.jms.inQueue"))
                .when(deleteQueueDlqMessage).execute("domibus.backend.jms.inQueue", "ID:1");

            mockMvc.perform(delete("/api/v1/admin/jms/queues/dlq/messages/{id}", "ID:1")
                                .param("queue", "domibus.backend.jms.inQueue"))
                   .andExpect(status().isBadRequest());
        }

        @Test
        void should_return_409_when_the_message_could_not_be_deleted() throws Exception {
            doThrow(new ConnectorQueueMessageDeleteException(
                "q",
                "ID:1",
                new RuntimeException("x")
            ))
                .when(deleteQueueDlqMessage).execute("q", "ID:1");

            mockMvc.perform(delete("/api/v1/admin/jms/queues/dlq/messages/{id}", "ID:1")
                                .param("queue", "q"))
                   .andExpect(status().isConflict());
        }
    }
}
