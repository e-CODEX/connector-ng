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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.ecodex.connector.soap.BackendServiceTest;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("ConnectorAcknowledgeMessageIT SOAP")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorAcknowledgeMessageIT extends BackendServiceTest {
    @LocalServerPort
    private int port;

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
    void should_failed_to_acknowledge_unknown_message() {
        var request = acknowledgeMessage(true);
        request.setResponseForMessageId("unknown-transport-id");

        assertThatThrownBy(() -> soapClient.acknowledgeMessage(request))
            .isInstanceOf(SOAPFaultException.class);
    }

    @Test
    @WithMessageData
    void should_acknowledge_message_with_success_status_successfully() {
        var request = acknowledgeMessage(true);
        var response = soapClient.acknowledgeMessage(request);
        assertThat(response).isNotNull();
    }

    @Test
    @WithMessageData
    void should_failed_to_acknowledge_message_with_failed_status_and_no_error() {
        var request = acknowledgeMessage(false);

        assertThatThrownBy(() -> soapClient.acknowledgeMessage(request))
            .isInstanceOf(SOAPFaultException.class);
    }

    @Test
    @WithMessageData
    void should_acknowledge_message_with_failed_status_successfully() {
        var error = new DomibusConnectorMessageErrorType();
        error.setErrorMessage("Error message");
        error.setErrorDetails("Error details");
        error.setErrorSource("Error source");
        var request = acknowledgeMessage(false);
        request.getMessageErrors().add(error);

        var response = soapClient.acknowledgeMessage(request);

        assertThat(response).isNotNull();
    }

    private DomibusConnectorMessageResponseType acknowledgeMessage(boolean result) {
        var ackResponse = new DomibusConnectorMessageResponseType();
        ackResponse.setResult(result);
        ackResponse.setAssignedMessageId("ebf72de4-05d2-4a7b-838a-5018923996da");
        ackResponse.setResponseForMessageId(
            "3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu");
        ackResponse.setResultMessage("Message acknowledged successfully");
        return ackResponse;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
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
        "classpath:sql/evidence.sql",
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
    })
    private @interface WithMessageData {
    }
}
