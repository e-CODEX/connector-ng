/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.RepositoryContextConfiguration;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageErrorRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("DataFlowIssue")
@SpringBootTest(classes = RepositoryContextConfiguration.class)
public class ConnectorMessageErrorRepositoryTest {
    @Autowired
    private ConnectorMessageErrorRepository repository;

    // saving

    @Test
    void should_throw_exception_when_saving_error_with_null_message_identifier() {
        assertThrows(
                NullPointerException.class, () -> repository.save(null, List.of())
        );
    }

    @Test
    void should_throw_exception_when_saving_error_with_null_errors() {
        assertThrows(
                NullPointerException.class, () -> repository.save("message-identifier", null)
        );
    }

    @Test
    void should_throw_exception_when_saving_error_with_null_message_identifier_and_errors() {
        assertThrows(
                NullPointerException.class, () -> repository.save(null, null)
        );
    }

    @Test
    void should_throw_exception_when_saving_error_with_empty_errors() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save("message-identifier", List.of())
        );
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
    })
    void should_save_message_errors_successfully() {
        var savedErrors = repository.save(
                "7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu",
                errors()
        );

        assertThat(savedErrors).isNotNull();
        assertThat(savedErrors).hasSize(1);
        assertThat(savedErrors.getFirst().label()).isEqualTo("error-label");
    }

    private List<ConnectorMessageError> errors() {
        return List.of(
                ConnectorMessageError.builder()
                                     .label("error-label")
                                     .details("error-details")
                                     .source("error-source")
                                     .build()
        );
    }
}
