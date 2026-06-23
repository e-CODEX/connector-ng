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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorListTransportSteps;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.transport.ConnectorMessageTransportStepAdminController;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestTestClient
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorMessageTransportStepAdminController.class)
public class ConnectorMessageTransportStepAdminControllerTest {
    private static final String URL = "/api/v1/admin/transport-steps";
    @MockitoBean
    private ConnectorListTransportSteps listTransportStepsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_200_when_listing_messages_transport_steps() throws Exception {
        var pageResult = new ConnectorPageResult<>(
                List.of(TransportStepFixtures.createTransportStep()), 1, 1, 1
        );

        when(listTransportStepsService.execute(any(), any(), any())).thenReturn(pageResult);

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
