/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.soap.controller;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.application.exception.businessdomain.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.infrastructure.adapter.in.web.rest.exception.ConnectorInternalServerException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ConnectorBackendWebServiceController result message")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorBackendWebServiceControllerResultMessageTest {
    private static final String GENERIC = "The message could not be processed";

    @Test
    void should_keep_the_message_of_an_application_exception() {
        var exception = new ConnectorBusinessDomainNotFoundException(
            "Business domain [unknown] not found");

        assertThat(ConnectorBackendWebServiceController.resultMessageOf(exception))
            .isEqualTo("Business domain [unknown] not found");
    }

    @Test
    void should_hide_the_message_of_an_io_exception() {
        var exception = new IOException("cannot write /srv/connector/upload/tmp-1.pdf");

        assertThat(ConnectorBackendWebServiceController.resultMessageOf(exception))
            .isEqualTo(GENERIC);
    }

    @Test
    void should_hide_the_message_of_an_unexpected_runtime_exception() {
        var exception = new IllegalStateException("jdbc:mysql://db.internal:3306 refused");

        assertThat(ConnectorBackendWebServiceController.resultMessageOf(exception))
            .isEqualTo(GENERIC);
    }

    @Test
    void should_hide_the_cause_of_an_internal_server_exception() {
        var exception = new ConnectorInternalServerException(
            "Unable to read the attachment", new IOException("/srv/secret"));

        assertThat(ConnectorBackendWebServiceController.resultMessageOf(exception))
            .isEqualTo(GENERIC);
    }

    @Test
    void should_handle_an_exception_without_message() {
        assertThat(ConnectorBackendWebServiceController.resultMessageOf(new RuntimeException()))
            .isEqualTo(GENERIC);
    }
}
