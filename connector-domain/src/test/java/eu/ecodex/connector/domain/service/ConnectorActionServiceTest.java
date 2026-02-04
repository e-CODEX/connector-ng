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

import eu.ecodex.connector.domain.api.service.ConnectorActionService;
import eu.ecodex.connector.domain.exception.ConnectorActionNotFoundException;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.utils.ActionUtil;
import eu.ecodex.connector.utils.BusinessDomainUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorActionService} implementation.
 */
@ExtendWith(MockitoExtension.class)
class ConnectorActionServiceTest {
    @Mock
    private ConnectorActionRepository actionRepository;
    private ConnectorActionService actionService;

    @BeforeEach
    void setUp() {
        this.actionService = new ConnectorActionServiceImpl(actionRepository);
    }

    @Test
    void should_find_action_by_name_and_business_domain_successfully() {
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        var action = this.actionService.findByNameAndBusinessDomain(
                "", BusinessDomainUtil.createDefaultBusinessDomain().identifier());
        assertThat(action).isNotNull();
        assertThat(action.name()).isEqualTo("ConTest_Form");
    }

    @Test
    void should_throw_exception_when_action_does_not_exist() {
        when(this.actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(null);
        assertThrows(
                ConnectorActionNotFoundException.class,
                () -> this.actionService.findByNameAndBusinessDomain(
                        "", BusinessDomainUtil.createDefaultBusinessDomain().identifier())
        );
    }
}
