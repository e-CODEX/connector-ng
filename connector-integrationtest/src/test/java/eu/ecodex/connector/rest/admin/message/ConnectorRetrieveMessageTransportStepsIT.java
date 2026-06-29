/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.message;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRetrieveMessageTransportStepsIT extends AbstractIntegrationTest {
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
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/attachment.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql"
    })
    void should_retrieve_a_connector_messages_transport_steps_successfully() {
        var messageId = "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";
        apiClient.get()
                 .uri(buildUrl(messageId))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorMessageTransportStepDto>() {
                 })
                 .value(step -> {
                     assertThat(step).isNotNull();
                     assert step != null;
                     assertThat(step.identifier()).isNotNull();
                     assertThat(step.transportedMessageIdentifier()).isEqualTo(messageId);
                     assertThat(step.numberOfAttempts()).isEqualTo(1);
                     assertThat(step.status()).isEqualTo("SUBMITTED");
                     assertThat(step.messageType()).isEqualTo("EVIDENCE");
                 });
    }

    @Test
    void should_throw_404_not_found_when_retrieving_a_non_existing_connector_message_transport_steps() {
        var messageId = "5410e2a3-be9a-4598-99b3-21846233c67e@connector.ecodex.eu";
        apiClient.get()
                 .uri(buildUrl(messageId))
                 .exchange()
                 .expectStatus().isNotFound();
    }

    private String buildUrl(String identifier) {
        return "/api/v1/admin/messages/%s/transport-steps".formatted(identifier);
    }
}
