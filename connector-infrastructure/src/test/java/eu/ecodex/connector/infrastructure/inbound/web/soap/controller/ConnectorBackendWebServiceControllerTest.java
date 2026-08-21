/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.soap.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.SoapMessageSubmitTestFixtures;
import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.port.api.message.ConnectorListPendingMessageIds;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.api.transport.ConnectorAckMessageTransportStep;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveMessageByTransportId;
import eu.ecodex.connector.application.port.api.transport.ConnectorSetMessagesTransportStepToDownload;
import eu.ecodex.connector.application.service.message.ConnectorListPendingMessagesService;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.EmptyRequestType;
import eu.ecodex.connector.domain.transition.GetMessageByIdRequest;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.soap.interceptor.ProcessMessageAfterDownload;
import eu.ecodex.connector.infrastructure.inbound.web.soap.interceptor.ProcessMessagesAfterDownload;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import jakarta.xml.ws.WebServiceContext;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorBackendWebServiceController")
public class ConnectorBackendWebServiceControllerTest {
    private static final String TRANSPORT_ID =
        "2921bed4-5587-488b-93a2-c048bc130a12@connector.ecodex.eu_backend_alice";

    @Mock
    private ConnectorOutboundBusinessMessageReceiver messageStagingService;
    @Mock
    private ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageProcessor;
    @Mock
    private ConnectorListPendingMessageIds listPendingMessageIdsService;
    @Mock
    private ConnectorListPendingMessagesService listPendingMessagesService;
    @Mock
    private ConnectorRetrieveMessageByTransportId retrieveMessageByTransportIdService;
    @Mock
    private ConnectorUploadAttachments uploadAttachmentsService;
    @Mock
    private ConnectorBackendClientVerifier backendClientVerifierService;
    @Mock
    private ConnectorSetMessagesTransportStepToDownload changePendingMessagesStatusService;
    @Mock
    private ConnectorAckMessageTransportStep acknowledgeMessageTransportStepService;
    @Mock
    private LegacyMessageHelper legacyMessageHelper;
    @Mock
    private WrappedMessageContext wrappedMessageContext;
    @Mock
    private Message cxfMessage;
    @Mock
    private InterceptorChain interceptorChain;
    @Mock
    private WebServiceContext webServiceContext;

    private DomibusConnectorBackendWebService backendWebService;

    @BeforeEach
    void setUp() {
        backendWebService = new ConnectorBackendWebServiceController(
            messageStagingService,
            outboundEvidenceMessageProcessor,
            listPendingMessageIdsService,
            listPendingMessagesService,
            retrieveMessageByTransportIdService,
            uploadAttachmentsService,
            changePendingMessagesStatusService,
            acknowledgeMessageTransportStepService,
            backendClientVerifierService,
            legacyMessageHelper
        );
        // @Resource field isn't constructor-injected, so set it manually
        ReflectionTestUtils.setField(backendWebService, "webServiceContext", webServiceContext);
    }

    private void stubAuthenticatedBackendClient() {
        when(webServiceContext.getUserPrincipal()).thenReturn((UserPrincipal) () -> "CN=alice");
        when(backendClientVerifierService.getBackendClient(any()))
            .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());
    }

    @Nested
    @DisplayName("submitMessage (backend -> connector)")
    class SubmitMessage {
        @Test
        void should_return_a_successful_ack() {
            stubAuthenticatedBackendClient();
            when(uploadAttachmentsService.execute(any()))
                .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));
            when(messageStagingService.execute(any()))
                .thenReturn(BusinessMessageTestFixtures.createOutboundMessage());

            var payload = SoapMessageSubmitTestFixtures.createBackendToConnectorMessage();
            var ack = backendWebService.submitMessage(payload);

            assertThat(ack).isNotNull();
            assertThat(ack.getMessageId())
                .isEqualTo("223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
            assertThat(ack.getResultMessage()).isNullOrEmpty();
            assertThat(ack.isResult()).isTrue();
        }

        @Test
        void should_return_a_failure_ack_when_an_exception_occurs() {
            stubAuthenticatedBackendClient();
            when(uploadAttachmentsService.execute(any()))
                .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));
            when(messageStagingService.execute(any()))
                .thenThrow(new RuntimeException("Error"));

            var payload =
                SoapMessageSubmitTestFixtures.createBackendToConnectorMessageWithoutAttachment();
            var ack = backendWebService.submitMessage(payload);

            assertThat(ack).isNotNull();
            assertThat(ack.getMessageId()).isNullOrEmpty();
            assertThat(ack.getResultMessage()).isNotNull();
            assertThat(ack.isResult()).isFalse();
        }
    }

    @Nested
    @DisplayName("listPendingMessageIds")
    class ListPendingMessageIds {
        @Test
        void should_return_the_pending_message_ids() {
            stubAuthenticatedBackendClient();
            when(listPendingMessageIdsService.execute(any())).thenReturn(List.of(TRANSPORT_ID));

            var response = backendWebService.listPendingMessageIds(new EmptyRequestType());

            assertThat(response).isNotNull();
            assertThat(response.getMessageTransportIds()).containsExactly(TRANSPORT_ID);
        }
    }

    @Nested
    @DisplayName("getMessageById")
    class GetMessageById {
        @Test
        void should_return_the_message() {
            var connectorMessage = BusinessMessageTestFixtures.createInboundMessage();

            when(retrieveMessageByTransportIdService.execute(TRANSPORT_ID))
                .thenReturn(connectorMessage);
            when(webServiceContext.getMessageContext()).thenReturn(wrappedMessageContext);
            when(wrappedMessageContext.getWrappedMessage()).thenReturn(cxfMessage);
            when(cxfMessage.getInterceptorChain()).thenReturn(interceptorChain);

            var expectedResult = new DomibusConnectorMessageType();
            when(legacyMessageHelper.convertMessage(connectorMessage)).thenReturn(expectedResult);

            var request = new GetMessageByIdRequest();
            request.setMessageTransportId(TRANSPORT_ID);

            var response = backendWebService.getMessageById(request);

            assertThat(response).isNotNull();
            verify(retrieveMessageByTransportIdService).execute(TRANSPORT_ID);
            verify(legacyMessageHelper).convertMessage(connectorMessage);

            var interceptorCaptor = ArgumentCaptor.forClass(ProcessMessageAfterDownload.class);
            verify(interceptorChain).add(interceptorCaptor.capture());
            assertThat(interceptorCaptor.getValue()).isInstanceOf(ProcessMessageAfterDownload.class);
        }

        @Test
        void should_fail_when_the_message_is_not_found() {
            var request = new GetMessageByIdRequest();
            request.setMessageTransportId("UNKNOWN-ID");

            when(retrieveMessageByTransportIdService.execute("UNKNOWN-ID"))
                .thenThrow(new NotFoundException("not found"));

            assertThatThrownBy(() -> backendWebService.getMessageById(request))
                .isInstanceOf(NotFoundException.class);

            verifyNoInteractions(webServiceContext, legacyMessageHelper);
        }
    }

    @Nested
    @DisplayName("requestMessages (list pending messages)")
    class RequestMessages {
        @Test
        void should_return_the_pending_messages() {
            var connectorMessage = BusinessMessageTestFixtures.createInboundMessage();

            when(listPendingMessagesService.execute("backend_alice"))
                .thenReturn(List.of(connectorMessage));
            stubAuthenticatedBackendClient();
            when(webServiceContext.getMessageContext()).thenReturn(wrappedMessageContext);
            when(wrappedMessageContext.getWrappedMessage()).thenReturn(cxfMessage);
            when(cxfMessage.getInterceptorChain()).thenReturn(interceptorChain);

            var expectedResult = new DomibusConnectorMessageType();
            when(legacyMessageHelper.convertMessage(connectorMessage)).thenReturn(expectedResult);

            var response = backendWebService.requestMessages(new EmptyRequestType());

            assertThat(response).isNotNull();
            verify(listPendingMessagesService).execute("backend_alice");
            verify(legacyMessageHelper).convertMessage(connectorMessage);

            var interceptorCaptor = ArgumentCaptor.forClass(ProcessMessagesAfterDownload.class);
            verify(interceptorChain).add(interceptorCaptor.capture());
            assertThat(interceptorCaptor.getValue())
                .isInstanceOf(ProcessMessagesAfterDownload.class);
        }
    }

    @Nested
    @DisplayName("acknowledgeMessage")
    class AcknowledgeMessage {
        @Test
        void should_acknowledge_a_successful_message() {
            doNothing().when(acknowledgeMessageTransportStepService).execute(any(), any());

            var response = backendWebService.acknowledgeMessage(acknowledgeMessage(true));

            assertThat(response).isNotNull();
        }

        @Test
        void should_acknowledge_a_failed_message() {
            doNothing().when(acknowledgeMessageTransportStepService).execute(any(), any());

            var response = backendWebService.acknowledgeMessage(acknowledgeMessage(false));

            assertThat(response).isNotNull();
        }
    }

    private DomibusConnectorMessageResponseType acknowledgeMessage(boolean result) {
        var ackResponse = new DomibusConnectorMessageResponseType();
        ackResponse.setResult(result);
        ackResponse.setAssignedMessageId("12345678-1234-1234-1234-123456789012");
        ackResponse.setResponseForMessageId(
            "3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu");
        ackResponse.setResultMessage("Message acknowledged successfully");
        return ackResponse;
    }
}
