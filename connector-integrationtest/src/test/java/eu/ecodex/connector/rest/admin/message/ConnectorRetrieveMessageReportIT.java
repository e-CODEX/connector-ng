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
import eu.ecodex.connector.domain.model.stats.report.summary.ConnectorMessageReportSummary;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("ConnectorRetrieveMessageReportIT REST")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorRetrieveMessageReportIT extends AbstractIntegrationTest {
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
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/user.sql",
    })
    void should_retrieve_report_of_connector_messages() {
        apiClient.get()
            .uri("/api/v1/admin/messages/reports")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateDefaultAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<ConnectorMessageReportSummary>() {
            })
            .value(reports -> {
                assertThat(reports).isNotNull();
                assert reports != null;
                assertThat(reports.parties().size()).isEqualTo(2);
                assertThat(reports.parties().containsAll(List.of("RE", "BL")));
                assertThat(reports.services().size()).isEqualTo(1);
                assertThat(reports.months().size()).isEqualTo(1);
                assertThat(reports.years().size()).isEqualTo(1);

                var year1 = reports.years().getFirst();
                assertThat(year1).isNotNull();
                assertThat(year1.year()).isNotNull();
                assertThat(year1.months().size()).isEqualTo(1);

                var month1 = year1.months().getFirst();
                assertThat(month1).isNotNull();
                assertThat(month1.month()).isNotNull();
                assertThat(month1.totalInbound()).isEqualTo(3);
                assertThat(month1.totalOutbound()).isEqualTo(1);
                assertThat(month1.total())
                    .isEqualTo(month1.totalOutbound() + month1.totalInbound());


                assertThat(month1.reports().size()).isEqualTo(2);
                var monthReport1 = month1.reports().getFirst();
                assertThat(monthReport1).isNotNull();
                assertThat(monthReport1.party()).isEqualTo("BL");
                assertThat(monthReport1.service()).isEqualTo("Connector-TEST");
                assertThat(monthReport1.inbound()).isEqualTo(3);
                assertThat(monthReport1.outbound()).isEqualTo(0);
                assertThat(monthReport1.total())
                    .isEqualTo(monthReport1.inbound() + monthReport1.outbound());

                var monthReport2 = month1.reports().get(1);
                assertThat(monthReport2).isNotNull();
                assertThat(monthReport2.party()).isEqualTo("RE");
                assertThat(monthReport2.service()).isEqualTo("Connector-TEST");
                assertThat(monthReport2.inbound()).isEqualTo(0);
                assertThat(monthReport2.outbound()).isEqualTo(1);
                assertThat(monthReport2.total())
                    .isEqualTo(monthReport2.inbound() + monthReport2.outbound());
            });
    }
}
