/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.businessdomain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.JsonTestFixtures;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorBusinessDomainDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRegisterBusinessDomainIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/business-domains";
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    void should_register_business_domain_successfully() {
        var body = JsonTestFixtures.readJson("json/business-domain.creation.json");
        var response = apiClient.post()
                                .uri(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(body)
                                .exchange()
                                .expectStatus().isCreated()
                                .returnResult(ConnectorBusinessDomainDto.class);

        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assert responseBody != null;
        assertThat(responseBody.enabled()).isTrue();
        assertThat(responseBody.identifier())
                .isEqualTo("default_business_domain");
        assertThat(responseBody.source())
                .isEqualTo(ConnectorConfigurationSource.IMPLEMENTATION);
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_fail_to_register_business_domain_with_existing_identifier() {
        var body = JsonTestFixtures.readJson("json/business-domain.creation.json");
        apiClient.post()
                 .uri(URL)
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(body)
                 .exchange()
                 .expectStatus().is4xxClientError();
    }

    @Test
    @Sql("classpath:sql/business-domain.sql")
    void should_retrieve_business_domains_successfully() {
        var response = apiClient.get()
                                .uri(URL)
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(ConnectorBusinessDomainDto[].class);

        var responseBody = response.getResponseBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).hasSize(1);
    }
}
