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
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.infrastructure.inbound.web.rest.advice.ErrorResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageIdentifiers;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.evidence.ConnectorEvidenceTriggerMessageRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorEvidenceTriggerMessageIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorEvidenceTriggerMessageIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/messages/evidence-trigger";
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    private ConnectorEvidenceTriggerMessageRequest validRequest() {
        return ConnectorEvidenceTriggerMessageRequest
            .builder()
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .identifiers(ConnectorEvidenceTriggerMessageIdentifiers
                             .builder()
                             .referenceToIdentifier(null)
                             .backendMessageIdentifier("1f30e203-f89c-4568-a076-469c4f8b35a5")
                             .build())
            .build();
    }

    /**
     * Reference data (business domain, processing mode, party, service, action).
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
        "classpath:sql/service.sql",
        "classpath:sql/action.sql",
    })
    private @interface WithReferenceData {
    }

    /**
     * Reference data plus a fully seeded business message and its evidence.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
        "classpath:sql/service.sql",
        "classpath:sql/action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/evidence.sql",
    })
    private @interface WithMessageData {
    }

    @Nested
    @DisplayName("POST (submit an evidence trigger message)")
    class SubmitEvidenceTrigger {
        @Test
        @WithMessageData
        void should_return_201_when_the_trigger_is_submitted() {
            apiClient.post()
                     .uri(URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(validRequest())
                     .exchange()
                     .expectStatus().isCreated()
                     .expectBody(ConnectorOutboundMessageDto.class)
                     .value(response -> {
                         assert response != null;
                         assertThat(response.identifier()).isNotBlank();
                     });
        }

        @Test
        @WithReferenceData
        void should_return_404_when_the_related_business_message_is_not_found() {
            apiClient.post()
                     .uri(URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(validRequest())
                     .exchange()
                     .expectStatus().is4xxClientError()
                     .expectBody(ErrorResponse.class)
                     .value(response -> {
                         assert response != null;
                         assertThat(response.status()).isEqualTo(404);
                     });
        }

        @Test
        void should_return_400_when_the_payload_is_invalid() {
            apiClient.post()
                     .uri(URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(validRequest().toBuilder().evidenceType(null).build())
                     .exchange()
                     .expectStatus().isBadRequest();
        }
    }
}
