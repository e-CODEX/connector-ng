/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.evidence.ConnectorInboundEvidenceMessageProcessorService;
import eu.ecodex.connector.application.service.usecase.link.ConnectorLinkSubmitter;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.model.ConnectorErrorCode;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
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

    @Test
    void should_apply_delivery_evidence_and_forward_confirmation_message_to_backend() {
        var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
        var confirmationMessage = gatewayConfirmationMessage(
                REFERENCED_EBMS_ID,
                List.of(deliveryEvidence)
        );
        var referencedMessage = referencedBusinessMessage();

        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID, ConnectorMessageDirection.revert(confirmationMessage.direction())))
                .thenReturn(referencedMessage);
        when(messageRepository.findByIdentifier(REFERENCED_MESSAGE_ID))
                .thenReturn(referencedMessage);

        processor.process(confirmationMessage);

        verify(evidenceVerifier).verify(eq(ConnectorEvidenceType.DELIVERY), any(ConnectorMessage.class));
        verify(evidenceRepository).save(deliveryEvidence, REFERENCED_MESSAGE_ID);
    }

    @Test
    void should_forward_confirmation_message_without_lifecycle_update_when_reference_is_missing() {
        var confirmationMessage = gatewayConfirmationMessage(
                REFERENCED_EBMS_ID,
                List.of(EvidenceTestFixtures.createDeliveryEvidence())
        );
        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID, ConnectorMessageDirection.revert(confirmationMessage.direction()))
        ).thenReturn(null);

        processor.process(confirmationMessage);

        verifyNoInteractions(evidenceVerifier, evidenceRepository);
        verify(linkSubmitter).submit(confirmationMessage);
    }

    @Test
    void should_ignore_irrelevant_evidence_but_still_forward_confirmation_message() {
        var deliveryEvidence = EvidenceTestFixtures.createDeliveryEvidence();
        var confirmationMessage = gatewayConfirmationMessage(
                REFERENCED_EBMS_ID,
                List.of(deliveryEvidence)
        );
        var referencedMessage = referencedBusinessMessage();

        when(messageRepository.findByEbmsMessageIdentifierAndDirection(
                REFERENCED_EBMS_ID, ConnectorMessageDirection.revert(confirmationMessage.direction())))
                .thenReturn(referencedMessage);
        doThrow(new ConnectorEvidenceNotRelevantException(
                ConnectorErrorCode.EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY
        )).when(evidenceVerifier).verify(eq(ConnectorEvidenceType.DELIVERY), any(ConnectorMessage.class));

        processor.process(confirmationMessage);

        verify(evidenceRepository, never()).save(any(), any());
    }

    private static ConnectorMessage referencedBusinessMessage() {
        return MessageTestFixtures.createValidOutboundBusinessMessage()
                                  .toBuilder()
                                  .identifier(REFERENCED_MESSAGE_ID)
                                  .backendMessageIdentifier(BACKEND_MESSAGE_ID)
                                  .as4Properties(
                                          MessageTestFixtures.createValidOutboundBusinessMessage()
                                                             .as4Properties()
                                                             .toBuilder()
                                                             .ebmsMessageIdentifier(REFERENCED_EBMS_ID)
                                                             .build()
                                  )
                                  .build();
    }

    private static ConnectorMessage gatewayConfirmationMessage(
            String referenceToMessageId,
            List<ConnectorMessageEvidence> evidences) {
        return ConnectorMessage.builder()
                               .identifier(CONFIRMATION_MESSAGE_ID)
                               .businessDomainIdentifier(
                                       MessageTestFixtures.createValidOutboundBusinessMessage()
                                                          .businessDomainIdentifier()
                               )
                               .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                               .as4Properties(
                                       ConnectorMessageAS4Properties.builder()
                                                                    .referenceToIdentifier(
                                                                            referenceToMessageId)
                                                                    .ebmsMessageIdentifier(
                                                                            "evidence-ebms@domibus.eu")
                                                                    .build()
                               )
                               .transportedEvidences(evidences)
                               .build();
    }
}
