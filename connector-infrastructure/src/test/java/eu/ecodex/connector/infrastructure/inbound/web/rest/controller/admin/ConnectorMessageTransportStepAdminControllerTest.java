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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.api.transport.ConnectorListTransportSteps;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStepByTransportId;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.transport.ConnectorMessageTransportStepAdminController;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("ConnectorMessageTransportStepAdminController")
@WebMvcTest(ConnectorMessageTransportStepAdminController.class)
public class ConnectorMessageTransportStepAdminControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/admin/transport-steps";
    private static final String RETRIEVE_URL = "/api/v1/admin/transport-steps/%s/message";

    @MockitoBean
    private ConnectorListTransportSteps listTransportStepsService;
    @MockitoBean
    private ConnectorRetrieveTransportStepByTransportId retrieveTransportStepService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("list transport steps")
    class ListTransportSteps {
        @Test
        void should_return_200_with_the_paged_transport_steps() throws Exception {
            var pageResult = new ConnectorPageResult<>(
                List.of(TransportStepFixtures.createTransportStep()), 1, 1, 1
            );

            when(listTransportStepsService.execute(any())).thenReturn(pageResult);

            mockMvc.perform(get(URL)
                                .param("page", "0")
                                .param("size", "20")
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.totalElements").value(1))
                   .andExpect(jsonPath("$.totalPages").value(1))
                   .andExpect(jsonPath("$.size").value(1))
                   .andExpect(jsonPath("$.content").isArray())
                   .andExpect(jsonPath("$.content.length()").value(1));
        }
    }

    @Nested
    @DisplayName("retrieve transport steps")
    class RetrieveTransportSteps {
        private static final String TRANSPORT_ID =
            "1c544c2e-7e60-4647-a4b2-c3c89958b2a7@connector.ecodex.eu";
        private static final String TRANSPORTED_MESSAGE_ID =
            "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu";

        @Test
        void should_return_200_with_found_transport_step() throws Exception {
            when(retrieveTransportStepService.execute(TRANSPORT_ID))
                .thenReturn(TransportStepFixtures.createTransportStep());

            mockMvc.perform(get(RETRIEVE_URL.formatted(TRANSPORT_ID))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.identifier").value(TRANSPORTED_MESSAGE_ID));
        }

        @Test
        void should_return_404_with_not_found_transport_step() throws Exception {
            doThrow(ConnectorMessageTransportStepNotFoundException.class)
                .when(retrieveTransportStepService).execute(TRANSPORT_ID);

            mockMvc.perform(get(RETRIEVE_URL.formatted(TRANSPORT_ID))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isNotFound())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }
}
