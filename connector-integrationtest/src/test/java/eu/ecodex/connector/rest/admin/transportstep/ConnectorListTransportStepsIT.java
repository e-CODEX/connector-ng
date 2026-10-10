/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.transportstep;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

@DisplayName("ConnectorListTransportStepsIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorListTransportStepsIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/transport-steps";
    private static final String MESSAGE_ID = "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";
    private static final String REMOTE_SYSTEM_ID = "6e3320bb-6724-4387-822c-a2914dba559a";
    private static final String BACKEND_NAME = "backend_alice";
    private static final Instant NOW = Instant.now();

    @Autowired
    private RestTestClient apiClient;

    static Stream<Arguments> transportStepFilters() {
        return Stream.of(
            arguments("no filter", Map.of(), 3),
            arguments(
                "date range", Map.of(
                    "from", NOW.minus(1, ChronoUnit.HOURS).toString(),
                    "to", NOW.plus(1, ChronoUnit.HOURS).toString()
                ), 3
            ),
            arguments(
                "transported message identifier",
                Map.of("messageOrRemoteSystemIdentifier", MESSAGE_ID), 1
            ),
            arguments(
                "remote system identifier",
                Map.of("messageOrRemoteSystemIdentifier", REMOTE_SYSTEM_ID), 1
            ),
            arguments(
                "message identifier + backend name + from + to",
                Map.of(
                    "messageOrRemoteSystemIdentifier", MESSAGE_ID,
                    "linkPartnerName", BACKEND_NAME,
                    "from", NOW.minus(1, ChronoUnit.HOURS).toString(),
                    "to", NOW.plus(1, ChronoUnit.HOURS).toString()
                ), 1
            ),
            arguments(
                "remote system identifier + backend name",
                Map.of(
                    "messageOrRemoteSystemIdentifier", REMOTE_SYSTEM_ID,
                    "linkPartnerName", BACKEND_NAME
                ), 1
            ),

            arguments("status SUBMITTED", Map.of("statuses", "SUBMITTED"), 1),
            arguments("status DOWNLOADED", Map.of("statuses", "DOWNLOADED"), 1),
            arguments(
                "status READY_FOR_DOWNLOAD",
                Map.of("statuses", "READY_FOR_DOWNLOAD"),
                1
            ),
            arguments(
                "several statuses (union)",
                Map.of("statuses", "SUBMITTED,DOWNLOADED"), 2
            ),
            arguments(
                "identifier + matching status",
                Map.of(
                    "messageOrRemoteSystemIdentifier", MESSAGE_ID,
                    "statuses", "SUBMITTED"
                ), 1
            ),

            arguments(
                "status DELIVERED (none in seed)",
                Map.of("statuses", "DELIVERED"),
                0
            ),
            arguments("status FAILED (none in seed)", Map.of("statuses", "FAILED"), 0),
            arguments(
                "several statuses, none matching",
                Map.of("statuses", "FAILED,DELIVERED"), 0
            ),
            arguments(
                "identifier + non-matching status",
                Map.of(
                    "messageOrRemoteSystemIdentifier", MESSAGE_ID,
                    "statuses", "DELIVERED"
                ), 0
            )
        );
    }

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("transportStepFilters")
    @WithReferenceData
    void should_list_transport_steps_with_filters(
        String description,
        Map<String, String> queryParams,
        int expectedSize) {

        var expectedPages = expectedSize == 0 ? 0 : 1;

        var uriBuilder = UriComponentsBuilder.fromPath(URL);
        queryParams.forEach(uriBuilder::queryParam);

        apiClient.get()
                 .uri(uriBuilder.build().toUri())
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorPageResult<ConnectorMessageTransportStepDto>>() {
                 })
                 .value(result -> assertThat(result)
                     .isNotNull()
                     .satisfies(r -> {
                         assertThat(r.content().size()).isEqualTo(expectedSize);
                         assertThat(r.size()).isEqualTo(expectedSize);
                         assertThat(r.totalElements()).isEqualTo(expectedSize);
                         assertThat(r.totalPages()).isEqualTo(expectedPages);
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
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
        "classpath:sql/user.sql"
    })
    private @interface WithReferenceData {
    }
}
