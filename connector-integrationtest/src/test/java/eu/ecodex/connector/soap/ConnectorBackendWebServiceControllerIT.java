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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.SoapMessageSubmitTestFixtures;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = {
                "classpath:sql/business-domain.sql",
                "classpath:sql/processing-mode.sql",
                "classpath:sql/party.sql",
                "classpath:sql/service.sql",
                "classpath:sql/action.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ConnectorBackendWebServiceControllerIT extends AbstractIntegrationTest {
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
    void should_submit_soap_outbound_message_successfully() {
        var message = SoapMessageSubmitTestFixtures.createBackendToConnectorMessage();

        var ack = soapClient.submitMessage(message);

        assertThat(ack).isNotNull();
        assertThat(ack.isResult()).isTrue();
        assertThat(ack.getMessageId()).isNotBlank();
    }

    @Test
    void should_submit_soap_outbound_message_without_attachment_successfully() {
        var message = SoapMessageSubmitTestFixtures.createBackendToConnectorMessageWithoutAttachment();

        var ack = soapClient.submitMessage(message);

        assertThat(ack).isNotNull();
        assertThat(ack.isResult()).isTrue();
        assertThat(ack.getMessageId()).isNotBlank();
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
