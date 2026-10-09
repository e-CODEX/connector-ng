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
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private RestTestClient apiClient;

    static Stream<Arguments> transportStepFilters() {
        var none = TransportStepFilter.none();
        return Stream.of(
            arguments("no filter", none, 3, 1),
            arguments("transported message identifier", none.withIdentifier(MESSAGE_ID), 1, 1),
            arguments("remote system identifier", none.withIdentifier(REMOTE_SYSTEM_ID), 1, 1),
            arguments(
                "message identifier + backend name",
                none.withIdentifier(MESSAGE_ID).withLinkPartnerName(BACKEND_NAME), 1, 1
            ),
            arguments(
                "remote system identifier + backend name",
                none.withIdentifier(REMOTE_SYSTEM_ID).withLinkPartnerName(BACKEND_NAME), 1, 1
            ),

            arguments("status DELIVERED", none.withStatuses("DELIVERED"), 0, 0),
            arguments("status FAILED", none.withStatuses("FAILED"), 0, 0),
            arguments("status SUBMITTED", none.withStatuses("SUBMITTED"), 1, 1),
            arguments("status DOWNLOADED", none.withStatuses("DOWNLOADED"), 1, 1),
            arguments("status READY_FOR_DOWNLOAD", none.withStatuses("READY_FOR_DOWNLOAD"), 1, 1),
            arguments("several statuses", none.withStatuses("FAILED", "DELIVERED"), 0, 0),
            arguments(
                "identifier + status",
                none.withIdentifier(MESSAGE_ID).withStatuses("DELIVERED"), 0, 0
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
        TransportStepFilter filter,
        int expectedSize, int totalPages) {

        var uri = UriComponentsBuilder
            .fromPath(URL)
            .queryParamIfPresent(
                "messageOrRemoteSystemIdentifier",
                Optional.ofNullable(filter.identifier())
            )
            .queryParamIfPresent("linkPartnerName", Optional.ofNullable(filter.linkPartnerName()))
            .queryParamIfPresent("statuses", Optional.ofNullable(filter.statuses()))
            .build()
            .toUri();

        apiClient.get()
                 .uri(uri)
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
                         assertThat(r.totalPages()).isEqualTo(totalPages);
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

    record TransportStepFilter(String identifier, String linkPartnerName, List<String> statuses) {
        static TransportStepFilter none() {
            return new TransportStepFilter(null, null, null);
        }

        TransportStepFilter withIdentifier(String v) {
            return new TransportStepFilter(v, linkPartnerName, statuses);
        }

        TransportStepFilter withLinkPartnerName(String v) {
            return new TransportStepFilter(identifier, v, statuses);
        }

        TransportStepFilter withStatuses(String... v) {
            return new TransportStepFilter(identifier, linkPartnerName, List.of(v));
        }
    }
}
