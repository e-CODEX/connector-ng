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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:sql/business-domain.sql",
                "classpath:sql/processing-mode.sql",
                "classpath:sql/party.sql",
                "classpath:sql/service.sql",
                "classpath:sql/action.sql",
                "classpath:sql/message.sql",
                "classpath:sql/message-as4-properties.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ConnectorListMessagesIT extends AbstractIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_step_statuses");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_steps");
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
    void should_list_connector_messages_successfully() {
        apiClient.get()
                .uri("/api/v1/messages")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
                })
                .value(result -> {
                    assertThat(result).isNotNull();
                    assert result != null;
                    assertThat(result.content().size()).isEqualTo(4);
                    assertThat(result.totalElements()).isEqualTo(4);
                    assertThat(result.page()).isEqualTo(0);
                    assertThat(result.size()).isEqualTo(20);
                });
    }

    @ParameterizedTest
    @CsvSource({
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", // identifier
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", // backendMessageIdentifier
            "9085a015-06f3-4631-96e6-55a216e900ff", // conversationIdentifier
    })
    void should_list_connector_messages_applying_identifier_filter_successfully(String identifier) {
        apiClient.get()
                .uri(String.format("/api/v1/messages?identifier=%s", identifier))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
                })
                .value(result -> {
                    assertThat(result).isNotNull();
                    assert result != null;
                    assertThat(result.content().size()).isEqualTo(1);
                    assertThat(result.totalElements()).isEqualTo(1);
                    assertThat(result.page()).isEqualTo(0);
                    assertThat(result.size()).isEqualTo(20);
                });
    }

    @ParameterizedTest
    @CsvSource({
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu,backend_alice", // identifier
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu,backend_alice", // backendMessageIdentifier
            "9085a015-06f3-4631-96e6-55a216e900ff,backend_alice", // conversationIdentifier
    })
    void should_list_connector_messages_applying_identifier_and_backend_name_filter_successfully(String identifier, String backendName) {
        apiClient.get()
                .uri(String.format("/api/v1/messages?identifier=%s&backendName=%s", identifier, backendName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
                })
                .value(result -> {
                    assertThat(result).isNotNull();
                    assert result != null;
                    assertThat(result.content().size()).isEqualTo(1);
                    assertThat(result.totalElements()).isEqualTo(1);
                    assertThat(result.page()).isEqualTo(0);
                    assertThat(result.size()).isEqualTo(20);
                });
    }
}
