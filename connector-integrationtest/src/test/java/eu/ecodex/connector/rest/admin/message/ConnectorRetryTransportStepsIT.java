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

import static eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus.READY_FOR_PULL;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.domain.model.user.ConnectorRoleName;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.login.ConnectorLoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRetryTransportStepsIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRetryTransportStepsIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @Autowired
    private ConnectorUserRepository userRepository;

    @Autowired
    private ConnectorMessageTransportStepRepository transportStepRepository;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql("classpath:sql/user.sql")
    void should_failed_when_unauthorized_user_retry_transport_step() {
        var username = "test-user-it";
        var loginRequest = ConnectorLoginRequest.builder()
            .username(username)
            .password("password")
            .build();

        var existing = userRepository.findByUsername(username);
        assertThat(existing).isNotEmpty();
        assertThat(existing.get().roleNames()).doesNotContain(ConnectorRoleName.ADMIN.name());

        var loginResponse = apiClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getResponseBody()).isNotNull();
        var accessToken = loginResponse.getResponseBody().accessToken();

        var transportIdentifier =
            "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";
        apiClient.post()
            .uri(buildUrl(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .exchange()
            .expectStatus()
            .isForbidden();
    }

    @Test
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/processing-mode-party.sql",
        "classpath:sql/processing-mode-service.sql",
        "classpath:sql/processing-mode-action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
        "classpath:sql/user.sql",
    })
    void should_retry_transport_step_when_is_retriable() {
        // Given
        var transportIdentifier =
            "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";
        var transportStep = transportStepRepository.findByIdentifier(transportIdentifier);
        assertThat(transportStep).isNotNull();
        assertThat(transportStep.status()).isEqualTo(READY_FOR_PULL);
        assertThat(transportStep.numberOfAttempts()).isOne();

        apiClient.post()
            .uri(buildUrl(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus()
            .isOk();
    }

    @Test
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/processing-mode-party.sql",
        "classpath:sql/processing-mode-service.sql",
        "classpath:sql/processing-mode-action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
        "classpath:sql/user.sql",
    })
    void should_not_retry_transport_step_when_is_not_retriable() {
        var transportIdentifier =
            "8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice";
        apiClient.post()
            .uri(buildUrl(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isEqualTo(409);
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_when_retrieving_transport_steps_for_non_existing_connector_message() {
        var transportIdentifier = "unknownTransportIdentifier@connector.ecodex.eu";
        apiClient.post()
            .uri(buildUrl(transportIdentifier))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isNotFound();
    }

    private String buildUrl(String identifier) {
        return "/api/v1/admin/transport-steps/%s/retry".formatted(identifier);
    }
}
