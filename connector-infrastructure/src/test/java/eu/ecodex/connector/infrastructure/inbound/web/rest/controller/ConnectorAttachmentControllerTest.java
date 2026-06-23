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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorListAttachments;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.attachement.ConnectorAttachmentController;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestTestClient
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(ConnectorAttachmentController.class)
public class ConnectorAttachmentControllerTest {
    @MockitoBean
    private ConnectorUploadAttachments uploadAttachmentsService;
    @MockitoBean
    private ConnectorListAttachments listAttachmentsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_send_201_when_uploading_attachments() throws Exception {
        when(uploadAttachmentsService.execute(any()))
                .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/attachments/upload")
                                .file(getAttachment("raw/fake_file.pdf", "fake_file.pdf"))
                                .file(getAttachment("raw/fake_file.txt", "fake_file.txt"))
                                .contentType(MediaType.MULTIPART_FORM_DATA)
               )
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_return_200_when_retrieving_attachments() throws Exception {
        var pageResult = new ConnectorPageResult<>(
                List.of(MessageAttachmentTestFixtures.createAttachment()), 1, 1, 1
        );

        when(listAttachmentsService.execute(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/attachments")
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

    private MockMultipartFile getAttachment(String resourcePath, String filename) {
        return new MockMultipartFile(
                "attachments",
                filename,
                MediaType.APPLICATION_XML_VALUE,
                FileTestFixtures.readAsBytes(resourcePath)
        );
    }
}
