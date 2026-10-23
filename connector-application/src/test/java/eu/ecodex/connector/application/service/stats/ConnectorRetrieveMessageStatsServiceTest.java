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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageStatsTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorMessageStatsRepository;
import eu.ecodex.connector.domain.model.filter.ConnectorDateRangeFilter;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRetrieveMessageStatsService")
public class ConnectorRetrieveMessageStatsServiceTest {
    @Mock
    private ConnectorMessageStatsRepository statsRepository;

    @InjectMocks
    private ConnectorRetrieveMessageStatsService service;

    private final ConnectorDateRangeFilter dateRange = ConnectorDateRangeFilter.of(
        Instant.now(), Instant.now()
    );

    @Test
    void should_retrieve_message_stats() {
        when(statsRepository.findAll(
            any(),
            any()
        )).thenReturn(MessageStatsTestFixtures.createStats());

        var stats = service.execute(dateRange, null);

        assertThat(stats).isNotNull();
        assertThat(stats.all()).isNotNull();
        assertThat(stats.inbound()).isNotNull();
        assertThat(stats.outbound()).isNotNull();
    }

    @Test
    void should_fail_when_date_range_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> service.execute(null, null)
        );
    }
}
