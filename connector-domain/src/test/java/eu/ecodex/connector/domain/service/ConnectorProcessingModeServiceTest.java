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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorProcessingModeService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorProcessingModeServiceTest {
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
    @Mock
    private ConnectorKeystoreRepository keystoreRepository;

    private ConnectorProcessingModeService processingModeService;

    @BeforeEach
    void setUp() {
        var serviceService = new ConnectorServiceServiceImpl(serviceRepository);
        var actionService = new ConnectorActionServiceImpl(actionRepository);
        var partyService = new ConnectorPartyServiceImpl(partyRepository);
        var businessDomainService = new ConnectorBusinessDomainServiceImpl(
                businessDomainRepository
        );
        var keystoreService = new ConnectorKeystoreServiceImpl(keystoreRepository);

        this.processingModeService = new ConnectorProcessingModeServiceImpl(
                processingModeRepository,
                businessDomainService,
                serviceService,
                actionService,
                partyService,
                keystoreService
        );
    }

    // check message service and action validity against indicated business domain pmode
    @Test
    void should_check_message_validity_in_strict_verification_mode_successfully() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());

        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        this.processingModeService.checkMessage(message, ProcessingModeVerificationMode.STRICT);
    }

    @Test
    void should_throw_exception_in_strict_verification_mode_when_message_is_invalid_due_to_service_not_found() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createNullFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.STRICT
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(0)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(0)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_strict_verification_mode_when_message_is_invalid_due_to_action_not_found() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createNullFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.STRICT
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(0)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_check_message_validity_in_relaxed_verification_mode_successfully() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(this.partyRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                PartyTestFixtures.createToParty(), PartyTestFixtures.createFromParty()
        );
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        this.processingModeService.checkMessage(message, ProcessingModeVerificationMode.RELAXED);

        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(2)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_to_party_not_found() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(this.partyRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(1)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_empty_to_party_identifier_type() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(this.partyRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createEmptyToPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(1)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_from_party_not_found() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(this.partyRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                PartyTestFixtures.createToParty(),
                (ConnectorParty) null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(partyRepository, times(2)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_empty_from_party_identifier_type() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(this.partyRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                PartyTestFixtures.createToParty(),
                (ConnectorParty) null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createEmptyFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(partyRepository, times(2)).findByNameAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_create_verification_mode() {
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.CREATE
                )
        );
    }

    @Test
    void should_throw_exception_if_message_is_empty_during_message_verification() {
        assertThrows(
                NullPointerException.class,
                () -> this.processingModeService.checkMessage(
                        null,
                        ProcessingModeVerificationMode.CREATE
                )
        );
    }

    @Test
    void should_throw_exception_if_verification_mode_is_empty_during_message_verification() {
        assertThrows(
                NullPointerException.class,
                () -> this.processingModeService.checkMessage(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        null
                )
        );
    }
}
