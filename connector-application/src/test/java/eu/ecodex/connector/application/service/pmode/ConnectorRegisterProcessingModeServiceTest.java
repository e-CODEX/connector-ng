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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.PartyTestFixtures;
import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.application.port.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorServiceRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeParser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorRegisterProcessingModeService")
class ConnectorRegisterProcessingModeServiceTest {
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
    private ConnectorProcessingModeParser processingModeParser;

    @InjectMocks
    private ConnectorRegisterProcessingModeService registerProcessingModeService;

    @Captor
    private ArgumentCaptor<ConnectorProcessingMode> processingModeCaptor;

    private ConnectorBusinessDomain businessDomain;
    private ConnectorProcessingMode processingMode;
    private ConnectorProcessingModeParser.ParsedProcessingMode parsed;

    @BeforeEach
    void setUp() {
        businessDomain = BusinessDomainTestFixtures.createDefaultBusinessDomain();
        processingMode = ProcessingModeTestFixtures.createWithNoBusinessDomain();
        parsed = new ConnectorProcessingModeParser.ParsedProcessingMode(
            "blue_gw",
            Set.of(PartyTestFixtures.createFromParty(), PartyTestFixtures.createToParty()),
            Set.of(ServiceTestFixtures.createService()),
            Set.of(ActionTestFixtures.createAction())
        );
    }

    private void givenBusinessDomainExistsWithoutProcessingMode() {
        when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);
        when(processingModeRepository.findByBusinessDomainIdentifier(any())).thenReturn(null);
    }

    @Nested
    @DisplayName("when the registration succeeds")
    class Success {
        @BeforeEach
        void setUp() {
            givenBusinessDomainExistsWithoutProcessingMode();
            when(processingModeParser.parse(any())).thenReturn(parsed);
            when(processingModeRepository.save(any(), any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());
        }

        @Test
        void should_return_the_persisted_processing_mode() {
            var created = registerProcessingModeService.execute(
                businessDomain.identifier(), processingMode);

            assertThat(created).isNotNull();
            verify(processingModeRepository).save(any(), eq(businessDomain.identifier()));
        }

        @Test
        void should_attach_the_business_domain_and_the_parsed_content_before_saving() {
            registerProcessingModeService.execute(businessDomain.identifier(), processingMode);

            verify(processingModeRepository).save(
                processingModeCaptor.capture(), eq(businessDomain.identifier()));

            var saved = processingModeCaptor.getValue();
            assertThat(saved.businessDomain()).isEqualTo(businessDomain);
            assertThat(saved.description()).isEqualTo(processingMode.description());
            assertThat(saved.parties()).containsAnyElementsOf(parsed.parties());
            assertThat(saved.services()).containsAnyElementsOf(parsed.services());
            assertThat(saved.actions()).containsAnyElementsOf(parsed.actions());
        }

        @Test
        void should_persist_the_parties_services_and_actions_returned_by_the_parser() {
            registerProcessingModeService.execute(businessDomain.identifier(), processingMode);

            ArgumentCaptor<List<ConnectorParty>> parties = ArgumentCaptor.captor();
            ArgumentCaptor<List<ConnectorService>> services = ArgumentCaptor.captor();
            ArgumentCaptor<List<ConnectorAction>> actions = ArgumentCaptor.captor();

            verify(partyRepository).saveAll(parties.capture(), eq(businessDomain.identifier()));
            verify(serviceRepository).saveAll(services.capture(), eq(businessDomain.identifier()));
            verify(actionRepository).saveAll(actions.capture(), eq(businessDomain.identifier()));

            assertThat(parties.getValue()).containsExactlyInAnyOrderElementsOf(parsed.parties());
            assertThat(services.getValue()).containsExactlyInAnyOrderElementsOf(parsed.services());
            assertThat(actions.getValue()).containsExactlyInAnyOrderElementsOf(parsed.actions());
        }

        @Test
        void should_hand_the_raw_definition_to_the_parser() {
            registerProcessingModeService.execute(businessDomain.identifier(), processingMode);

            verify(processingModeParser).parse(processingMode.content().getBytes());
        }
    }

    @Nested
    @DisplayName("when the registration is rejected")
    class Rejection {
        @Test
        void should_throw_when_the_business_domain_does_not_exist() {
            when(businessDomainRepository.findByIdentifier(any())).thenReturn(null);

            assertThatExceptionOfType(ConnectorBusinessDomainNotFoundException.class)
                .isThrownBy(() -> registerProcessingModeService.execute(
                    businessDomain.identifier(), processingMode))
                .withMessageContaining(businessDomain.identifier().messageLaneIdentifier());

            verifyNoInteractions(
                processingModeParser, partyRepository, serviceRepository,
                actionRepository
            );
            verify(processingModeRepository, never()).save(any(), any());
        }

        @Test
        void should_throw_when_the_business_domain_already_has_a_processing_mode() {
            when(businessDomainRepository.findByIdentifier(any())).thenReturn(businessDomain);
            when(processingModeRepository.findByBusinessDomainIdentifier(any()))
                .thenReturn(ProcessingModeTestFixtures.createWithBusinessDomain());

            assertThatExceptionOfType(ConnectorProcessingModeException.class)
                .isThrownBy(() -> registerProcessingModeService.execute(
                    businessDomain.identifier(), processingMode));

            verifyNoInteractions(
                processingModeParser, partyRepository, serviceRepository,
                actionRepository
            );
            verify(processingModeRepository, never()).save(any(), any());
        }

        @Test
        void should_propagate_a_parsing_failure_without_persisting_anything() {
            givenBusinessDomainExistsWithoutProcessingMode();
            when(processingModeParser.parse(any()))
                .thenThrow(RuntimeException.class);

            assertThatExceptionOfType(ConnectorProcessingModeException.class)
                .isThrownBy(() -> registerProcessingModeService.execute(
                    businessDomain.identifier(), processingMode));

            verify(processingModeRepository, never()).save(any(), any());
            verifyNoInteractions(partyRepository, serviceRepository, actionRepository);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("when arguments are null")
    class NullArguments {
        @Test
        void should_throw_when_the_business_domain_identifier_is_null() {
            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> registerProcessingModeService.execute(null, processingMode));

            verifyNoInteractions(
                businessDomainRepository, processingModeRepository,
                processingModeParser
            );
        }

        @Test
        void should_throw_when_the_processing_mode_is_null() {
            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> registerProcessingModeService.execute(
                    businessDomain.identifier(), null));

            verifyNoInteractions(
                businessDomainRepository, processingModeRepository,
                processingModeParser
            );
        }
    }
}
