/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.link;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorListLinkPartnersIT extends AbstractIntegrationTest {
    private static final String URL = "/api/v1/link-partners";

    @Autowired
    private RestTestClient apiClient;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    void should_list_the_connector_link_partners_successfully() {
        var response = apiClient.get()
                                .uri(URL)
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(ConnectorLinkPartner[].class);

        var linkPartners = response.getResponseBody();
        assertThat(linkPartners).isNotNull();
        assertThat(linkPartners).hasSize(2);
    }

    @Test
    void should_list_the_connector_link_partners_based_on_filtering_successfully() {
        var response = apiClient.get()
                                .uri(URL.concat("?linkType=BACKEND"))
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(ConnectorLinkPartner[].class);

        var linkPartners = response.getResponseBody();
        assertThat(linkPartners).isNotNull();
        assertThat(linkPartners).hasSize(1);

        assert linkPartners != null;
        var first = Arrays.stream(linkPartners).findFirst();
        first.ifPresent(partner -> assertThat(partner.type()).isEqualTo(ConnectorLinkType.BACKEND));
    }
}
