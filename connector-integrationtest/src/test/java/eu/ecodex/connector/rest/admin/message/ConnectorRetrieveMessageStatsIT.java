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
import eu.ecodex.connector.domain.model.stats.ConnectorMessageStats;
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
public class ConnectorRetrieveMessageStatsIT extends AbstractIntegrationTest {
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
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
    })
    void should_retrieve_connector_messages_stats_successfully() {
        apiClient.get()
                 .uri("/api/v1/admin/messages/stats")
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<ConnectorMessageStats>() {
                 })
                 .value(stats -> {
                     assertThat(stats).isNotNull();
                     assert stats != null;
                     assertThat(stats.all()).isNotNull();
                     assertThat(stats.all().total()).isEqualTo(4);
                     assertThat(stats.all().delivered()).isEqualTo(0);
                     assertThat(stats.all().rejected()).isEqualTo(0);
                     assertThat(stats.all().pending()).isEqualTo(4);

                     assertThat(stats.outbound()).isNotNull();
                     assertThat(stats.outbound().total()).isEqualTo(1);
                     assertThat(stats.outbound().delivered()).isEqualTo(0);
                     assertThat(stats.outbound().rejected()).isEqualTo(0);
                     assertThat(stats.outbound().pending()).isEqualTo(1);

                     assertThat(stats.inbound()).isNotNull();
                     assertThat(stats.inbound().total()).isEqualTo(3);
                     assertThat(stats.inbound().delivered()).isEqualTo(0);
                     assertThat(stats.inbound().rejected()).isEqualTo(0);
                     assertThat(stats.inbound().pending()).isEqualTo(3);
                 });
    }
}
