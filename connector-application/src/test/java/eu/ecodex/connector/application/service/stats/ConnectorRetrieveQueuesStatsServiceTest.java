/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.stats;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.QueuesStatsTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorQueueStatsProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveQueuesStatsServiceTest {
    @Mock
    private ConnectorQueueStatsProvider queueStatsProvider;

    @InjectMocks
    private ConnectorRetrieveQueuesStatsService service;

    @Test
    void should_retrieve_queues_stats_successfully() {
        when(queueStatsProvider.getAllStats()).thenReturn(List.of(QueuesStatsTestFixtures.create()));

        var stats = service.execute();

        assertThat(stats).isNotEmpty();
        assertThat(stats.size()).isEqualTo(1);
        var statsEntry = stats.getFirst();
        assertThat(statsEntry.queueName())
            .isEqualTo("connector.queues.outbound-message-staging-queue");
        assertThat(statsEntry.queueDescription()).isEqualTo(
            "Staging area for outbound messages awaiting processing");
        assertThat(statsEntry.pendingCount()).isEqualTo(0);
        assertThat(statsEntry.dlqCount()).isEqualTo(1);
    }
}
