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
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.api.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendDeliveryWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.inbound.jms.listener.outbound.ConnectorJmsBackendMessageDeliveryListener;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorBackendDeliveryServiceClient;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorJmsBackendMessageDeliveryListener")
@ExtendWith(MockitoExtension.class)
public class ConnectorJmsBackendMessageDeliveryListenerTest {
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
    @Mock
    private ConnectorOutboundEvidenceMessageReceiver outboundMessageReceiverService;

    @InjectMocks
    private ConnectorJmsBackendMessageDeliveryListener listener;

    private ConnectorMessageAS4Properties as4Properties() {
        return ConnectorMessageAS4Properties
            .builder()
            .service(
                ConnectorService.builder()
                                .name("EPO")
                                .type("urn:e-codex:services:")
                                .build()
            )
            .action(
                ConnectorAction.builder()
                               .name("Form_A")
                               .build()
            )
            .fromParty(
                ConnectorParty.builder()
                              .identifier("BL")
                              .identifierType(
                                  "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
                              )
                              .role("GW")
                              .roleType(ConnectorPartyRoleType.INITIATOR)
                              .build()
            )
            .toParty(
                ConnectorParty.builder()
                              .identifier("RE")
                              .identifierType(
                                  "urn:oasis:names:tc:ebcore:partyid-type:ecodex"
                              )
                              .role("GW")
                              .roleType(ConnectorPartyRoleType.RESPONDER)
                              .build()
            )
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
                                          .build()
            )
            .build();
    }

    private ConnectorBusinessMessage inboundMessage() {
        return ConnectorBusinessMessage
            .builder()
            .businessDomainIdentifier(
                BusinessDomainTestFixtures
                    .createDefaultBusinessDomain()
                    .identifier()
            )
            .identifier(MESSAGE_ID)
            .backendName(BACKEND_NAME)
            .backendMessageIdentifier(null)
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .as4Properties(as4Properties())
            .businessContent(businessContent())
            .attachments(List.of())
            .evidences(List.of())
            .transportedEvidences(
                List.of(
                    EvidenceTestFixtures.createSubmissionAcceptanceEvidence()
                )
            )
            .build();
    }

    private ConnectorBusinessMessage triggerBusinessMessage() {
        return inboundMessage();
    }

    private ConnectorEvidenceMessage triggerEvidenceMessage() {
        return EvidenceMessageTestFixtures
            .createSubmissionAcceptanceEvidenceMessage()
            .switchDirection();
    }

    private ConnectorLinkPartner linkPartner() {
        return ConnectorLinkPartner
            .builder()
            .senderMode(ConnectorLinkMode.PUSH)
            .build();
    }

    /**
     * Stubs the common happy-path chain up to deliveryWebService.
     */
    private void stubHappyPath() {
        when(backendServiceClient.createClient(any()))
            .thenReturn(deliveryWebService);

        when(legacyMessageHelper.convertMessage(any()))
            .thenReturn(mock(DomibusConnectorMessageType.class));
    }

    @Nested
    @DisplayName("invalid JMS message")
    class InvalidMessages {
        @Test
        void should_reject_null_message() {
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
        void should_fail_when_backend_is_unknown() {
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
    }

    @Nested
    @DisplayName("push delivery")
    class PushDelivery {
        @Test
        void should_deliver_business_message_successfully() {
            ReflectionTestUtils.setField(listener, "autoTriggerDeliveryEvidences", true);
            stubHappyPath();
            when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
            when(outboundMessageReceiverService.execute(any())).thenReturn(null);

            var acknowledgement = mock(DomibsConnectorAcknowledgementType.class);
            when(acknowledgement.isResult()).thenReturn(true);
            when(acknowledgement.getMessageId()).thenReturn("backend-message-id");
            when(deliveryWebService.deliverMessage(any())).thenReturn(acknowledgement);
            when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

            listener.handle(triggerBusinessMessage());

            verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(messageRepository).setDeliveredToLinkPartnerAt(MESSAGE_ID);
            verify(messageRepository).updateBackendIdentifier(
                MESSAGE_ID,
                "backend-message-id"
            );
            verify(messageRepository, never()).setAsRejected(any());
            verify(outboundMessageReceiverService).execute(any());
        }

        @Test
        void should_deliver_evidence_message_successfully() {
            stubHappyPath();
            when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());

            var acknowledgement = mock(DomibsConnectorAcknowledgementType.class);
            when(acknowledgement.isResult()).thenReturn(true);
            when(deliveryWebService.deliverMessage(any())).thenReturn(acknowledgement);
            when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

            listener.handle(triggerEvidenceMessage());

            verify(evidenceRepository).setDeliveredToLinkPartnerAt(any());
            verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.DELIVERED)
            );
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
            verify(messageRepository, never()).setAsRejected(any());
            verify(outboundMessageReceiverService, never()).execute(any());
        }

        @Test
        void should_mark_business_message_as_rejected_when_backend_rejects_it() {
            stubHappyPath();
            when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());

            var acknowledgement = mock(DomibsConnectorAcknowledgementType.class);
            when(acknowledgement.isResult()).thenReturn(false);
            when(deliveryWebService.deliverMessage(any())).thenReturn(acknowledgement);
            when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

            listener.handle(triggerBusinessMessage());

            verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.FAILED)
            );
            verify(messageRepository).setAsRejected(MESSAGE_ID);
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());
        }

        @Test
        void should_mark_delivery_as_failed_when_backend_submission_throws_exception() {
            stubHappyPath();
            when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner());
            when(deliveryWebService.deliverMessage(any()))
                .thenThrow(new RuntimeException());
            when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

            assertThatNoException()
                .isThrownBy(() -> listener.handle(triggerBusinessMessage()));

            verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.FAILED)
            );
            verify(evidenceRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
            verify(messageRepository, never()).setAsRejected(any());
        }
    }

    @Nested
    @DisplayName("pull delivery")
    class PullDelivery {
        @Test
        void should_make_business_message_ready_for_download() {
            var linkPartner = linkPartner()
                .toBuilder()
                .senderMode(ConnectorLinkMode.PULL)
                .build();

            when(linkPartnerRepository.findByName(any())).thenReturn(linkPartner);
            when(registerMessageTransportStep.execute(any(), any())).thenReturn(any());

            listener.handle(triggerBusinessMessage());

            verify(registerMessageTransportStep).execute(
                any(),
                eq(ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD)
            );
            verify(messageRepository, never()).setDeliveredToLinkPartnerAt(any());
            verify(messageRepository, never()).updateBackendIdentifier(any(), any());
            verify(messageRepository, never()).setAsRejected(any());
        }
    }
}
