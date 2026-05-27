/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.evidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.evidence.ConnectorEvidenceTriggerProcessorService;
import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.service.ConnectorEvidenceServiceImpl;
import eu.ecodex.connector.domain.service.ConnectorMessageServiceImpl;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorEvidenceTriggerProcessorServiceTest {
    private static final String EBMS_ID = "ebms-business-1";

    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageEvidenceCreator evidenceCreator;
    @Mock
    private ConnectorMessageEvidenceVerifier evidenceVerifier;
    @Mock
    private ConnectorEvidenceMessageCreator evidenceMessageCreator;
    @Mock
    private ConnectorLinkSubmitter linkSubmitter;
    @Mock
    private ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    private ConnectorMessageService messageService;
    private ConnectorEvidenceService evidenceService;
    private ConnectorEvidenceTriggerProcessorService processor;

    @BeforeEach
    void setUp() {
        messageService = new ConnectorMessageServiceImpl(messageRepository);
        evidenceService = new ConnectorEvidenceServiceImpl(messageService);
        processor = new ConnectorEvidenceTriggerProcessorService(
                messageService,
                evidenceService,
                messageRepository,
                evidenceCreator,
                evidenceVerifier,
                evidenceMessageCreator,
                linkSubmitter,
                processingConfigurationProvider
        );
    }

    @Test
    void should_create_delivery_evidence_and_submit_to_gateway() {
        var businessMessage = MessageTestFixtures.createValidInboundBusinessMessage()
                .toBuilder()
                .as4Properties(
                        MessageTestFixtures.createValidInboundBusinessMessage()
                                .as4Properties()
                                .toBuilder()
                                .ebmsMessageIdentifier(EBMS_ID)
                                .build()
                )
                .build();

        var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
        var trigger = MessageTestFixtures.createEvidenceTriggerMessage()
                .toBuilder()
                .as4Properties(
                        MessageTestFixtures.createEvidenceTriggerMessage()
                                .as4Properties()
                                .toBuilder()
                                .referenceToIdentifier(EBMS_ID)
                                .build()
                )
                .transportedEvidences(
                        java.util.List.of(
                                EvidenceTestFixtures.createEvidenceTrigger().toBuilder()
                                        .type(ConnectorEvidenceType.DELIVERY)
                                        .build()
                        )
                )
                .build();

        var gatewayEvidenceMessage = MessageTestFixtures.createDeliveryEvidenceMessage();

        when(messageRepository.findByEbmsMessageIdentifier(EBMS_ID)).thenReturn(businessMessage);
        when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
                .thenReturn(deliveryEvidence);
        when(messageRepository.findByIdentifier(businessMessage.identifier()))
                .thenReturn(businessMessage.toBuilder().evidences(java.util.List.of(
                        EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence()
                )).build());
        when(evidenceMessageCreator.createForTrigger(businessMessage, deliveryEvidence, trigger))
                .thenReturn(gatewayEvidenceMessage);
        when(processingConfigurationProvider.getConfiguration())
                .thenReturn(ConnectorMessageProcessingConfiguration.builder()
                        .sendGeneratedEvidencesToBackend(false)
                        .build());

        processor.process(trigger);

        verify(linkSubmitter).submit(gatewayEvidenceMessage);
        verify(linkSubmitter, never()).submit(gatewayEvidenceMessage.switchDirection());
        verify(evidenceVerifier).verify(eq(ConnectorEvidenceType.DELIVERY), any());
    }

    @Test
    void should_reject_trigger_when_business_message_not_found() {
        var trigger = MessageTestFixtures.createEvidenceTriggerMessage()
                .toBuilder()
                .as4Properties(
                        MessageTestFixtures.createEvidenceTriggerMessage()
                                .as4Properties()
                                .toBuilder()
                                .referenceToIdentifier("missing-ref")
                                .build()
                )
                .build();

        when(messageRepository.findByEbmsMessageIdentifier("missing-ref")).thenReturn(null);
        when(messageRepository.findByBackendMessageIdentifier(any())).thenReturn(null);
        when(messageRepository.findByIdentifier(any())).thenReturn(null);

        assertThatThrownBy(() -> processor.process(trigger))
                .isInstanceOf(eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException.class);
    }

    @Test
    void should_send_evidence_back_to_backend_when_configured() {
        var businessMessage = MessageTestFixtures.createValidInboundBusinessMessage()
                .toBuilder()
                .as4Properties(
                        MessageTestFixtures.createValidInboundBusinessMessage()
                                .as4Properties()
                                .toBuilder()
                                .ebmsMessageIdentifier(EBMS_ID)
                                .build()
                )
                .build();
        var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
        var trigger = MessageTestFixtures.createEvidenceTriggerMessage()
                .toBuilder()
                .as4Properties(
                        MessageTestFixtures.createEvidenceTriggerMessage()
                                .as4Properties()
                                .toBuilder()
                                .referenceToIdentifier(EBMS_ID)
                                .build()
                )
                .transportedEvidences(
                        java.util.List.of(
                                EvidenceTestFixtures.createEvidenceTrigger().toBuilder()
                                        .type(ConnectorEvidenceType.DELIVERY)
                                        .build()
                        )
                )
                .build();
        var gatewayEvidenceMessage = MessageTestFixtures.createDeliveryEvidenceMessage()
                .toBuilder()
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .build();

        when(messageRepository.findByEbmsMessageIdentifier(EBMS_ID)).thenReturn(businessMessage);
        when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
                .thenReturn(deliveryEvidence);
        when(messageRepository.findByIdentifier(businessMessage.identifier())).thenReturn(businessMessage);
        when(evidenceMessageCreator.createForTrigger(businessMessage, deliveryEvidence, trigger))
                .thenReturn(gatewayEvidenceMessage);
        when(processingConfigurationProvider.getConfiguration())
                .thenReturn(ConnectorMessageProcessingConfiguration.builder()
                        .sendGeneratedEvidencesToBackend(true)
                        .build());

        processor.process(trigger);

        var captor = ArgumentCaptor.forClass(eu.ecodex.connector.domain.model.message.ConnectorMessage.class);
        verify(linkSubmitter, org.mockito.Mockito.times(2)).submit(captor.capture());
        assertThat(captor.getAllValues().get(1).direction())
                .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }
}
