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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("DataFlowIssue")
@SpringBootTest(classes = RepositoryContextConfiguration.class)
public class ConnectorMessageTransportStepRepositoryTest {
    private static final String STEP_IDENTIFIER = "8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice";
    private static final String MESSAGE_IDENTIFIER = "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";

    @Autowired
    private ConnectorMessageTransportStepRepository repository;

    @Test
    void should_throw_null_pointer_exception_when_saving_if_the_transport_step_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.save(null)
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
    void should_create_a_new_transport_step_successfully() {
        var transportStep = this.repository.save(generateTransportStep(MESSAGE_IDENTIFIER));

        assertThat(transportStep).isNotNull();
        assertThat(transportStep.identifier()).isEqualTo(STEP_IDENTIFIER);
        assertThat(transportStep.identifier()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_with_null_transport_step_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.update(STEP_IDENTIFIER, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_with_null_transport_step() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.update(
                        null,
                        generateTransportStep(MESSAGE_IDENTIFIER)
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_with_null_transport_step_and_identifier() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.update(null, null)
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
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_update_an_existing_transport_step_successfully() {
        var transportStep = generateTransportStep(MESSAGE_IDENTIFIER)
                .toBuilder()
                .status(ConnectorMessageTransportStatus.DOWNLOADED)
                .build();

        var updatedStep = this.repository.update(
                STEP_IDENTIFIER,
                transportStep
        );

        assertThat(updatedStep).isNotNull();
        assertThat(updatedStep.identifier()).isEqualTo(STEP_IDENTIFIER);
        assertThat(updatedStep.statuses().size()).isEqualTo(2);
    }

    // update status

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_status_with_null_identifiers() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.updateStatus(null, ConnectorMessageTransportStatus.DOWNLOADED)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_status_with_null_status() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.updateStatus(
                        List.of("b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice"),
                        null
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_transport_step_status_with_null_identifiers_and_status() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.updateStatus(null, null)
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
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_update_transport_step_status_successfully() {
        this.repository.updateStatus(
                List.of("b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice"),
                ConnectorMessageTransportStatus.DOWNLOADED
        );

        var updatedTransportStep = this.repository.findByIdentifier("b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice");
        assertThat(updatedTransportStep).isNotNull();
        assertThat(updatedTransportStep.status()).isEqualTo(ConnectorMessageTransportStatus.DOWNLOADED);
        assertThat(updatedTransportStep.statuses().size()).isEqualTo(2);
    }

    // find pending messages IDs

    @Test
    void should_throw_null_pointer_exception_when_searching_pending_messages_ids_with_null_backend_name() {
        assertThrows(
                NullPointerException.class,
                () -> this.repository.findPendingMessagesIds(null)
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
            "classpath:sql/message-transport-step.sql",
            "classpath:sql/message-transport-step-statuses.sql",
    })
    void should_find_pending_messages_ids_successfully() {
        var messagesIds = this.repository.findPendingMessagesIds("backend_alice");

        assertThat(messagesIds).isNotNull();
        assertThat(messagesIds.size()).isEqualTo(1);
    }

    private ConnectorMessageTransportStep generateTransportStep(String messageIdentifier) {
        return ConnectorMessageTransportStep.builder()
                                            .identifier(STEP_IDENTIFIER)
                                            .numberOfAttempts(0)
                                            .status(ConnectorMessageTransportStatus.PENDING)
                                            .transportedMessage(
                                                    ConnectorMessage.builder()
                                                                    .identifier(messageIdentifier)
                                                                    .build()
                                            )
                                            .build();
    }
}
