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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.application.port.api.link.ConnectorListLinkPartners;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.linkpartner.ConnectorLinkPartnerController;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConnectorLinkPartnerController.class)
public class ConnectorLinkPartnerControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/link-partners";

    @MockitoBean
    private ConnectorListLinkPartners listLinkPartnersService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_list_all_link_partners_successfully() throws Exception {
        when(listLinkPartnersService.execute(any()))
            .thenReturn(List.of(LinkPartnerTestFixtures.createAliceBackendLinkPartner()));

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(1)));

        verify(listLinkPartnersService).execute(null);
    }

    @Test
    void should_return_empty_list_if_no_link_partners_match_the_link_type() throws Exception {
        when(listLinkPartnersService.execute(any()))
            .thenReturn(List.of());

        mockMvc.perform(get(URL.concat("?linkType=BACKEND")).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(0)));

        verify(listLinkPartnersService).execute(ConnectorLinkType.BACKEND);
    }
}
