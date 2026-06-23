/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.attachment;

import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

public class ConnectorAttachmentIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    void should_succeed_to_upload_attachment() {
        var parts = produceAttachmentPart(MediaType.APPLICATION_PDF, 150);

        apiClient.post()
                 .uri("/api/v1/attachments/upload")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().isCreated()
                 .returnResult(String[].class);
    }

    @Test
    void should_fail_to_upload_attachment_if_payload_is_over_200_MB() {
        var parts = produceAttachmentPart(MediaType.APPLICATION_PDF, 201);

        apiClient.post()
                 .uri("/api/v1/attachments/upload")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().is4xxClientError();
    }
}
