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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.application.service.impl.pmode.ConnectorRegisterProcessingModeService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.pmode.ConnectorServiceRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorRegisterProcessingModeServiceTest {
    @Mock
    private ConnectorProcessingModeRepository processingModeRepository;
    @Mock
    private ConnectorServiceRepository serviceRepository;
    @Mock
    private ConnectorActionRepository actionRepository;
    @Mock
    private ConnectorPartyRepository partyRepository;
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;

    @InjectMocks
    private ConnectorRegisterProcessingModeService registerProcessingModeService;

    @Test
    void should_register_pmode_successfully() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        var processingMode = ProcessingModeTestFixtures.createWithNoBusinessDomain();

        when(processingModeRepository.save(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());
        when(processingModeRepository.findByBusinessDomainIdentifier(any()))
                .thenReturn(null);
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);
        when(actionRepository.saveAll(any(), any()))
                .thenReturn(List.of(ActionTestFixtures.createAction()));
        when(serviceRepository.saveAll(any(), any()))
                .thenReturn(List.of(ServiceTestFixtures.createService()));
        when(partyRepository.saveAll(any(), any()))
                .thenReturn(List.of(PartyTestFixtures.createToParty()));

        var createdProcessingMode = this.registerProcessingModeService.execute(
                businessDomain.identifier(), processingMode);

        assertThat(createdProcessingMode).isNotNull();
        assertThat(createdProcessingMode.description()).isEqualTo(processingMode.description());
        assertThat(createdProcessingMode.businessDomain()).isEqualTo(businessDomain);

        verify(processingModeRepository, times(1))
                .save(any(), any());
    }

    @Test
    void should_throw_exception_when_saving_pmode_if_business_domain_does_not_exists() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        var processingMode = ProcessingModeTestFixtures.createWithNoBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);

        assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> this.registerProcessingModeService.execute(
                        businessDomain.identifier(), processingMode)
        );
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Test
    void should_throw_exception_when_saving_pmode_if_business_domains_has_already_one_pmode() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        var processingMode = ProcessingModeTestFixtures.createWithBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);
        when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(
                processingMode
        );

        assertThrows(
                ConnectorProcessingModeException.class,
                () -> this.registerProcessingModeService.execute(
                        BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier(),
                        processingMode
                )
        );
    }

    @Test
    void should_throw_exception_when_saving_pmode_and_no_home_party_is_found() {
        var businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        var processingMode = ProcessingModeTestFixtures.createWithNoBusinessDomainAndNoHomeParty();

        when(processingModeRepository.findByBusinessDomainIdentifier(any()))
                .thenReturn(null);
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

        assertThrows(
                ConnectorProcessingModeException.class,
                () -> this.registerProcessingModeService.execute(
                        BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier(),
                        processingMode
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_pmode_if_business_domain_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.registerProcessingModeService.execute(
                        null,
                        ProcessingModeTestFixtures.createWithNoBusinessDomain()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_pmode_if_pmode_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.registerProcessingModeService.execute(
                        BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier(),
                        null
                )
        );
    }
}
