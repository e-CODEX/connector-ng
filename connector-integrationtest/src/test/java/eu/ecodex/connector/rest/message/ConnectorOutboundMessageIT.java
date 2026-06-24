/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.FilePartTestFixtures;
import eu.ecodex.connector.FileTestFixtures;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorOutboundMessageIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql"
    })
    void should_submit_rest_outbound_message_successfully() {
        var attachmentId = uploadAttachment();
        var parts = buildOutboundMessageParts(List.of(attachmentId), true);
        var response = postSuccessfulMessage(parts);

        assertThat(response).isNotNull();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql"
    })
    void should_submit_rest_outbound_message_without_attachment_successfully() {
        var parts = buildOutboundMessageParts(null, true);

        var response = postSuccessfulMessage(parts);
        assertThat(response).isNotNull();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql"
    })
    void should_submit_rest_outbound_message_without_detached_signature_successfully() {
        var parts = buildOutboundMessageParts(null, false);

        var response = postSuccessfulMessage(parts);
        assertThat(response).isNotNull();
    }

    private LinkedMultiValueMap<String, Object> buildOutboundMessageParts(
            List<String> attachmentIds,
            boolean withDetachedSignature
    ) {
        var parts = new LinkedMultiValueMap<String, Object>();

        parts.add("businessDomainIdentifier", "default_business_domain");
        parts.add(
                "backendMessageIdentifier",
                "56ed1a8f-b089-4615-9ddd-da302a665a11@backend_system"
        );

        parts.add(
                "businessContent.contentFile",
                FilePartTestFixtures.filePart(
                        "Form_A.xml",
                        FileTestFixtures.readAsBytes("raw/Form_A.xml"),
                        MediaType.APPLICATION_XML
                )
        );

        parts.add(
                "businessContent.businessDocument.document",
                FilePartTestFixtures.filePart(
                        "test-pdf.pdf",
                        FileTestFixtures.readAsBytes("raw/test-pdf.pdf"),
                        MediaType.APPLICATION_PDF
                )
        );

        parts.add(
                "businessContent.businessDocument.aesType",
                "SIGNATURE_BASED"
        );

        // detached signature (optional)
        if (withDetachedSignature) {
            parts.add(
                    "businessContent.businessDocument.detachedSignature.signature",
                    FilePartTestFixtures.filePart(
                            "DetachedNonSigned.xml",
                            FileTestFixtures.readAsBytes("raw/signature/DetachedNonSigned.xml"),
                            MediaType.APPLICATION_XML
                    )
            );
            parts.add(
                    "businessContent.businessDocument.detachedSignature.mimeType",
                    "XML"
            );
        }

        // AS4 properties
        parts.add(
                "as4Properties.conversationIdentifier",
                "e6a173ec-de21-46dc-8a19-63a6cb74915d"
        );
        parts.add("as4Properties.originalSender", "alice");
        parts.add("as4Properties.finalRecipient", "bob");
        parts.add("as4Properties.service.name", "Connector-TEST");
        parts.add("as4Properties.service.type", "urn:e-codex:services:");
        parts.add("as4Properties.action.name", "ConTest_Form");
        parts.add("as4Properties.fromParty.identifier", "BL");
        parts.add(
                "as4Properties.fromParty.identifierType",
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
        );
        parts.add("as4Properties.fromParty.role", "GW");
        parts.add("as4Properties.toParty.identifier", "RE");
        parts.add(
                "as4Properties.toParty.identifierType",
                "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
        );
        parts.add("as4Properties.toParty.role", "GW");

        // optional attachments list
        if (attachmentIds != null) {
            attachmentIds.forEach(id -> parts.add("attachments", id));
        }

        return parts;
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
