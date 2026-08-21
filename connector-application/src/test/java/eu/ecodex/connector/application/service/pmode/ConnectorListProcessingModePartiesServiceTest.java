/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.pmode;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorPartyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorListProcessingModePartiesService")
public class ConnectorListProcessingModePartiesServiceTest {
    @Mock
    private ConnectorPartyRepository partyRepository;

    @InjectMocks
    private ConnectorListProcessingModePartiesService listProcessingModePartiesService;

    @Test
    void should_return_all_the_parties() {
        when(partyRepository.findAllByBusinessDomainIdentifier(any()))
            .thenReturn(List.of(PartyTestFixtures.createToParty()));

        var parties = listProcessingModePartiesService.execute("default_business_domain");

        assertThat(parties).isNotNull();
        assertThat(parties).hasSize(1);
    }

    @Test
    void should_fail_when_the_business_domain_identifier_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> listProcessingModePartiesService.execute(null)
        );
    }
}
