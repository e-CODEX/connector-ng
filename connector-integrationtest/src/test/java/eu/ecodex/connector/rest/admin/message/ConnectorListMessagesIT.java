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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@DisplayName("ConnectorListMessagesIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorListMessagesIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/messages";
    private static final String MESSAGE_ID = "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";
    private static final String CONVERSATION_ID = "9085a015-06f3-4631-96e6-55a216e900ff";
    private static final Instant NOW = Instant.now();

    @Autowired
    private RestTestClient apiClient;

    static Stream<Arguments> messageFilters() {
        return Stream.of(
            arguments("no filter", Map.of(), 4),
            arguments(
                "date range", Map.of(
                    "from", NOW.minus(1, ChronoUnit.HOURS).toString(),
                    "to", NOW.plus(1, ChronoUnit.HOURS).toString()
                ), 4
            ),
            arguments("message identifier", Map.of("identifier", MESSAGE_ID), 1),
            arguments("conversation identifier", Map.of("identifier", CONVERSATION_ID), 1),
            arguments(
                "message identifier + backend, direction, domain, service, action, from, to",
                Map.of(
                    "identifier", MESSAGE_ID,
                    "backendName", "backend_alice",
                    "direction", "BACKEND_TO_GATEWAY",
                    "businessDomain", "default_business_domain",
                    "service", "Connector-TEST",
                    "action", "Test_Form",
                    "from", NOW.minus(1, ChronoUnit.HOURS).toString(),
                    "to", NOW.plus(1, ChronoUnit.HOURS).toString()
                ),
                1
            ),
            arguments(
                "conversation identifier + backend, direction, domain, service, action",
                Map.of(
                    "identifier", CONVERSATION_ID,
                    "backendName", "backend_alice",
                    "direction", "GATEWAY_TO_BACKEND",
                    "businessDomain", "default_business_domain",
                    "service", "Connector-TEST",
                    "action", "Test_Form"
                ),
                1
            )
        );
    }

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("messageFilters")
    @WithMessageData
    void should_list_connector_messages(
        String description,
        Map<String, String> queryParams,
        int expectedSize) {

        var uriBuilder = UriComponentsBuilder.fromPath(URL);
        queryParams.forEach(uriBuilder::queryParam);

        apiClient.get()
                 .uri(uriBuilder.build().toUri())
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageDto>>() {
                 })
                 .value(result -> assertThat(result)
                     .isNotNull()
                     .satisfies(r -> {
                         assertThat(r.content().size()).isEqualTo(expectedSize);
                         assertThat(r.size()).isEqualTo(expectedSize);
                         assertThat(r.totalElements()).isEqualTo(expectedSize);
                         assertThat(r.totalPages()).isEqualTo(1);
                     }));
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
