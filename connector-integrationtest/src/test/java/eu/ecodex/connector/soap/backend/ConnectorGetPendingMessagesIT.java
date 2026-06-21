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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import eu.ecodex.connector.application.service.usecase.transport.ConnectorChangePendingMessagesStatus;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.EmptyRequestType;
import eu.ecodex.connector.soap.BackendServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        statements = "DELETE FROM connector_business_domains WHERE id > 0",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorGetPendingMessagesIT extends BackendServiceTest {
    @LocalServerPort
    private int port;
    @MockitoBean
    private ConnectorChangePendingMessagesStatus changePendingMessagesStatus;

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
    void should_retrieve_all_pending_messages_successfully() {
        doNothing().when(changePendingMessagesStatus).execute(any(), any());

        var response = soapClient.requestMessages(new EmptyRequestType());

        assertThat(response).isNotNull();
        assertThat(response.getMessages()).isNotNull();
        assertThat(response.getMessages().size()).isEqualTo(1);
    }
}
