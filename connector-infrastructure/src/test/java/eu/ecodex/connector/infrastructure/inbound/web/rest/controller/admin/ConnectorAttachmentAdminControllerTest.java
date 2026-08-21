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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.application.port.api.attachment.ConnectorListAttachments;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.attachment.ConnectorAttachmentAdminController;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("ConnectorAttachmentAdminController")
@WebMvcTest(ConnectorAttachmentAdminController.class)
public class ConnectorAttachmentAdminControllerTest extends AbstractWebMvcTest {
    @MockitoBean
    private ConnectorListAttachments listAttachmentsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_200_when_retrieving_attachments() throws Exception {
        var pageResult = new ConnectorPageResult<>(
            List.of(MessageAttachmentTestFixtures.createAttachment()), 1, 1, 1
        );

        when(listAttachmentsService.execute(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/admin/attachments")
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
