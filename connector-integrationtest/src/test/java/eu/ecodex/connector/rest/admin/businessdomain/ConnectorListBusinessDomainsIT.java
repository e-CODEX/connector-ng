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
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorBusinessDomainDto;
import java.util.List;
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
public class ConnectorListBusinessDomainsIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
    })
    void should_list_connector_messages_successfully() {
        apiClient.get()
                 .uri("/api/v1/admin/business-domains")
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<List<ConnectorBusinessDomainDto>>() {
                 })
                 .value(businessDomains -> {
                     assertThat(businessDomains).isNotNull();
                     assert businessDomains != null;
                     assertThat(businessDomains.size()).isEqualTo(1);
                 });
    }
}
