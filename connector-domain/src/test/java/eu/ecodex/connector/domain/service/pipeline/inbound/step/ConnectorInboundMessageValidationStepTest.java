/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.service.ConnectorActionServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorBusinessDomainServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorPartyServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorProcessingModeServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorServiceServiceImpl;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import eu.ecodex.connector.utils.ActionUtil;
import eu.ecodex.connector.utils.MessageProcessingConfigProviderUtil;
import eu.ecodex.connector.utils.MessageUtil;
import eu.ecodex.connector.utils.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageValidationStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageValidationStepTest {
    @Mock
    ConnectorProcessingModeRepository processingModeRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;
    @Mock
    private ConnectorServiceRepository serviceRepository;
    @Mock
    private ConnectorActionRepository actionRepository;
    @Mock
    private ConnectorPartyRepository partyRepository;
    @Mock
    private ConnectorBusinessDomainRepository businessDomainRepository;

    private ConnectorInboundMessageValidationStep inboundMessageValidationStep;

    @BeforeEach
    void setUp() {
        var serviceService = new ConnectorServiceServiceImpl(serviceRepository);
        var actionService = new ConnectorActionServiceImpl(actionRepository);
        var partyService = new ConnectorPartyServiceImpl(partyRepository);
        var businessDomainService = new ConnectorBusinessDomainServiceImpl(businessDomainRepository);
        var processingModeService = new ConnectorProcessingModeServiceImpl(
                processingModeRepository,
                businessDomainService,
                serviceService,
                actionService,
                partyService
        );
        inboundMessageValidationStep = new ConnectorInboundMessageValidationStep(
                processingModeService, messageProcessingConfigProvider
        );
    }

    @Test
    void should_execute_inbound_message_validation_successfully() {
        var inboundMessage = MessageUtil.createValidInboundBusinessMessage();

        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceUtil.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionUtil.createAction());
        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(MessageProcessingConfigProviderUtil.getProcessingProperties());

        var outputMessage = inboundMessageValidationStep.execute(inboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage).isEqualTo(inboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> inboundMessageValidationStep.execute(null)
        );
    }
}
