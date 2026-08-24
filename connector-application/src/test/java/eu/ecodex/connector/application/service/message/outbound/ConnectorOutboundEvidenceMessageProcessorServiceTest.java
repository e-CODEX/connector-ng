/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.TriggeredEvidenceMessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundEvidenceMessageProcessorService")
class ConnectorOutboundEvidenceMessageProcessorServiceTest {
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
    private ConnectorOutboundEvidenceMessageProcessorService processor;

    private ConnectorBusinessMessage businessMessageWithEbmsId() {
        var base = BusinessMessageTestFixtures.createInboundMessage();
        return base.toBuilder()
                   .as4Properties(
                       base.as4Properties()
                           .toBuilder()
                           .ebmsMessageIdentifier(EBMS_ID)
                           .build()
                   )
                   .build();
    }

    @Nested
    @DisplayName("when processing succeeds")
    class WhenProcessingSucceeds {
        @Test
        void should_create_the_delivery_evidence_and_submit_it_to_the_gateway() {
            var businessMessage = businessMessageWithEbmsId();
            var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
            var triggeredEvidenceMessage =
                TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage();
            var evidenceMessage = EvidenceMessageTestFixtures.createDeliveryEvidenceMessage();

            when(messageRepository.findReferencedBusinessMessage(
                triggeredEvidenceMessage.referenceToIdentifier(),
                triggeredEvidenceMessage.direction()
            )).thenReturn(businessMessage);
            when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
                .thenReturn(deliveryEvidence);
            when(messageRepository.findByIdentifier(businessMessage.identifier()))
                .thenReturn(businessMessage.toBuilder().evidences(List.of(
                    EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence()
                )).build());
            when(evidenceMessageCreator.createForTrigger(
                businessMessage,
                deliveryEvidence,
                triggeredEvidenceMessage
            )).thenReturn(evidenceMessage);
            when(processingConfigurationProvider.getConfiguration())
                .thenReturn(ConnectorMessageProcessingConfiguration
                                .builder()
                                .sendGeneratedEvidencesToBackend(false)
                                .build());

            processor.execute(triggeredEvidenceMessage);

            verify(linkSubmitter).submit(evidenceMessage);
            verify(linkSubmitter, never()).submit(evidenceMessage.switchDirection());
            verify(evidenceVerifier).verify(eq(ConnectorEvidenceType.DELIVERY), any());
        }

        @Test
        void should_also_submit_the_evidence_to_the_backend_when_configured() {
            var businessMessage = businessMessageWithEbmsId();
            var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
            var triggeredEvidenceMessage =
                TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage();
            var gatewayEvidenceMessage = EvidenceMessageTestFixtures.createDeliveryEvidenceMessage()
                                                                    .toBuilder()
                                                                    .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                                                    .build();

            when(messageRepository.findReferencedBusinessMessage(
                triggeredEvidenceMessage.referenceToIdentifier(),
                triggeredEvidenceMessage.direction()
            )).thenReturn(businessMessage);
            when(evidenceCreator.createSuccess(ConnectorEvidenceType.DELIVERY, businessMessage))
                .thenReturn(deliveryEvidence);
            when(messageRepository.findByIdentifier(businessMessage.identifier()))
                .thenReturn(businessMessage);
            when(evidenceMessageCreator.createForTrigger(
                businessMessage,
                deliveryEvidence,
                triggeredEvidenceMessage
            )).thenReturn(gatewayEvidenceMessage);
            when(processingConfigurationProvider.getConfiguration())
                .thenReturn(ConnectorMessageProcessingConfiguration.builder()
                                                                   .sendGeneratedEvidencesToBackend(
                                                                       true)
                                                                   .build());

            processor.execute(triggeredEvidenceMessage);

            var captor = ArgumentCaptor.forClass(ConnectorEvidenceMessage.class);
            verify(linkSubmitter, times(2)).submit(captor.capture());
            assertThat(captor.getAllValues().get(1).direction())
                .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
        }
    }

    @Nested
    @DisplayName("when processing fails")
    class WhenProcessingFails {
        @Test
        void should_fail_when_the_business_message_is_not_found() {
            var triggeredEvidenceMessage =
                TriggeredEvidenceMessageTestFixtures.createDeliveryTriggeredEvidenceMessage();

            when(messageRepository.findReferencedBusinessMessage(
                triggeredEvidenceMessage.referenceToIdentifier(),
                triggeredEvidenceMessage.direction()
            )).thenReturn(null);

            assertThatThrownBy(() -> processor.execute(triggeredEvidenceMessage))
                .isInstanceOf(ConnectorMessageNotFoundException.class);
        }

        @Test
        void should_fail_when_the_triggered_message_is_null() {
            assertThatThrownBy(() -> processor.execute(null))
                .isInstanceOf(NullPointerException.class);
        }
    }
}
