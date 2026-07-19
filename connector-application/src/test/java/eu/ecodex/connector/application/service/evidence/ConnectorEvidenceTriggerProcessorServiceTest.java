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
import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private ConnectorEvidenceTriggerProcessorService processor;

    @Test
    void should_create_delivery_evidence_and_submit_to_gateway() {
        var businessMessage = MessageTestFixtures.createInboundBusinessMessage()
                                                 .toBuilder()
                                                 .as4Properties(
                                                     MessageTestFixtures.createInboundBusinessMessage()
                                                                        .as4Properties()
                                                                        .toBuilder()
                                                                        .ebmsMessageIdentifier(
                                                                            EBMS_ID)
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
                                             List.of(
                                                 EvidenceTestFixtures.createEvidenceTrigger()
                                                                     .toBuilder()
                                                                     .type(ConnectorEvidenceType.DELIVERY)
                                                                     .build()
                                             )
                                         )
                                         .build();

        var evidenceMessage = MessageTestFixtures.createDeliveryEvidenceMessage();

        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
            EBMS_ID, ConnectorMessageDirection.revert(evidenceMessage.direction())))
            .thenReturn(businessMessage);
        when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
            .thenReturn(deliveryEvidence);
        when(messageRepository.findByIdentifier(businessMessage.identifier()))
            .thenReturn(businessMessage.toBuilder().evidences(java.util.List.of(
                EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence()
            )).build());
        when(evidenceMessageCreator.createForTrigger(businessMessage, deliveryEvidence, trigger))
            .thenReturn(evidenceMessage);
        when(processingConfigurationProvider.getConfiguration())
            .thenReturn(ConnectorMessageProcessingConfiguration.builder()
                                                               .sendGeneratedEvidencesToBackend(
                                                                   false)
                                                               .build());

        processor.process(trigger);

        verify(linkSubmitter).submit(evidenceMessage);
        verify(linkSubmitter, never()).submit(evidenceMessage.switchDirection());
        verify(evidenceVerifier).verify(eq(ConnectorEvidenceType.DELIVERY), any());
    }

    @Test
    void should_throw_exception_if_evidence_message_direction_is_not_set_or_is_null() {
        var trigger = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .direction(null)
                                         .build();

        assertThatThrownBy(() -> processor.process(trigger))
            .isInstanceOf(ConnectorEvidenceException.class);
    }

    @Test
    void should_reject_trigger_when_business_message_not_found() {
        var trigger = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .as4Properties(
                                             MessageTestFixtures.createEvidenceTriggerMessage()
                                                                .as4Properties()
                                                                .toBuilder()
                                                                .referenceToIdentifier(
                                                                    "missing-ref")
                                                                .build()
                                         )
                                         .build();

        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
            "missing-ref",
            ConnectorMessageDirection.revert(trigger.direction())
        ))
            .thenReturn(null);
        when(messageRepository.findByBackendMessageIdentifier(any())).thenReturn(null);
        when(messageRepository.findByIdentifier(any())).thenReturn(null);

        assertThatThrownBy(() -> processor.process(trigger))
            .isInstanceOf(eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException.class);
    }

    @Test
    void should_send_evidence_back_to_backend_when_configured() {
        var businessMessage = MessageTestFixtures.createInboundBusinessMessage()
                                                 .toBuilder()
                                                 .as4Properties(
                                                     MessageTestFixtures.createInboundBusinessMessage()
                                                                        .as4Properties()
                                                                        .toBuilder()
                                                                        .ebmsMessageIdentifier(
                                                                            EBMS_ID)
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
                                                 EvidenceTestFixtures.createEvidenceTrigger()
                                                                     .toBuilder()
                                                                     .type(ConnectorEvidenceType.DELIVERY)
                                                                     .build()
                                             )
                                         )
                                         .build();
        var gatewayEvidenceMessage = MessageTestFixtures.createDeliveryEvidenceMessage()
                                                        .toBuilder()
                                                        .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                                        .build();

        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
            EBMS_ID, ConnectorMessageDirection.revert(trigger.direction())))
            .thenReturn(businessMessage);
        when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
            .thenReturn(deliveryEvidence);
        when(messageRepository.findByIdentifier(businessMessage.identifier())).thenReturn(
            businessMessage);
        when(evidenceMessageCreator.createForTrigger(businessMessage, deliveryEvidence, trigger))
            .thenReturn(gatewayEvidenceMessage);
        when(processingConfigurationProvider.getConfiguration())
            .thenReturn(ConnectorMessageProcessingConfiguration.builder()
                                                               .sendGeneratedEvidencesToBackend(
                                                                   true)
                                                               .build());

        processor.process(trigger);

        var captor = ArgumentCaptor.forClass(eu.ecodex.connector.domain.model.message.ConnectorMessage.class);
        verify(linkSubmitter, org.mockito.Mockito.times(2)).submit(captor.capture());
        assertThat(captor.getAllValues().get(1).direction())
            .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }
}
