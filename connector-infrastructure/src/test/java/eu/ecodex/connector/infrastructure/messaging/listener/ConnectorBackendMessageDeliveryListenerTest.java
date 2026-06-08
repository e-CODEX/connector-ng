package eu.ecodex.connector.infrastructure.messaging.listener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendDeliveryWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.messaging.listener.outbound.ConnectorBackendMessageDeliveryListener;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorBackendDeliveryServiceClient;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorBackendMessageDeliveryListenerTest {
    private static final String MESSAGE_ID = "msg-001";
    private static final String BACKEND_NAME = "backend_alice";
    @Mock
    ConnectorMessageEvidenceRepository evidenceRepository;
    @Mock
    private ConnectorRegisterMessageTransportStep registerMessageTransportStep;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorBackendDeliveryServiceClient backendServiceClient;
    @Mock
    private DomibusConnectorBackendDeliveryWebService deliveryWebService;
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;
    @Mock
    private LegacyMessageHelper legacyMessageHelper;
    @InjectMocks
    private ConnectorBackendMessageDeliveryListener listener;

    @Test
    void should_throw_exception_when_handling_null_message() {
        assertThatThrownBy(() -> listener.handle(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(
                messageRepository,
                backendServiceClient,
                registerMessageTransportStep,
                linkPartnerRepository,
                evidenceRepository
        );
    }

    @Test
    void should_throw_exception_when_handling_message_without_identifier() {
        var message = ConnectorMessage.builder().identifier(null).build();

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(
                messageRepository,
                backendServiceClient,
                registerMessageTransportStep,
                linkPartnerRepository,
                evidenceRepository
        );
    }

    @Test
    void should_throw_exception_when_handling_non_evidence_or_non_business_message() {
        var message = ConnectorMessage.builder().identifier(MESSAGE_ID).build();

        assertThatThrownBy(() -> listener.handle(message))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(
                messageRepository,
                backendServiceClient,
                registerMessageTransportStep,
                linkPartnerRepository,
                evidenceRepository
        );
    }

    @Test
    void should_throw_exception_when_handling_message_with_unknown_backend_name() {
        when(linkPartnerRepository.findByName(any())).thenReturn(null);

        assertThatThrownBy(() -> listener.handle(inboundMessage()))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(
                messageRepository,
                backendServiceClient,
                registerMessageTransportStep,
                evidenceRepository
        );
    }

    @Test
    void should_submit_message_to_backend_and_mark_it_as_rejected_when_unsuccessful() {
        stubHappyPath(inboundMessage());
        when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
        var ack = mock(DomibsConnectorAcknowledgementType.class);
        when(ack.isResult()).thenReturn(false);
        when(deliveryWebService.deliverMessage(any())).thenReturn(ack);
        when(registerMessageTransportStep.execute(any(), any()))
                .thenReturn(any());

        listener.handle(triggerBusinessMessage());

        verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.FAILED)
        );
        verify(messageRepository).setAsRejected(MESSAGE_ID);
        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());
    }

    @Test
    void should_swallow_exception_if_one_occurred_during_the_message_submission() {
        stubHappyPath(inboundMessage());
        when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
        when(deliveryWebService.deliverMessage(any())).thenThrow(new RuntimeException());
        when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

        assertThatNoException().isThrownBy(() -> listener.handle(triggerBusinessMessage()));

        verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.FAILED)
        );
        verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());
        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        verify(messageRepository, never()).setAsRejected(any());
    }

    @Test
    void should_submit_a_business_message_to_backend_and_mark_it_as_delivered_when_successful() {
        stubHappyPath(inboundMessage());
        when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
        var ack = mock(DomibsConnectorAcknowledgementType.class);
        when(ack.isResult()).thenReturn(true);
        when(ack.getMessageId()).thenReturn("backend-message-id");
        when(deliveryWebService.deliverMessage(any())).thenReturn(ack);
        when(registerMessageTransportStep.execute(any(), any()))
                .thenReturn(any());

        listener.handle(triggerBusinessMessage());

        verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.SUBMITTED)
        );
        verify(messageRepository).setDeliveredToBackendAt(MESSAGE_ID);
        verify(messageRepository).updateBackendIdentifier(MESSAGE_ID, "backend-message-id");
        verify(messageRepository, never()).setAsRejected(any());
    }

    @Test
    void should_submit_an_evidence_message_to_backend_and_mark_the_evidence_as_delivered_when_successful() {
        stubHappyPath(inboundMessage());
        when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
        var ack = mock(DomibsConnectorAcknowledgementType.class);
        when(ack.isResult()).thenReturn(true);
        when(deliveryWebService.deliverMessage(any())).thenReturn(ack);
        when(registerMessageTransportStep.execute(any(), any()))
                .thenReturn(any());

        listener.handle(triggerEvidenceMessage());

        verify(evidenceRepository).setDeliveredToLinkPartnerAt(any());
        verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.SUBMITTED)
        );
        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        verify(messageRepository, never()).setAsRejected(any());
    }

    @Test
    void should_make_message_ready_for_pull_successfully() {
        var linkPartner = linkPartner().toBuilder().senderMode(ConnectorLinkMode.PULL).build();
        when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner);
        when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

        listener.handle(triggerBusinessMessage());

        verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.PENDING)
        );
        verify(messageRepository, never()).setDeliveredToBackendAt(any());
        verify(messageRepository, never()).updateBackendIdentifier(any(), any());
        verify(messageRepository, never()).setAsRejected(any());
    }

    private ConnectorMessageAS4Properties as4Properties() {
        return ConnectorMessageAS4Properties
                .builder()
                .service(ConnectorService.builder()
                                         .name("EPO")
                                         .type("urn:e-codex:services:")
                                         .build())
                .action(ConnectorAction.builder()
                                       .name("Form_A")
                                       .build())
                .fromParty(ConnectorParty.builder()
                                         .identifier("BL")
                                         .identifierType(
                                                 "urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                         .role("GW")
                                         .roleType(ConnectorPartyRoleType.INITIATOR)
                                         .build())
                .toParty(ConnectorParty.builder()
                                       .identifier("RE")
                                       .identifierType(
                                               "urn:oasis:names:tc:ebcore:partyid-type:ecodex")
                                       .role("GW")
                                       .roleType(ConnectorPartyRoleType.RESPONDER)
                                       .build())
                .conversationIdentifier("3d5ec775-6602-4bb0-a23c-0311ef8dabc8")
                .ebmsMessageIdentifier("50ef4a19-916f-4e38-bc86-4f85921e6f0a@domibus.eu")
                .referenceToIdentifier(null)
                .originalSender("bob")
                .finalRecipient("alice")
                .build();
    }

    private ConnectorMessageBusinessContent businessContent() {
        return ConnectorMessageBusinessContent
                .builder()
                .uuid(UUID.randomUUID().toString())
                .xmlContent(
                        ConnectorMessageAttachment.builder()
                                                  .identifier("xml-content-id")
                                                  .build())
                .build();
    }

    private ConnectorMessage inboundMessage() {
        return ConnectorMessage.builder()
                               .businessDomainIdentifier(
                                       BusinessDomainTestFixtures.createDefaultBusinessDomain()
                                                                 .identifier())
                               .identifier(MESSAGE_ID)
                               .backendName(BACKEND_NAME)
                               .backendMessageIdentifier(null)
                               .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                               .as4Properties(as4Properties())
                               .businessContent(businessContent())
                               .attachments(List.of())
                               .evidences(List.of(EvidenceTestFixtures.createSubmissionAcceptanceEvidence()))
                               .build();
    }

    private ConnectorMessage triggerBusinessMessage() {
        return inboundMessage();
    }

    private ConnectorMessage triggerEvidenceMessage() {
        return MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage().switchDirection();
    }

    private ConnectorLinkPartner linkPartner() {
        return ConnectorLinkPartner.builder().senderMode(ConnectorLinkMode.PUSH).build();
    }

    /**
     * Stubs the common happy-path chain up to deliveryWebService.
     */
    private void stubHappyPath(ConnectorMessage inbound) {
        when(backendServiceClient.createClient(any())).thenReturn(deliveryWebService);
        when(legacyMessageHelper.convertMessage(any())).thenReturn(mock(DomibusConnectorMessageType.class));
    }
}
