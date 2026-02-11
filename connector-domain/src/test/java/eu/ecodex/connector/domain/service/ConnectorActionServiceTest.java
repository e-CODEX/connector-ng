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

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorActionService;
import eu.ecodex.connector.domain.exception.ConnectorActionNotFoundException;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorActionService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
class ConnectorActionServiceTest {
    @Mock
    private ConnectorActionRepository actionRepository;
    private ConnectorActionService actionService;

    @BeforeEach
    void setUp() {
        this.actionService = new ConnectorActionServiceImpl(actionRepository);
    }

    // bulk save
    @Test
    void should_bulk_save_actions_successfully() {
        var actions = List.of(ActionTestFixtures.createAction());

        when(actionRepository.saveAll(any(), any())).thenReturn(actions);

        var savedActions = actionService.persistAll(
                actions, BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
        );

        assertThat(savedActions).isNotNull();
        assertThat(savedActions).hasSize(1);
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_business_domain_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> actionService.persistAll(List.of(), null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_actions() {
        assertThrows(
                NullPointerException.class,
                () -> actionService.persistAll(
                        null,
                        BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_bulk_saving_actions_with_null_actions_and_business_domain_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> actionService.persistAll(null, null)
        );
    }

    // find action by name and business domain
    @Test
    void should_find_action_by_name_and_business_domain_successfully() {
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        var action = this.actionService.findByNameAndBusinessDomain(
                "", BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier());
        assertThat(action).isNotNull();
        assertThat(action.name()).isEqualTo("ConTest_Form");
    }

    @Test
    void should_throw_exception_when_action_does_not_exist() {
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorActionNotFoundException.class,
                () -> this.actionService.findByNameAndBusinessDomain(
                        "", BusinessDomainTestFixtures.createDefaultBusinessDomain().identifier())
        );
    }
}
