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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.evidence.ConnectorRetrieveEvidence;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotFoundException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.evidence.ConnectorEvidenceController;
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
@WebMvcTest(ConnectorEvidenceController.class)
public class ConnectorMessageEvidenceControllerTest {
    private static final String URL_DOWNLOAD = "/api/v1/evidences/%s/download";
    @MockitoBean
    private ConnectorRetrieveEvidence downloadEvidenceService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_download_evidence_successfully() throws Exception {
        when(downloadEvidenceService.execute(any())).thenReturn(
                EvidenceTestFixtures.createSubmissionAcceptanceEvidence()
        );

        mockMvc.perform(get(URL_DOWNLOAD.formatted("12345678-1234-1234-1234-123456789012"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_XML));

        verify(downloadEvidenceService).execute("12345678-1234-1234-1234-123456789012");
    }

    @Test
    void should_return_404_not_found_when_downloading_an_evidence_with_unknown_identifier()
            throws Exception {
        doThrow(ConnectorEvidenceNotFoundException.class).when(downloadEvidenceService)
                                                         .execute(any());

        mockMvc.perform(get(URL_DOWNLOAD.formatted("unknown-identifier"))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
