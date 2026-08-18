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
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorListMessagesIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorListMessagesIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/messages";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @WithMessageData
    void should_list_connector_messages() {
        apiClient.get()
            .uri(URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
            })
            .value(result -> {
                assertThat(result).isNotNull();
                assert result != null;
                assertThat(result.content().size()).isEqualTo(4);
                assertThat(result.size()).isEqualTo(4);
                assertThat(result.totalElements()).isEqualTo(4);
                assertThat(result.totalPages()).isEqualTo(1);
            });
    }

    @ParameterizedTest
    @CsvSource({
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", // identifier
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", // backendMessageIdentifier
        "9085a015-06f3-4631-96e6-55a216e900ff", // conversationIdentifier
    })
    @WithMessageData
    void should_list_connector_messages_filtered_by_identifiers(String identifier) {
        apiClient.get()
            .uri(String.format("%s?identifier=%s", URL, identifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
            })
            .value(result -> {
                assertThat(result).isNotNull();
                assert result != null;
                assertThat(result.content().size()).isEqualTo(1);
                assertThat(result.size()).isEqualTo(1);
                assertThat(result.totalElements()).isEqualTo(1);
                assertThat(result.totalPages()).isEqualTo(1);
            });
    }

    @ParameterizedTest
    @CsvSource({
        // identifier
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu,backend_alice,"
            + "default_business_domain,Connector-TEST,Test_Form",
        // backendMessageIdentifier
        "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu,backend_alice,"
            + "default_business_domain,Connector-TEST,Test_Form",
        // conversationIdentifier
        "9085a015-06f3-4631-96e6-55a216e900ff,backend_alice,default_business_domain,"
            + "Connector-TEST,Test_Form",
    })
    @WithMessageData
    void should_list_connector_messages_matching_identifier_backend_name_and_business_domain_filters(
        String identifier,
        String backendName,
        String businessDomain,
        String service,
        String action) {
        apiClient.get()
            .uri(String.format(
                "%s?identifier=%s&backendName=%s&businessDomain=%s&service=%s&action=%s",
                URL,
                identifier,
                backendName,
                businessDomain,
                service,
                action
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
            })
            .value(result -> {
                assertThat(result).isNotNull();
                assert result != null;
                assertThat(result.content().size()).isEqualTo(1);
                assertThat(result.size()).isEqualTo(1);
                assertThat(result.totalElements()).isEqualTo(1);
                assertThat(result.totalPages()).isEqualTo(1);
            });
    }

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
        "classpath:sql/user.sql",
    })
    private @interface WithMessageData {
    }
}
