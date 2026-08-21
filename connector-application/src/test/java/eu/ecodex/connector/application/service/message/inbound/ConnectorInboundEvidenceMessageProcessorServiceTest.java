/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.AS4PropertiesTestFixtures;
import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.ConnectorErrorCode;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorInboundEvidenceMessageProcessorService")
public class ConnectorInboundEvidenceMessageProcessorServiceTest {
    private static final String REFERENCED_EBMS_ID = "original-ebms-id@domibus.eu";
    private static final String REFERENCED_MESSAGE_ID =
        "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu";
    private static final String CONFIRMATION_MESSAGE_ID = "evidence-msg@connector.ecodex.eu";
    private static final String BACKEND_MESSAGE_ID =
        "85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu";

    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @Mock
    private ConnectorMessageEvidenceVerifier evidenceVerifier;
    @Mock
    private ConnectorLinkSubmitter linkSubmitter;

    @InjectMocks
    private ConnectorInboundEvidenceMessageProcessorService processor;

    @Nested
    @DisplayName("when the referenced message is found")
    class WhenReferenceIsFound {
        @Test
        void should_apply_the_evidence_and_forward_the_confirmation() {
            var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
            var confirmationMessage = gatewayConfirmationMessage(List.of(deliveryEvidence));
            var referencedMessage = referencedBusinessMessage();

            when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID,
                ConnectorMessageDirection.revert(confirmationMessage.direction())
            )).thenReturn(referencedMessage);
            when(messageRepository.findByIdentifier(REFERENCED_MESSAGE_ID))
                .thenReturn(referencedMessage);

            processor.process(confirmationMessage);

            verify(evidenceVerifier).verify(
                eq(ConnectorEvidenceType.DELIVERY),
                any(ConnectorBusinessMessage.class)
            );
            verify(evidenceRepository).save(deliveryEvidence, REFERENCED_MESSAGE_ID);
        }

        @Test
        void should_skip_the_evidence_but_still_forward_the_confirmation_when_it_is_not_relevant() {
            var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
            var confirmationMessage = gatewayConfirmationMessage(List.of(deliveryEvidence));
            var referencedMessage = referencedBusinessMessage();

            when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID,
                ConnectorMessageDirection.revert(confirmationMessage.direction())
            )).thenReturn(referencedMessage);
            doThrow(new ConnectorEvidenceNotRelevantException(
                ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY
            )).when(evidenceVerifier)
              .verify(eq(ConnectorEvidenceType.DELIVERY), any(ConnectorBusinessMessage.class));

            processor.process(confirmationMessage);

            verify(evidenceRepository, never()).save(any(), any());
        }
    }

    @Nested
    @DisplayName("when the referenced message is missing")
    class WhenReferenceIsMissing {

        @Test
        void should_forward_the_confirmation_without_updating_the_lifecycle() {
            var confirmationMessage = gatewayConfirmationMessage(
                List.of(EvidenceTestFixtures.createDeliveryEvidence())
            );

            when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID,
                ConnectorMessageDirection.revert(confirmationMessage.direction())
            )).thenReturn(null);

            processor.process(confirmationMessage);

            verifyNoInteractions(evidenceVerifier, evidenceRepository);
            verify(linkSubmitter).submit(confirmationMessage);
        }
    }

    private static ConnectorBusinessMessage referencedBusinessMessage() {
        var base = BusinessMessageTestFixtures.createOutboundMessage();
        return base.toBuilder()
                   .identifier(REFERENCED_MESSAGE_ID)
                   .backendMessageIdentifier(BACKEND_MESSAGE_ID)
                   .as4Properties(
                       base.as4Properties()
                           .toBuilder()
                           .ebmsMessageIdentifier(REFERENCED_EBMS_ID)
                           .build()
                   )
                   .build();
    }

    private static ConnectorEvidenceMessage gatewayConfirmationMessage(
        List<ConnectorMessageEvidence> evidences) {
        return ConnectorEvidenceMessage.builder()
                                       .identifier(CONFIRMATION_MESSAGE_ID)
                                       .businessDomainIdentifier(
                                           BusinessMessageTestFixtures.createOutboundMessage()
                                                                      .businessDomainIdentifier()
                                       )
                                       .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                       .as4Properties(
                                           AS4PropertiesTestFixtures.createAS4Properties()
                                                                    .toBuilder()
                                                                    .referenceToIdentifier(REFERENCED_EBMS_ID)
                                                                    .ebmsMessageIdentifier("evidence-ebms@domibus.eu")
                                                                    .build()
                                       )
                                       .transportedEvidences(evidences)
                                       .build();
    }
}
