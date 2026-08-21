/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.admin.jms;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.model.stats.queue.ConnectorQueueStats;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

@DisplayName("RetrieveQueuesStatsIT REST")
public class RetrieveQueuesStatsIT extends AbstractIntegrationTest {
    @Autowired
    private RestTestClient apiClient;

    @Test
    void should_retrieve_jms_queue_statistics() {
        apiClient.get()
                 .uri("/api/v1/admin/jms/queues/stats")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(new ParameterizedTypeReference<List<ConnectorQueueStats>>() {
                 })
                 .value(result -> {
                     assertThat(result).isNotNull();
                     assert result != null;
                     assertThat(result.size()).isEqualTo(8);
                 });
    }
}
