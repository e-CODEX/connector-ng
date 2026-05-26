/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.soap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.time.LocalDateTime;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:sql/business-domain.sql",
                "classpath:sql/processing-mode.sql",
                "classpath:sql/party.sql",
                "classpath:sql/service.sql",
                "classpath:sql/action.sql",
                "classpath:sql/message.sql",
                "classpath:sql/message-as4-properties.sql",
                "classpath:sql/message-transport-step.sql",
                "classpath:sql/message-transport-step-statuses.sql",
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class AcknowledgeMessageIT extends AbstractIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @LocalServerPort
    private int port;

    private DomibusConnectorBackendWebService soapClient;

    @BeforeEach
    void setUp() {
        soapClient = createClient();
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_step_statuses");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_transport_steps");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_document_signatures");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_documents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_business_contents");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_as4_properties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_messages");
        jdbcTemplate.execute("TRUNCATE TABLE connector_message_attachments");
        jdbcTemplate.execute("TRUNCATE TABLE connector_parties");
        jdbcTemplate.execute("TRUNCATE TABLE connector_services");
        jdbcTemplate.execute("TRUNCATE TABLE connector_actions");
        jdbcTemplate.execute("TRUNCATE TABLE connector_processing_modes");
        jdbcTemplate.execute("TRUNCATE TABLE connector_keystores");
        jdbcTemplate.execute("TRUNCATE TABLE connector_business_domains");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    void should_failed_to_acknowledge_unknown_message() {
        var request = acknowledgeMessage(true);
        request.setResponseForMessageId("unknown-transport-id");

        assertThatThrownBy(() -> soapClient.acknowledgeMessage(request))
                .isInstanceOf(SOAPFaultException.class);
    }

    @Test
    void should_acknowledge_message_with_success_status_successfully() {
        var request = acknowledgeMessage(true);
        var response = soapClient.acknowledgeMessage(request);
        assertThat(response).isNotNull();

        var deliveredToBackendAt = jdbcTemplate.queryForObject(
                "SELECT delivered_to_backend_at FROM connector_messages WHERE identifier = ?",
                LocalDateTime.class,
                request.getResponseForMessageId()
        );
        assertThat(deliveredToBackendAt).isNotNull();
    }

    @Test
    void should_failed_to_acknowledge_message_with_failed_status_and_no_error() {
        var request = acknowledgeMessage(false);

        assertThatThrownBy(() -> soapClient.acknowledgeMessage(request))
                .isInstanceOf(SOAPFaultException.class);
    }

    @Test
    void should_acknowledge_message_with_failed_status_successfully() {
        var error = new DomibusConnectorMessageErrorType();
        error.setErrorMessage("Error message");
        error.setErrorDetails("Error details");
        error.setErrorSource("Error source");
        var request = acknowledgeMessage(true);
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

    private DomibusConnectorBackendWebService createClient() {
        var address = "http://localhost:" + port + "/services/backend";

        var factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DomibusConnectorBackendWebService.class);
        factory.setAddress(address);
        factory.setWsdlURL("classpath:wsdl/v1/DomibusConnectorBackendWebService.wsdl");

        return (DomibusConnectorBackendWebService) factory.create();
    }
}
