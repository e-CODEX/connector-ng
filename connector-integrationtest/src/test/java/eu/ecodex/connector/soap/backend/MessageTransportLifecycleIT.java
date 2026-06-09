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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.ecodex.connector.domain.transition.GetMessageByIdRequest;
import eu.ecodex.connector.soap.BackendServiceTest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class MessageTransportLifecycleIT extends BackendServiceTest {
    private static final String PENDING_TRANSPORT_ID =
            "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";
    private static final String DOWNLOADED_MESSAGE_ID =
            "3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu";
    private static final String ASSIGNED_BACKEND_MESSAGE_ID = "ebf72de4-05d2-4a7b-838a-5018923996da";

    @Autowired
    private JdbcTemplate jdbcTemplate;
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
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql",
            "classpath:sql/message.sql",
            "classpath:sql/message-as4-properties.sql",
            "classpath:sql/evidence.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_transition_from_pending_to_downloaded_when_message_is_retrieved() {
        assertThat(transportStepStatus(PENDING_TRANSPORT_ID)).isEqualTo("PENDING");

        var getMessageByIdRequest = new GetMessageByIdRequest();
        getMessageByIdRequest.setMessageTransportId(PENDING_TRANSPORT_ID);
        var getMessageByIdResponse = soapClient.getMessageById(getMessageByIdRequest);

        assertThat(getMessageByIdResponse).isNotNull();
        assertThat(transportStepStatus(PENDING_TRANSPORT_ID)).isEqualTo("DOWNLOADED");
        assertThat(transportStepStatusHistory(PENDING_TRANSPORT_ID))
                .containsExactly("PENDING", "DOWNLOADED");
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
            "classpath:sql/evidence.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_transition_from_downloaded_to_submitted_when_acknowledged_successfully() {
        assertThat(transportStepStatusByMessageId(DOWNLOADED_MESSAGE_ID)).isEqualTo("DOWNLOADED");

        var acknowledgementResponse = soapClient.acknowledgeMessage(successfulAcknowledgement());

        assertThat(acknowledgementResponse).isNotNull();
        assertThat(transportStepStatusByMessageId(DOWNLOADED_MESSAGE_ID)).isEqualTo("SUBMITTED");
        assertThat(transportStepStatusHistoryByMessageId(DOWNLOADED_MESSAGE_ID))
                .contains("PENDING", "DOWNLOADED", "SUBMITTED");
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
            "classpath:sql/evidence.sql",
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_transition_from_downloaded_to_failed_when_acknowledged_with_errors() {
        assertThat(transportStepStatusByMessageId(DOWNLOADED_MESSAGE_ID)).isEqualTo("DOWNLOADED");

        var acknowledgementResponse = soapClient.acknowledgeMessage(failedAcknowledgement());

        assertThat(acknowledgementResponse).isNotNull();
        assertThat(transportStepStatusByMessageId(DOWNLOADED_MESSAGE_ID)).isEqualTo("FAILED");
        assertThat(transportStepStatusHistoryByMessageId(DOWNLOADED_MESSAGE_ID))
                .contains("PENDING", "DOWNLOADED", "FAILED");
    }

    private DomibusConnectorMessageResponseType successfulAcknowledgement() {
        var acknowledgementResponse = new DomibusConnectorMessageResponseType();
        acknowledgementResponse.setResult(true);
        acknowledgementResponse.setAssignedMessageId(ASSIGNED_BACKEND_MESSAGE_ID);
        acknowledgementResponse.setResponseForMessageId(DOWNLOADED_MESSAGE_ID);
        acknowledgementResponse.setResultMessage("Message acknowledged successfully");
        return acknowledgementResponse;
    }

    private DomibusConnectorMessageResponseType failedAcknowledgement() {
        var messageError = new DomibusConnectorMessageErrorType();
        messageError.setErrorMessage("Error message");
        messageError.setErrorDetails("Error details");
        messageError.setErrorSource("Error source");

        var acknowledgementResponse = new DomibusConnectorMessageResponseType();
        acknowledgementResponse.setResult(false);
        acknowledgementResponse.setAssignedMessageId(ASSIGNED_BACKEND_MESSAGE_ID);
        acknowledgementResponse.setResponseForMessageId(DOWNLOADED_MESSAGE_ID);
        acknowledgementResponse.setResultMessage("Message acknowledgement failed");
        acknowledgementResponse.getMessageErrors().add(messageError);
        return acknowledgementResponse;
    }

    private String transportStepStatus(String transportIdentifier) {
        return jdbcTemplate.queryForObject(
                "SELECT status FROM connector_message_transport_steps WHERE identifier = ?",
                String.class,
                transportIdentifier
        );
    }

    private String transportStepStatusByMessageId(String messageIdentifier) {
        return jdbcTemplate.queryForObject(
                """
                        SELECT status FROM connector_message_transport_steps
                        WHERE transported_message_identifier = ?
                        """,
                String.class,
                messageIdentifier
        );
    }

    private List<String> transportStepStatusHistory(String transportIdentifier) {
        return jdbcTemplate.queryForList(
                """
                        SELECT status FROM connector_message_transport_step_statuses
                        WHERE transport_step_id = (
                            SELECT id FROM connector_message_transport_steps WHERE identifier = ?
                        )
                        ORDER BY created_at
                        """,
                String.class,
                transportIdentifier
        );
    }

    private List<String> transportStepStatusHistoryByMessageId(String messageIdentifier) {
        return jdbcTemplate.queryForList(
                """
                        SELECT status FROM connector_message_transport_step_statuses
                        WHERE transport_step_id = (
                            SELECT id FROM connector_message_transport_steps
                            WHERE transported_message_identifier = ?
                        )
                        ORDER BY created_at
                        """,
                String.class,
                messageIdentifier
        );
    }

}
