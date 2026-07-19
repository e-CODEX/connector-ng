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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageStatsTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorMessageStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorRetrieveMessageStatsServiceTest {
    @Mock
    private ConnectorMessageStatsRepository statsRepository;

    @InjectMocks
    private ConnectorRetrieveMessageStatsService service;

    @Test
    void should_retrieve_message_stats_successfully() {
        when(statsRepository.findAll(
            any(),
            any()
        )).thenReturn(MessageStatsTestFixtures.createStats());

        var stats = service.execute(any(), any());

        assertThat(stats).isNotNull();
        assertThat(stats.all()).isNotNull();
        assertThat(stats.inbound()).isNotNull();
        assertThat(stats.outbound()).isNotNull();
    }
}
