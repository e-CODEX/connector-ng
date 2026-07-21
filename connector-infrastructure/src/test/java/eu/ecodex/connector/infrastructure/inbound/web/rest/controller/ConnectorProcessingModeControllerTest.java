/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeActions;
import eu.ecodex.connector.application.port.api.pmode.ConnectorListProcessingModeServices;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.pmode.ConnectorProcessingModeController;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConnectorProcessingModeController.class)
public class ConnectorProcessingModeControllerTest extends AbstractWebMvcTest {
    private static final String SERVICE_URL = "/api/v1/processing-modes/%s/services";
    private static final String ACTION_URL = "/api/v1/processing-modes/%s/actions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnectorListProcessingModeServices listProcessingModeServices;
    @MockitoBean
    private ConnectorListProcessingModeActions listProcessingModeActions;

    // list processing mode services

    @Test
    void should_list_a_processing_mode_services_successfully() throws Exception {
        when(listProcessingModeServices.execute(any()))
            .thenReturn(List.of(ServiceTestFixtures.createService()));

        mockMvc.perform(
                   get(SERVICE_URL.formatted("default_business_domain"))
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].name").value("Connector-TEST"))
               .andExpect(jsonPath("$[0].type").value("urn:e-codex:services:"));
    }

    // list processing mode actions

    @Test
    void should_list_a_processing_mode_actions_successfully() throws Exception {
        when(listProcessingModeActions.execute(any()))
            .thenReturn(List.of(ActionTestFixtures.createAction()));

        mockMvc.perform(
                   get(ACTION_URL.formatted("default_business_domain"))
                       .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].name").value("ConTest_Form"));
    }
}
