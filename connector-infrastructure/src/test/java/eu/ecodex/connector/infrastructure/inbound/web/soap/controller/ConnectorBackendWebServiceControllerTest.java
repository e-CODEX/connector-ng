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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.SoapMessageSubmitTestFixtures;
import eu.ecodex.connector.application.service.impl.message.ConnectorListPendingMessagesService;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.service.usecase.message.ConnectorListPendingMessageIds;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorAcknowledgeMessageTransportStep;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorChangePendingMessagesStatus;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorRetrieveMessageByTransportId;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
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
import java.util.List;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ConnectorBackendWebServiceControllerTest {
    private static final String TRANSPORT_ID = "2921bed4-5587-488b-93a2-c048bc130a12@connector.ecodex.eu_backend_alice";
    @Mock
    private ConnectorOutboundMessageReceiver messageStagingService;
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
    private ConnectorRegisterMessageTransportStep registerMessageTransportStep;
    @Mock
    private ConnectorChangePendingMessagesStatus changePendingMessagesStatusService;
    @Mock
    private ConnectorAcknowledgeMessageTransportStep updateMessageTransportStepService;
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
                listPendingMessageIdsService,
                listPendingMessagesService,
                retrieveMessageByTransportIdService,
                uploadAttachmentsService,
                registerMessageTransportStep,
                changePendingMessagesStatusService,
                updateMessageTransportStepService,
                backendClientVerifierService,
                legacyMessageHelper
        );
        // Inject @Resource field manually via reflection
        ReflectionTestUtils.setField(backendWebService, "webServiceContext", webServiceContext);
    }

    // submit message from backend to the connector

    @Test
    void should_return_successful_ack_when_submitting_message_from_backend_to_the_connector() {
        when(uploadAttachmentsService.execute(any()))
                .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));
        // TODO set appropriate response
        when(messageStagingService.register(any()))
                .thenReturn(MessageTestFixtures.createValidOutboundBusinessMessage());
        when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());

        var payload = SoapMessageSubmitTestFixtures.createBackendToConnectorMessage();
        var ack = backendWebService.submitMessage(payload);

        assertThat(ack).isNotNull();
        assertThat(ack.getMessageId()).isNotNull();
        assertThat(ack.getMessageId()).isEqualTo(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
        assertThat(ack.getMessageId()).isNotEmpty();
        assertThat(ack.getResultMessage()).isNullOrEmpty();
        assertThat(ack.isResult()).isTrue();
    }

    @Test
    void should_return_failure_ack_when_submitting_message_from_backend_to_the_connector_if_an_exception_occurs() {
        when(uploadAttachmentsService.execute(any()))
                .thenReturn(List.of(MessageAttachmentTestFixtures.createAttachment()));

        when(messageStagingService.register(any()))
                .thenThrow(new RuntimeException("Error"));

        var payload = SoapMessageSubmitTestFixtures.createBackendToConnectorMessageWithoutAttachment();
        var ack = backendWebService.submitMessage(payload);

        assertThat(ack).isNotNull();
        assertThat(ack.getMessageId()).isNullOrEmpty();
        assertThat(ack.getResultMessage()).isNotNull();
        assertThat(ack.isResult()).isFalse();
    }

    // list pending messages transport identifiers

    @Test
    void should_list_pending_messages_identifiers_successfully() {
        when(listPendingMessageIdsService.execute(any()))
                .thenReturn(List.of(
                        TRANSPORT_ID));

        var response = backendWebService.listPendingMessageIds(new EmptyRequestType());

        assertThat(response).isNotNull();
        assertThat(response.getMessageTransportIds()).isNotNull();
        assertThat(response.getMessageTransportIds().size()).isEqualTo(1);
        assertThat(response.getMessageTransportIds().getFirst())
                .isEqualTo(TRANSPORT_ID);
    }

    // get message by transport id
    @Test
    void should_retrieve_message_by_transport_id_successfully() {
        var connectorMessage = mock(ConnectorMessage.class);
        var expectedResult = new DomibusConnectorMessageType();

        when(retrieveMessageByTransportIdService.execute(TRANSPORT_ID)).thenReturn(connectorMessage);
        when(webServiceContext.getMessageContext()).thenReturn(wrappedMessageContext);
        when(wrappedMessageContext.getWrappedMessage()).thenReturn(cxfMessage);
        when(cxfMessage.getInterceptorChain()).thenReturn(interceptorChain);
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
    void should_throw_exception_when_retrieving_unknown_message_by_transport_id() {
        var request = new GetMessageByIdRequest();
        request.setMessageTransportId("UNKNOWN-ID");

        when(retrieveMessageByTransportIdService.execute("UNKNOWN-ID"))
                .thenThrow(new NotFoundException("not found"));

        assertThatThrownBy(() -> backendWebService.getMessageById(request))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(webServiceContext, legacyMessageHelper);
    }

    // list pending messages

    @Test
    void should_list_pending_messages_successfully() {
        var connectorMessage = mock(ConnectorMessage.class);
        var expectedResult = new DomibusConnectorMessageType();

        when(listPendingMessagesService.execute("backend_alice")).thenReturn(List.of(
                connectorMessage));
        when(backendClientVerifierService.getBackendClient(any()))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner().name().name());
        when(webServiceContext.getMessageContext()).thenReturn(wrappedMessageContext);
        when(wrappedMessageContext.getWrappedMessage()).thenReturn(cxfMessage);
        when(cxfMessage.getInterceptorChain()).thenReturn(interceptorChain);
        when(legacyMessageHelper.convertMessage(connectorMessage)).thenReturn(expectedResult);

        var response = backendWebService.requestMessages(new EmptyRequestType());

        assertThat(response).isNotNull();
        verify(listPendingMessagesService).execute("backend_alice");
        verify(legacyMessageHelper).convertMessage(connectorMessage);

        var interceptorCaptor = ArgumentCaptor.forClass(ProcessMessagesAfterDownload.class);
        verify(interceptorChain).add(interceptorCaptor.capture());
        assertThat(interceptorCaptor.getValue()).isInstanceOf(ProcessMessagesAfterDownload.class);
    }

    // acknowledge message

    @Test
    void should_acknowledge_message_with_success_status_successfully() {
        doNothing().when(updateMessageTransportStepService).execute(any(), any());

        var response = backendWebService.acknowledgeMessage(acknowledgeMessage(true));

        assertThat(response).isNotNull();
    }

    @Test
    void should_acknowledge_message_with_failed_status_successfully() {
        doNothing().when(updateMessageTransportStepService).execute(any(), any());

        var response = backendWebService.acknowledgeMessage(acknowledgeMessage(false));

        assertThat(response).isNotNull();
    }

    private DomibusConnectorMessageResponseType acknowledgeMessage(boolean result) {
        var ackResponse = new DomibusConnectorMessageResponseType();
        ackResponse.setResult(result);
        ackResponse.setAssignedMessageId("12345678-1234-1234-1234-123456789012");
        ackResponse.setResponseForMessageId("3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu");
        ackResponse.setResultMessage("Message acknowledged successfully");
        return ackResponse;
    }
}
