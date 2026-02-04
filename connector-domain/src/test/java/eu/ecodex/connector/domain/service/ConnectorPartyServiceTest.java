/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.api.service.ConnectorPartyService;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.utils.BusinessDomainUtil;
import eu.ecodex.connector.utils.PartyUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit tests for the {@code ConnectorPartyService} implementation.
 */
@ExtendWith(MockitoExtension.class)
class ConnectorPartyServiceTest {
    @Mock
    private ConnectorPartyRepository partyRepository;
    private ConnectorPartyService partyService;

    @BeforeEach
    void setUp() {
        this.partyService = new ConnectorPartyServiceImpl(partyRepository);
    }

    @Test
    void should_return_true_if_found_party_by_name_and_business_domain() {
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                PartyUtil.createFromParty());
        var exists = this.partyService.exists(
                PartyUtil.createFromParty(),
                BusinessDomainUtil.createDefaultBusinessDomain().identifier()
        );
        assertThat(exists).isNotNull();
        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_if_does_not_found_party_by_name_and_business_domain() {
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(null);
        var exists = this.partyService.exists(
                PartyUtil.createFromParty(),
                BusinessDomainUtil.createDefaultBusinessDomain().identifier()
        );
        assertThat(exists).isNotNull();
        assertThat(exists).isFalse();
    }
}
