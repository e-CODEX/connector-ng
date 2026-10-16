/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.transport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.dto.message.ConnectorMessageDetailDto;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRetrieveTransportStepByTransportIdIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRetrieveTransportStepByTransportIdIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/transport-steps/%s/message";

    @Autowired
    private RestTestClient apiClient;

    @Test
    @WithReferenceData
    void should_retrieve_transport_step() {
        var transportIdentifier = "8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice";
        var messageIdentifier = "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";

        apiClient
            .get()
            .uri(URL.formatted(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<ConnectorMessageDetailDto>() {
            })
            .value(message -> {
                assertThat(message).isNotNull();
                assert message != null;
                assertThat(message.identifier()).isEqualTo(messageIdentifier);
                assertThat(message.isBusiness()).isEqualTo(true);
            });
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_when_retrieving_non_existing_transport_step() {
        var transportIdentifier = "unknown";
        apiClient
            .get()
            .uri(URL.formatted(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isNotFound();
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
