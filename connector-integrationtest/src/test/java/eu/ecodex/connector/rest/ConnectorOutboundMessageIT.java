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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.ConnectorOutboundMessageTestFixtures;
import eu.ecodex.connector.FilePartTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import tools.jackson.databind.ObjectMapper;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:sql/business-domain.sql",
                "classpath:sql/processing-mode.sql",
                "classpath:sql/party.sql",
                "classpath:sql/service.sql",
                "classpath:sql/action.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ConnectorOutboundMessageIT extends AbstractIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RestTestClient apiClient;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_document_signatures");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_documents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_contents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_as4_properties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_messages");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_attachments");
        jdbcTemplate.execute("TRUNCATE TABLE connector_parties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_services");
        jdbcTemplate.execute("TRUNCATE TABLE connector_actions");
        jdbcTemplate.execute("TRUNCATE TABLE connector_processing_modes");
        jdbcTemplate.execute("TRUNCATE TABLE connector_keystores");
        jdbcTemplate.execute("TRUNCATE TABLE connector_business_domains");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    @Test
    void should_submit_rest_outbound_message_successfully() {
        var attachmentsId = uploadAttachment();
        var businessDocumentAttachmentId = uploadAttachment();

        var outboundMessageParts = new LinkedMultiValueMap<String, Object>();

        outboundMessageParts.add(
                "businessXMLDocument",
                produceBusinessXmlPart()
        );

        assert attachmentsId != null;

        var metadata = ConnectorOutboundMessageTestFixtures.produceOutboundMessageMetadata(
                List.of(attachmentsId),
                businessDocumentAttachmentId,
                true
        );

        outboundMessageParts.add("messageMetadata", produceMetadataAsPart(metadata));

        var response = postSuccessfulMessage(outboundMessageParts);

        assertThat(response).isNotNull();
    }

    @Test
    void should_submit_rest_outbound_message_without_attachment_successfully() {
        var businessDocumentAttachmentId = uploadAttachment();

        var outboundMessageParts = new LinkedMultiValueMap<String, Object>();
        outboundMessageParts.add(
                "businessXMLDocument",
                produceBusinessXmlPart()
        );

        var metadata = ConnectorOutboundMessageTestFixtures.produceOutboundMessageMetadata(
                null,
                businessDocumentAttachmentId,
                true
        );

        outboundMessageParts.add("messageMetadata", produceMetadataAsPart(metadata));

        var response = postSuccessfulMessage(outboundMessageParts);

        assertThat(response).isNotNull();
    }

    @Test
    void should_submit_rest_outbound_message_without_attachment_and_business_document_signature_successfully() {
        var businessDocumentAttachmentId = uploadAttachment();

        var outboundMessageParts = new LinkedMultiValueMap<String, Object>();
        outboundMessageParts.add(
                "businessXMLDocument",
                produceBusinessXmlPart()
        );

        var metadata = ConnectorOutboundMessageTestFixtures.produceOutboundMessageMetadata(
                null,
                businessDocumentAttachmentId,
                false
        );

        outboundMessageParts.add("messageMetadata", produceMetadataAsPart(metadata));

        var response = postSuccessfulMessage(outboundMessageParts);

        assertThat(response).isNotNull();
    }

    private HttpEntity<?> produceMetadataAsPart(ConnectorOutboundMessageRequest metadata) {
        var jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(objectMapper.writeValueAsString(metadata), jsonHeaders);
    }

    private  HttpEntity<ByteArrayResource> produceBusinessXmlPart() {
        return FilePartTestFixtures.filePart(
                "Form_A.xml",
                FileTestFixtures.readAsBytes("raw/Form_A.xml"),
                MediaType.APPLICATION_XML
        );
    }

    private ConnectorOutboundMessageDto postSuccessfulMessage(LinkedMultiValueMap<String, Object> body) {
        var response = apiClient.post()
                                .uri("/api/v1/messages/outbound")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(body)
                                .exchange()
                                .expectStatus().isCreated()
                                .returnResult(ConnectorOutboundMessageDto.class);

        return response.getResponseBody();
    }

    private String uploadAttachment() {
        var parts = produceAttachmentPart(MediaType.APPLICATION_PDF, 10);
        var identifiers = apiClient.post()
                                                     .uri("/api/v1/attachments/upload")
                                                     .contentType(
                                                             MediaType.MULTIPART_FORM_DATA)
                                                     .body(parts)
                                                     .exchange()
                                                     .expectStatus().isCreated()
                                                     .returnResult(String[].class)
                                                     .getResponseBody();

        assert identifiers != null;
        return identifiers[0];
    }
}
