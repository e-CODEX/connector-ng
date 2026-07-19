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

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")
public class ConnectorMessageRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorMessageRepository repository;
    @Autowired
    private ConnectorMessageJpaRepository jpaRepository;

    // save
    @Test
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
        "classpath:sql/service.sql",
        "classpath:sql/action.sql"
    })
    void should_save_connector_message_successfully_to_database() {
        var message = MessageTestFixtures.createOutboundStagingBusinessMessage()
                                         .toBuilder()
                                         .identifier(
                                             "2adede33-41cf-4509-b0c1-0c83ad96eed7@connector.ecodex.eu")
                                         .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                         .build();

        var saved = this.repository.save(message);

        assertThat(saved).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_saving_connector_message_with_a_null_message_to_database() {
        assertThrows(
            NullPointerException.class, () -> repository.save(null)
        );
    }

    // update gateway name

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
    void should_update_connector_message_gateway_name_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.getGatewayName()).isEqualTo("default_gateway");

        var update = repository.updateGatewayName(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", "gateway-name");

        assertThat(update).isNotNull();
        assertThat(update.gatewayName()).isEqualTo("gateway-name");
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_gateway_name_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateGatewayName(null, "gateway-name")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_gateway_name_with_a_null_gateway_name() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateGatewayName(
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_gateway_name_with_a_null_message_identifier_and_gateway_name() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateGatewayName(null, null)
        );
    }

    // update backend name

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
    void should_update_connector_message_backend_name_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.getGatewayName()).isEqualTo("default_gateway");

        var update = repository.updateBackendName(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", "backend-name");

        assertThat(update).isNotNull();
        assertThat(update.backendName()).isEqualTo("backend-name");
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_backend_name_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateBackendName(null, "backend-name")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_backend_name_with_a_null_backend_name() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateBackendName(
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_backend_name_with_a_null_message_identifier_and_backend_name() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateBackendName(null, null)
        );
    }

    // update ebms identifier

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
    void should_update_connector_message_ebms_identifier_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.getAs4Properties().getEbmsMessageIdentifier()).isNull();

        var ebmsIdentifier = String.format("%s@%s", UUID.randomUUID(), "connector.ecodex.eu");

        var update = repository.updateEbmsIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", ebmsIdentifier);

        assertThat(update).isNotNull();
        assertThat(update.as4Properties().ebmsMessageIdentifier()).isEqualTo(ebmsIdentifier);
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_ebms_identifier_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateEbmsIdentifier(
                null, "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_ebms_identifier_with_a_null_ebms_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateEbmsIdentifier(
                "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu", null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_connector_message_ebms_identifier_with_a_null_message_identifier_and_ebms_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateEbmsIdentifier(null, null)
        );
    }

    // set as rejected

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
    void should_set_connector_message_as_rejected_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.getRejectedAt()).isNull();

        var update = repository.setAsRejected(message.getIdentifier());

        assertThat(update).isNotNull();
        assertThat(update.rejectedAt()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_setting_connector_message_as_rejected_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.setAsRejected(null)
        );
    }

    // set as confirmed

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
    void should_set_connector_message_as_confirmed_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");
        assertThat(message).isNotNull();
        assertThat(message.getConfirmedAt()).isNull();

        var update = repository.setAsConfirmed(message.getIdentifier());

        assertThat(update).isNotNull();
        assertThat(update.confirmedAt()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_setting_connector_message_as_confirmed_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.setAsRejected(null)
        );
    }

    // set submitted to gateway at

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
    void should_set_connector_message_submitted_to_gateway_at_successfully_in_the_database() {
        var message = jpaRepository.findByIdentifier(
            "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu");

        assertThat(message).isNotNull();
        assertThat(message.getDeliveredToGatewayAt()).isNull();

        var update = repository.setDeliveredToGatewayAt(message.getIdentifier());

        assertThat(update).isNotNull();
        assertThat(update.deliveredToGatewayAt()).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_setting_connector_message_as_submitted_to_gateway_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.setDeliveredToGatewayAt(null)
        );
    }

    // find by conversation identifier

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
    void should_find_message_by_conversation_identifier_successfully_from_database() {
        var messages = repository.findByConversationIdentifier(
            "5abe51ce-e94a-4df6-8b77-41b10bc47da7");

        assertThat(messages).isNotNull();
        assertThat(messages).hasSize(1);
    }

    @Test
    void should_return_empty_list_when_searching_message_by_unknown_conversation_identifier_from_database() {
        var messages = repository.findByConversationIdentifier("unknown-conversation-identifier");

        assertThat(messages).isNotNull();
        assertThat(messages).isEmpty();
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_messages_by_null_conversation_identifier_from_database() {
        assertThrows(
            NullPointerException.class, () -> repository.findByConversationIdentifier(null)
        );
    }

    // find all by IDs

    @Test
    void should_throw_null_pointer_exception_when_searching_messages_by_null_identifiers() {
        assertThrows(
            NullPointerException.class, () -> repository.findAllByIdentifier(null)
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
    void should_find_messages_by_identifiers() {
        var messages = repository.findAllByIdentifier(
            List.of("7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu")
        );

        assertThat(messages).isNotNull();
        assertThat(messages).hasSize(1);
    }

    // find by identifier

    @Test
    void should_throw_null_pointer_exception_when_searching_messages_by_null_identifier() {
        assertThrows(
            NullPointerException.class, () -> repository.findByIdentifier(null)
        );
    }

    @Test
    void should_return_null_when_searching_message_by_unknown_identifier_from_database() {
        var message = repository.findByIdentifier("unknown-identifier");
        assertThat(message).isNull();
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
        "classpath:sql/attachment.sql",
        "classpath:sql/evidence.sql",
    })
    void should_find_a_message_by_its_identifier_successfully() {
        var message = repository.findByIdentifier(
            "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu");

        assertThat(message).isNotNull();
        assertThat(message.identifier()).isEqualTo(
            "7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu");
        assertThat(message.direction()).isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
        assertThat(message.attachments()).isNotEmpty();
        assertThat(message.evidences()).isNotEmpty();
        assertThat(message.errors()).isEmpty();
    }

    // update backend identifier

    @Test
    void should_throw_null_pointer_exception_when_updating_backend_identifier_with_a_null_message_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateBackendIdentifier(null, "backend-identifier")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_backend_identifier_with_a_null_backend_identifier() {
        assertThrows(
            NullPointerException.class,
            () -> repository.updateBackendIdentifier("message-identifier", null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_updating_backend_identifier_with_a_null_message_identifier_and_backend_identifier() {
        assertThrows(
            NullPointerException.class, () -> repository.updateBackendIdentifier(null, null)
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
    void should_update_backend_identifier_successfully_in_the_database() {
        var message = this.repository.updateBackendIdentifier(
            "7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu", "backend-identifier"
        );

        assertThat(message).isNotNull();
        assertThat(message.backendMessageIdentifier()).isEqualTo("backend-identifier");
    }
}
