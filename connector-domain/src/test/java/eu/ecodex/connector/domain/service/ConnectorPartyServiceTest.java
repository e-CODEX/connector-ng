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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorPartyService;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit tests for the {@code ConnectorPartyService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class ConnectorPartyServiceTest {
    @Mock
    private ConnectorPartyRepository partyRepository;
    private ConnectorPartyService partyService;

    @BeforeEach
    void setUp() {
        this.partyService = new ConnectorPartyServiceImpl(partyRepository);
    }

    // bulk save
    @Test
    void should_bulk_save_parties_successfully() {
        var parties = List.of(PartyTestFixtures.createFromParty());

        when(partyRepository.saveAll(any(), any())).thenReturn(parties);

        var savedParties = partyService.persistAll(
                parties,
                BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
        );

        assertThat(savedParties).isNotNull();
        assertThat(savedParties).hasSize(1);
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_parties() {
        assertThrows(
                NullPointerException.class,
                () -> partyService.persistAll(
                        null,
                        BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> partyService.persistAll(
                        List.of(PartyTestFixtures.createFromParty()),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_parties_with_null_parties_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> partyService.persistAll(
                        null,
                        null
                )
        );
    }

    // exists
    @Test
    void should_return_true_if_found_party_by_name_and_business_domain() {
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                PartyTestFixtures.createFromParty());
        var exists = this.partyService.exists(
                PartyTestFixtures.createFromParty(),
                BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
        );
        assertThat(exists).isNotNull();
        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_if_does_not_found_party_by_name_and_business_domain() {
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(null);
        var exists = this.partyService.exists(
                PartyTestFixtures.createFromParty(),
                BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
        );
        assertThat(exists).isNotNull();
        assertThat(exists).isFalse();
    }
}
