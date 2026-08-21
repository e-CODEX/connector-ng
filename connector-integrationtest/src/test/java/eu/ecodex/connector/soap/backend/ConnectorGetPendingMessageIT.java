/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.soap.backend;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.api.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.GetMessageByIdRequest;
import eu.ecodex.connector.infrastructure.outbound.provider.ConnectorS3FileStorageProvider;
import eu.ecodex.connector.soap.BackendServiceTest;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("ConnectorGetPendingMessageIT SOAP")
@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorGetPendingMessageIT extends BackendServiceTest {
    private static final String TRANSPORT_MESSAGE_ID = "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";

    @LocalServerPort
    private int port;
    @MockitoBean
    private ConnectorRegisterMessageTransportStep registerMessageTransportStep;
    @MockitoBean
    private ConnectorS3FileStorageProvider s3FileStorageProvider;

    private DomibusConnectorBackendWebService soapClient;

    @BeforeEach
    void setUp() {
        soapClient = createClient(port);
    }

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_retrieve_pending_message_by_transport_step_identifier() {
        when(registerMessageTransportStep.execute(any(), any())).thenReturn(mock(
                ConnectorMessageTransportStep.class));
        when(s3FileStorageProvider.findByIdentifier(any())).thenReturn(new byte[] {1, 2, 3});

        var request = new GetMessageByIdRequest();
        request.setMessageTransportId(TRANSPORT_MESSAGE_ID);
        var response = soapClient.getMessageById(request);

        assertThat(response).isNotNull();
        assertThat(response.getMessageDetails().getFinalRecipient()).isEqualTo("alice");
        assertThat(response.getMessageDetails().getOriginalSender()).isEqualTo("bob");
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/attachment.sql",
            "classpath:sql/message-business-content.sql",
            "classpath:sql/message-business-document.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_fail_when_identifier_does_not_exist() {
        when(registerMessageTransportStep.execute(any(), any())).thenReturn(mock(
                ConnectorMessageTransportStep.class));

        var request = new GetMessageByIdRequest();
        request.setMessageTransportId("unknown-transport-id");

        assertThatThrownBy(() -> soapClient.getMessageById(request))
                .isInstanceOf(SOAPFaultException.class);
    }
}
