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

import eu.ecodex.connector.MessageReportTestFixtures;
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
@DisplayName("ConnectorRetrieveMessageReportService")
public class ConnectorRetrieveMessageReportServiceTest {
    private final ConnectorDateRangeFilter dateRange = ConnectorDateRangeFilter.of(
        Instant.now(), Instant.now()
    );

    @Mock
    private ConnectorMessageStatsRepository statsRepository;

    @InjectMocks
    private ConnectorRetrieveMessageReportService service;

    @Test
    void should_retrieve_message_report() {
        when(statsRepository.computeReport(
            any(),
            any()
        )).thenReturn(MessageReportTestFixtures.createReport());

        var summary = service.execute(dateRange, null);

        assertThat(summary).isNotNull();
        assertThat(summary.services()).hasSize(2);
        assertThat(summary.parties()).hasSize(1);
        assertThat(summary.months()).hasSize(2);
        assertThat(summary.years()).hasSize(1);
    }

    @Test
    void should_fail_when_date_range_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> service.execute(null, null)
        );
    }
}
