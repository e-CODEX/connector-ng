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

import eu.ecodex.connector.SoapMessageSubmitTestFixtures;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.soap.BackendServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorSubmitMessageIT extends BackendServiceTest {
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
            "classpath:sql/action.sql"
    })
    void should_submit_soap_outbound_message_successfully() {
        var message = SoapMessageSubmitTestFixtures.createBackendToConnectorMessage();

        var ack = soapClient.submitMessage(message);

        assertThat(ack).isNotNull();
        assertThat(ack.isResult()).isTrue();
        assertThat(ack.getMessageId()).isNotBlank();
    }

    @Test
    @Sql({
            "classpath:sql/business-domain.sql",
            "classpath:sql/processing-mode.sql",
            "classpath:sql/party.sql",
            "classpath:sql/service.sql",
            "classpath:sql/action.sql"
    })
    void should_submit_soap_outbound_message_without_attachment_successfully() {
        var message = SoapMessageSubmitTestFixtures.createBackendToConnectorMessageWithoutAttachment();

        var ack = soapClient.submitMessage(message);

        assertThat(ack).isNotNull();
        assertThat(ack.isResult()).isTrue();
        assertThat(ack.getMessageId()).isNotBlank();
    }
}
