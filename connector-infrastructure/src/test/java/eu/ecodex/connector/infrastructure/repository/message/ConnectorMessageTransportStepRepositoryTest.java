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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorMessageTransportStepRepository")
public class ConnectorMessageTransportStepRepositoryTest extends AbstractRepositoryTest {
    private static final String STEP_IDENTIFIER =
        "8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice";
    private static final String MESSAGE_IDENTIFIER =
        "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu";
    private static final String PENDING_STEP_IDENTIFIER =
        "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";

    @Autowired
    private ConnectorMessageTransportStepRepository repository;

    private ConnectorMessageTransportStep generateTransportStep() {
        return ConnectorMessageTransportStep.builder()
                                            .identifier(STEP_IDENTIFIER)
                                            .numberOfAttempts(0)
                                            .status(ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD)
                                            .transportedMessage(
                                                BusinessMessageTestFixtures.createInboundMessage()
                                                                           .toBuilder()
                                                                           .identifier(
                                                                               MESSAGE_IDENTIFIER)
                                                                           .build()
                                            )
                                            .build();
    }

    /**
     * Reference data plus a seeded message and its AS4 properties.
     */
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
    })
    private @interface WithMessageData {
    }

    /**
     * Message data plus seeded transport steps and their statuses.
     */
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
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
    })
    private @interface WithTransportStepData {
    }

    @Nested
    @DisplayName("save a transport step")
    class Save {
        @Test
        void should_throw_when_the_transport_step_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.save(null)
            );
        }

        @Test
        @WithMessageData
        void should_create_a_new_transport_step() {
            var transportStep = repository.save(generateTransportStep());

            assertThat(transportStep).isNotNull();
            assertThat(transportStep.identifier()).isEqualTo(STEP_IDENTIFIER);
        }
    }

    @Nested
    @DisplayName("update a transport step")
    class Update {
        @Test
        void should_throw_when_the_transport_step_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.update(STEP_IDENTIFIER, null)
            );
        }

        @Test
        void should_throw_when_the_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.update(null, generateTransportStep())
            );
        }

        @Test
        void should_throw_when_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.update(null, null)
            );
        }

        @Test
        @WithTransportStepData
        void should_update_an_existing_transport_step() {
            var transportStep = generateTransportStep()
                .toBuilder()
                .status(ConnectorMessageTransportStatus.DOWNLOADED)
                .build();

            var updatedStep = repository.update(STEP_IDENTIFIER, transportStep);

            assertThat(updatedStep).isNotNull();
            assertThat(updatedStep.identifier()).isEqualTo(STEP_IDENTIFIER);
            assertThat(updatedStep.statuses()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("update the status")
    class UpdateStatus {
        @Test
        void should_throw_when_the_identifiers_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateStatus(null, ConnectorMessageTransportStatus.DOWNLOADED)
            );
        }

        @Test
        void should_throw_when_the_status_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateStatus(List.of(PENDING_STEP_IDENTIFIER), null)
            );
        }

        @Test
        void should_throw_when_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateStatus(null, null)
            );
        }

        @Test
        @WithTransportStepData
        void should_update_the_status() {
            repository.updateStatus(
                List.of(PENDING_STEP_IDENTIFIER),
                ConnectorMessageTransportStatus.DOWNLOADED
            );

            var updatedTransportStep = repository.findByIdentifier(PENDING_STEP_IDENTIFIER);

            assertThat(updatedTransportStep).isNotNull();
            assertThat(updatedTransportStep.status())
                .isEqualTo(ConnectorMessageTransportStatus.DOWNLOADED);
            assertThat(updatedTransportStep.statuses()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("find pending message ids")
    class FindPendingMessagesIds {
        @Test
        void should_throw_when_the_backend_name_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findPendingMessagesIds(null)
            );
        }

        @Test
        @WithTransportStepData
        void should_find_the_pending_message_ids() {
            var messagesIds = repository.findPendingMessagesIds("backend_alice");

            assertThat(messagesIds).isNotNull();
            assertThat(messagesIds).hasSize(1);
        }
    }
}
