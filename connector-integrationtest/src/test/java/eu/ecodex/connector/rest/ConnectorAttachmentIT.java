/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest;

import eu.ecodex.connector.AbstractFileStorageTest;
import eu.ecodex.connector.FilePartTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

public class ConnectorAttachmentIT extends AbstractFileStorageTest {
    @Autowired
    private RestTestClient apiClient;

    @BeforeEach
    void setUp() {
        s3Client.createBucket(CreateBucketRequest.builder().bucket("attachments").build());
    }

    @Test
    void should_succeed_to_upload_attachment() {
        var parts = produceFilePart(150);

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
        var parts = produceFilePart(300);

        apiClient.post()
                 .uri("/api/v1/attachments/upload")
                 .contentType(MediaType.MULTIPART_FORM_DATA)
                 .body(parts)
                 .exchange()
                 .expectStatus().is4xxClientError();
    }

    private MultiValueMap<String, Object> produceFilePart(int fileSize) {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add(
                "attachments",
                FilePartTestFixtures.filePart(
                        "fake_file.pdf",
                        FileTestFixtures.generateFakeFile(fileSize),
                        MediaType.APPLICATION_PDF
                )
        );
        return parts;
    }
}
