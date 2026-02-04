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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.api.service.ConnectorProcessingModeService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeVerificationException;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import eu.ecodex.connector.utils.ActionUtil;
import eu.ecodex.connector.utils.BusinessDomainUtil;
import eu.ecodex.connector.utils.MessageUtil;
import eu.ecodex.connector.utils.PartyUtil;
import eu.ecodex.connector.utils.ProcessingModeUtil;
import eu.ecodex.connector.utils.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorProcessingModeService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
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
    private ConnectorProcessingModeService processingModeService;

    @BeforeEach
    void setUp() {
        var serviceService = new ConnectorServiceServiceImpl(serviceRepository);
        var actionService = new ConnectorActionServiceImpl(actionRepository);
        var partyService = new ConnectorPartyServiceImpl(partyRepository);
        var businessDomainService = new ConnectorBusinessDomainServiceImpl(
                businessDomainRepository
        );

        this.processingModeService = new ConnectorProcessingModeServiceImpl(
                processingModeRepository,
                businessDomainService,
                serviceService,
                actionService,
                partyService
        );
    }

    // save
    @Test
    void should_register_processing_mode_successfully() {
        var businessDomain = BusinessDomainUtil.createDefaultBusinessDomain();
        var processingMode = ProcessingModeUtil.createWithNoBusinessDomain();

        when(processingModeRepository.save(any(), any())).thenReturn(
                ProcessingModeUtil.createWithBusinessDomain());
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);

        var createdProcessingMode = this.processingModeService.register(
                businessDomain.identifier(), processingMode);

        assertThat(createdProcessingMode).isNotNull();
        assertThat(createdProcessingMode.description()).isEqualTo(processingMode.description());
        assertThat(createdProcessingMode.businessDomain()).isEqualTo(businessDomain);

        verify(processingModeRepository, times(1)).save(businessDomain, processingMode);
    }

    @Test
    void should_throw_exception_when_saving_processing_mode_if_business_domain_does_not_exists() {
        var businessDomain = BusinessDomainUtil.createDefaultBusinessDomain();
        var processingMode = ProcessingModeUtil.createWithNoBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);

        assertThrows(
                ConnectorBusinessDomainNotFoundException.class,
                () -> this.processingModeService.register(businessDomain.identifier(), processingMode)
        );
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Test
    void should_throw_exception_when_saving_processing_mode_if_business_domains_has_already_one_processing_mode() {
        var businessDomain = BusinessDomainUtil.createDefaultBusinessDomain();
        var processingMode = ProcessingModeUtil.createWithBusinessDomain();

        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);
        when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(
                processingMode
        );

        assertThrows(
                ConnectorProcessingModeException.class,
                () -> this.processingModeService.register(
                        BusinessDomainUtil.createDefaultBusinessDomain().identifier(),
                        processingMode
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_processing_mode_if_business_domain_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.processingModeService.register(
                        null, ProcessingModeUtil.createWithNoBusinessDomain()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_processing_mode_if_processing_mode_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.processingModeService.register(
                        BusinessDomainUtil.createDefaultBusinessDomain().identifier(), null
                )
        );
    }

    // check message service and action validity against indicated business domain pmode
    @Test
    void should_check_message_validity_in_strict_verification_mode_successfully() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());

        var message = MessageUtil.createValidOutboundBusinessMessage();
        this.processingModeService.checkMessage(message, ProcessingModeVerificationMode.STRICT);
    }

    @Test
    void should_throw_exception_in_strict_verification_mode_when_message_is_invalid_due_to_service_not_found() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createNullFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.STRICT
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(0)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(0)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_strict_verification_mode_when_message_is_invalid_due_to_action_not_found() {
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createNullFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.STRICT
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(0)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_check_message_validity_in_relaxed_verification_mode_successfully() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                PartyUtil.createToParty(), PartyUtil.createFromParty()
        );
        var message = MessageUtil.createValidOutboundBusinessMessage();
        this.processingModeService.checkMessage(message, ProcessingModeVerificationMode.RELAXED);

        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(2)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_null_to_party_not_found() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createNullToPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(1)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_empty_to_party_identifier_type() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createEmptyToPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(serviceRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(actionRepository, times(1)).findByNameAndBusinessDomain(any(), any());
        verify(partyRepository, times(1)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_from_party_not_found() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                PartyUtil.createToParty(),
                (ConnectorParty) null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createNullFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(partyRepository, times(2)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_relaxed_verification_mode_when_message_is_invalid_due_to_empty_from_party_identifier_type() {
        when(this.serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(this.partyRepository.findByPartyAndBusinessDomain(any(), any())).thenReturn(
                PartyUtil.createToParty(),
                (ConnectorParty) null
        );
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createEmptyFromPartyOutboundBusinessMessage(),
                        ProcessingModeVerificationMode.RELAXED
                )
        );
        verify(partyRepository, times(2)).findByPartyAndBusinessDomain(any(), any());
    }

    @Test
    void should_throw_exception_in_create_verification_mode() {
        assertThrows(
                ConnectorProcessingModeVerificationException.class,
                () -> this.processingModeService.checkMessage(
                        MessageUtil.createValidOutboundBusinessMessage(),
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
                        MessageUtil.createValidOutboundBusinessMessage(),
                        null
                )
        );
    }
}
