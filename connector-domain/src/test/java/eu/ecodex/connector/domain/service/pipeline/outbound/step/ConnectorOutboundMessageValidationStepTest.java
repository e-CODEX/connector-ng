/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ActionTestFixtures;
import eu.ecodex.connector.MessageProcessingConfigProviderTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.ServiceTestFixtures;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.service.ConnectorActionServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorBusinessDomainServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorKeystoreServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorMessageServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorPartyServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorProcessingModeServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorServiceServiceImpl;
import eu.ecodex.connector.domain.spi.ConnectorActionRepository;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageValidationStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageValidationStepTest {
    @Mock
    ConnectorProcessingModeRepository processingModeRepository;
    @Mock
    private ConnectorMessageRepository messageRepository;
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
    @Mock
    private ConnectorKeystoreRepository keystoreRepository;

    private ConnectorMessageStep outboundMessageValidationStep;

    @BeforeEach
    void setUp() {
        var serviceService = new ConnectorServiceServiceImpl(serviceRepository);
        var processingModeService = getConnectorProcessingModeService(serviceService);
        var connectorMessageService = new ConnectorMessageServiceImpl(
                messageRepository, messageProcessingConfigProvider
        );
        outboundMessageValidationStep = new ConnectorOutboundMessageValidationStep(
                connectorMessageService, processingModeService, messageProcessingConfigProvider
        );
    }

    private @Nonnull ConnectorProcessingModeServiceImpl getConnectorProcessingModeService(
            ConnectorServiceServiceImpl serviceService) {
        var actionService = new ConnectorActionServiceImpl(actionRepository);
        var partyService = new ConnectorPartyServiceImpl(partyRepository);
        var businessDomainService = new ConnectorBusinessDomainServiceImpl(businessDomainRepository);
        var keystoreService = new ConnectorKeystoreServiceImpl(keystoreRepository);

        return new ConnectorProcessingModeServiceImpl(
                processingModeRepository,
                businessDomainService,
                serviceService,
                actionService,
                partyService,
                keystoreService
        );
    }

    @Test
    void should_execute_outbound_message_validation_successfully() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(MessageProcessingConfigProviderTestFixtures.getProcessingProperties());
        when(serviceRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ServiceTestFixtures.createService());
        when(actionRepository.findByNameAndBusinessDomain(any(), any())).thenReturn(
                ActionTestFixtures.createAction());
        when(partyRepository.findByPartyAndBusinessDomain(any(), any()))
                .thenReturn(
                        outboundMessage.as4Properties().toParty(),
                        outboundMessage.as4Properties().fromParty()
                );

        var outputMessage = outboundMessageValidationStep.execute(outboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage).isEqualTo(outboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> outboundMessageValidationStep.execute(null)
        );
    }
}
