/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.QueuesStatsTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.stats.ConnectorRetrieveQueuesStats;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.jms.ConnectorJmsAdminController;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestTestClient
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorJmsAdminController.class)
public class ConnectorJmsAdminControllerTest {
    private static final String BASE_URL = "/api/v1/admin/jms/queues/stats";

    @MockitoBean
    private ConnectorRetrieveQueuesStats retrieveQueuesStatsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_retrieve_message_stats() throws Exception {
        when(retrieveQueuesStatsService.execute())
            .thenReturn(List.of(QueuesStatsTestFixtures.create()));

        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].queueName").value("connector.queues.outbound-message-staging-queue"))
               .andExpect(jsonPath("$[0].pendingCount").value(0))
               .andExpect(jsonPath("$[0].dlqCount").value(1));
    }
}
