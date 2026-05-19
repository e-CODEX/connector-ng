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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageAttachmentTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.SoapMessageSubmitTestFixtures;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.service.usecase.message.ConnectorListPendingMessageIds;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.EmptyRequestType;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorBackendWebServiceControllerTest {
    @Mock
    private ConnectorOutboundMessageReceiver messageStagingService;
    @Mock
    private ConnectorListPendingMessageIds listPendingMessageIdsService;
    @Mock
    private ConnectorUploadAttachments uploadAttachmentsService;
    @Mock
    private ConnectorBackendClientVerifier backendClientVerifierService;

    private DomibusConnectorBackendWebService backendWebService;

    @BeforeEach
    void setUp() {
        backendWebService = new ConnectorBackendWebServiceController(
                messageStagingService,
                listPendingMessageIdsService,
                uploadAttachmentsService,
                backendClientVerifierService
        );
    }

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

    @Test
    void should_list_pending_messages_identifiers_successfully() {
        when(listPendingMessageIdsService.execute(any()))
                .thenReturn(List.of(
                        "2921bed4-5587-488b-93a2-c048bc130a12@connector.ecodex.eu_backend_alice"));

        var response = backendWebService.listPendingMessageIds(new EmptyRequestType());

        assertThat(response).isNotNull();
        assertThat(response.getMessageTransportIds()).isNotNull();
        assertThat(response.getMessageTransportIds().size()).isEqualTo(1);
        assertThat(response.getMessageTransportIds().getFirst())
                .isEqualTo("2921bed4-5587-488b-93a2-c048bc130a12@connector.ecodex.eu_backend_alice");
    }
}
