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

import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.service.ConnectorEvidenceServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorMessageServiceImpl;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageRoutingConfigProvider;
import eu.ecodex.connector.utils.EvidenceUtil;
import eu.ecodex.connector.utils.MessageProcessingConfigProviderUtil;
import eu.ecodex.connector.utils.MessageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageNonDeliveryStep}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageNonDeliveryStepTest {
    @Mock
    private ConnectorEvidenceToolkit evidenceToolkit;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;
    @Mock
    private ConnectorMessageRoutingConfigProvider messageRoutingConfigProvider;
    private ConnectorMessageService connectorMessageService;

    private ConnectorInboundMessageNonDeliveryStep inboundMessageNonDeliveryCreationStep;

    @BeforeEach
    void setUp() {
        connectorMessageService = new ConnectorMessageServiceImpl(
                messageRepository,
                messageProcessingConfigProvider
        );
        var connectorEvidenceService = new ConnectorEvidenceServiceImpl(
                evidenceToolkit,
                connectorMessageService
        );
        inboundMessageNonDeliveryCreationStep = new ConnectorInboundMessageNonDeliveryStep(
                connectorMessageService, connectorEvidenceService
        );
    }

    @Test
    void should_create_inbound_message_non_delivery_evidence_successfully() {
        var inboundMessage = MessageUtil.createValidInboundBusinessMessage();

        when(messageRepository.findByIdentifier(any())).thenReturn(inboundMessage);
        when(messageRepository.addEvidence(any(), any()))
                .thenReturn(MessageUtil.createNonDeliveryEvidenceMessage());
        when(evidenceToolkit.create(any(), any(), any()))
                .thenReturn(EvidenceUtil.createNonDeliveryEvidence());
        when(messageProcessingConfigProvider.getProcessingProperties())
                .thenReturn(MessageProcessingConfigProviderUtil.getProcessingProperties());

        var outputMessage = inboundMessageNonDeliveryCreationStep.execute(inboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(connectorMessageService.isEvidenceMessage(outputMessage)).isTrue();
        assertThat(outputMessage.evidences()).isNotEmpty();
        assertThat(outputMessage.evidences()).hasSize(1);
        Assertions.assertNotNull(outputMessage.evidences());
        assertThat(outputMessage.evidences().getFirst().type())
                .isEqualTo(ConnectorEvidenceType.NON_DELIVERY);
        assertThat(outputMessage.direction()).isNotEqualTo(inboundMessage.direction());
        assertThat(outputMessage.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> inboundMessageNonDeliveryCreationStep.execute(null)
        );
    }
}
