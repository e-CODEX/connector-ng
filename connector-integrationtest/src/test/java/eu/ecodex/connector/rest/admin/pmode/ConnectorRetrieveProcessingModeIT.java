/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.pmode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDetailDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRetrieveProcessingModeIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRetrieveProcessingModeIT extends AbstractIntegrationTest {
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
        "classpath:sql/action.sql"
    })
    void should_retrieve_connector_pmode() {
        var uuid = "4f10aed9-2e5f-4780-87f7-5fe1070d5ccf";
        apiClient.get()
                 .uri("/api/v1/admin/processing-modes/" + uuid)
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorProcessingModeDetailDto>() {
                 })
                 .value(pmode -> {
                     assertThat(pmode).isNotNull();
                     assert pmode != null;
                     assertThat(pmode.uuid()).isEqualTo(uuid);
                     assertThat(pmode.description()).isNotNull();
                     assertThat(pmode.content()).isNotEmpty();
                     assertThat(pmode.parties()).isNotNull();
                     assertThat(pmode.services()).isNotNull();
                     assertThat(pmode.actions()).isNotNull();
                 });
    }

    @Test
    void should_return_404_when_retrieving_non_existing_pmode() {
        var uuid = "ccafa470-c32b-4d69-be24-dbbf1b9fcad1";
        apiClient.get()
                 .uri("/api/v1/admin/processing-modes/" + uuid)
                 .exchange()
                 .expectStatus().isNotFound();
    }
}
