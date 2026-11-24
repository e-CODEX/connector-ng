/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.evidence;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRetrieveEvidenceIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorEvidenceDownloadIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/admin/evidences/{uuid}/download";
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
        "classpath:sql/processing-mode-party.sql",
        "classpath:sql/processing-mode-service.sql",
        "classpath:sql/processing-mode-action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/evidence.sql",
        "classpath:sql/user.sql"
    })
    void should_let_an_admin_reach_the_evidence_download() {
        byte[] body = apiClient.get()
                               .uri(URL, "f6cb9e83-4283-4255-8bbf-9cb0920fc1ef")
                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                               .exchange()
                               .expectStatus().isOk()
                               .expectHeader().contentType(MediaType.APPLICATION_XML)
                               .expectBody(byte[].class)
                               .returnResult()
                               .getResponseBody();

        assertThat(body).isNotNull().isNotEmpty();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_return_404_when_evidence_does_not_exist() {
        apiClient.get()
                 .uri(URL, "f6cb9e83-4283-4255-8bbf-9cb0920fc1ef")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
                 .exchange()
                 .expectStatus().isNotFound();
    }

    @Test
    @Sql({"classpath:sql/user.sql"})
    void should_forbid_a_user_without_the_admin_role_to_download_an_evidence() {
        apiClient.get()
                 .uri(URL, "unknown-evidence")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultSimpleUserToken())
                 .exchange()
                 .expectStatus()
                 .isForbidden();
    }

    @Test
    void should_reject_an_unauthenticated_evidence_download() {
        apiClient.get()
                 .uri(URL, "unknown-evidence")
                 .exchange()
                 .expectStatus()
                 .isUnauthorized();
    }
}
