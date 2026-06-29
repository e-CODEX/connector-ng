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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.TransportStepFixtures;
import eu.ecodex.connector.application.service.usecase.message.ConnectorListMessages;
import eu.ecodex.connector.application.service.usecase.message.ConnectorRetrieveMessage;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorRetrieveTransportStep;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.message.ConnectorMessageAdminController;
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
@WebMvcTest(ConnectorMessageAdminController.class)
public class ConnectorMessageAdminControllerTest {
    private static final String URL = "/api/v1/admin/messages";
    private static final String URL_MESSAGE_DETAIL = "/api/v1/admin/messages/%s";
    private static final String URL_TRANSPORT_STEP = "/api/v1/admin/messages/%s/transport-steps";

    @MockitoBean
    private ConnectorListMessages listMessagesService;
    @MockitoBean
    private ConnectorRetrieveMessage retrieveMessageService;
    @MockitoBean
    private ConnectorRetrieveTransportStep retrieveTransportStepService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_200_when_listing_messages() throws Exception {
        var pageResult = new ConnectorPageResult<>(
                List.of(MessageTestFixtures.createConfirmedMessage()), 1, 1, 1
        );

        when(listMessagesService.execute(any(), any(), any())).thenReturn(pageResult);

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

    // retrieve message

    @Test
    void should_return_200_ok_when_retrieving_a_message() throws Exception {
        when(retrieveMessageService.execute(any()))
                .thenReturn(MessageTestFixtures.createConfirmedMessage());

        mockMvc.perform(get(URL_MESSAGE_DETAIL.formatted("/223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.identifier").value(
                       "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu"));
    }

    @Test
    void should_return_404_not_found_when_retrieving_a_message_with_unknown_identifier()
            throws Exception {
        doThrow(ConnectorMessageNotFoundException.class).when(retrieveMessageService)
                                                        .execute(any());

        mockMvc.perform(get(URL_MESSAGE_DETAIL.formatted("unknown-identifier"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // retrieve message transport steps

    @Test
    void should_return_200_ok_when_retrieving_a_message_transport_steps() throws Exception {
        when(retrieveTransportStepService.execute(any()))
                .thenReturn(TransportStepFixtures.createTransportStep());

        mockMvc.perform(get(URL_TRANSPORT_STEP.formatted("/223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.transportedMessageIdentifier").value(
                       "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu"));
    }

    @Test
    void should_return_404_not_found_when_retrieving_a_message_transport_steps_with_unknown_identifier()
            throws Exception {
        doThrow(ConnectorMessageTransportStepNotFoundException.class).when(retrieveTransportStepService)
                                                                     .execute(any());

        mockMvc.perform(get(URL_TRANSPORT_STEP.formatted("unknown-identifier"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
