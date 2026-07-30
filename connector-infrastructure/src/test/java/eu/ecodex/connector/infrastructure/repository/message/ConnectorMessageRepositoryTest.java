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

import eu.ecodex.connector.AS4PropertiesTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("DataFlowIssue")

@DisplayName("ConnectorMessageRepository")
public class ConnectorMessageRepositoryTest extends AbstractRepositoryTest {
    private static final String MESSAGE_ID = "fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu";

    @Autowired
    private ConnectorMessageRepository repository;
    @Autowired
    private ConnectorMessageJpaRepository jpaRepository;

    /**
     * Reference data only (no message rows). Used by the save flow.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
        "classpath:sql/service.sql",
        "classpath:sql/action.sql",
    })
    private @interface WithReferenceData {
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
        "classpath:sql/attachment.sql",
        "classpath:sql/evidence.sql",
    })
    private @interface WithMessageData {
    }

    @Nested
    @DisplayName("save a message")
    class Save {
        @Test
        void should_fail_if_the_message_is_null() {
            assertThrows(
                NullPointerException.class, () -> repository.save(null)
            );
        }

        @Test
        void should_fail_if_the_business_domain_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .businessDomainIdentifier(null)
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        void should_fail_if_the_identifier_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .identifier(null)
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        void should_fail_if_the_as4_properties_service_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .as4Properties(
                                                 AS4PropertiesTestFixtures.createAS4PropertiesWithoutService()
                                             )
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        void should_fail_if_the_as4_properties_action_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .as4Properties(
                                                 AS4PropertiesTestFixtures.createAS4PropertiesWithoutAction()
                                             )
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        void should_fail_if_the_as4_properties_from_party_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .as4Properties(
                                                 AS4PropertiesTestFixtures.createAS4PropertiesWithoutFromParty()
                                             )
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        void should_fail_if_the_as4_properties_to_party_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .as4Properties(
                                                 AS4PropertiesTestFixtures.createAS4PropertiesWithoutToParty()
                                             )
                                             .build();
            assertThrows(
                IllegalArgumentException.class, () -> repository.save(message)
            );
        }

        @Test
        @WithReferenceData
        void should_save_the_message_successfully() {
            var message = MessageTestFixtures.createOutboundStagingBusinessMessage()
                                             .toBuilder()
                                             .identifier(
                                                 "2adede33-41cf-4509-b0c1-0c83ad96eed7@connector.ecodex.eu")
                                             .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                             .build();

            var saved = repository.save(message);

            assertThat(saved).isNotNull();
        }
    }

    @Nested
    @DisplayName("update the gateway name")
    class UpdateGatewayName {
        @Test
        @WithMessageData
        void should_update_the_gateway_name_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getGatewayName()).isEqualTo("default_gateway");

            var update = repository.updateGatewayName(MESSAGE_ID, "gateway-name");

            assertThat(update).isNotNull();
            assertThat(update.gatewayName()).isEqualTo("gateway-name");
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateGatewayName(null, "gateway-name")
            );
        }

        @Test
        void should_fail_if_the_gateway_name_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateGatewayName(MESSAGE_ID, null)
            );
        }

        @Test
        void should_fail_if_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateGatewayName(null, null)
            );
        }
    }

    @Nested
    @DisplayName("update the backend name")
    class UpdateBackendName {
        @Test
        @WithMessageData
        void should_update_the_backend_name_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getGatewayName()).isEqualTo("default_gateway");

            var update = repository.updateBackendName(MESSAGE_ID, "backend-name");

            assertThat(update).isNotNull();
            assertThat(update.backendName()).isEqualTo("backend-name");
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendName(null, "backend-name")
            );
        }

        @Test
        void should_fail_if_the_backend_name_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendName(MESSAGE_ID, null)
            );
        }

        @Test
        void should_fail_if_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendName(null, null)
            );
        }
    }

    @Nested
    @DisplayName("update the ebms identifier")
    class UpdateEbmsIdentifier {
        @Test
        @WithMessageData
        void should_update_the_ebms_identifier_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getAs4Properties().getEbmsMessageIdentifier()).isNull();

            var ebmsIdentifier = String.format("%s@%s", UUID.randomUUID(), "connector.ecodex.eu");

            var update = repository.updateEbmsIdentifier(MESSAGE_ID, ebmsIdentifier);

            assertThat(update).isNotNull();
            assertThat(update.as4Properties().ebmsMessageIdentifier()).isEqualTo(ebmsIdentifier);
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateEbmsIdentifier(null, "ebms-identifier")
            );
        }

        @Test
        void should_fail_if_the_ebms_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateEbmsIdentifier(MESSAGE_ID, null)
            );
        }

        @Test
        void should_fail_if_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateEbmsIdentifier(null, null)
            );
        }
    }

    @Nested
    @DisplayName("set a message as rejected")
    class SetAsRejected {
        @Test
        @WithMessageData
        void should_set_the_message_as_rejected_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getRejectedAt()).isNull();

            var update = repository.setAsRejected(message.getIdentifier());

            assertThat(update).isNotNull();
            assertThat(update.rejectedAt()).isNotNull();
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.setAsRejected(null)
            );
        }
    }

    @Nested
    @DisplayName("set a message as confirmed")
    class SetAsConfirmed {
        @Test
        @WithMessageData
        void should_set_the_message_as_confirmed_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getConfirmedAt()).isNull();

            var update = repository.setAsConfirmed(message.getIdentifier());

            assertThat(update).isNotNull();
            assertThat(update.confirmedAt()).isNotNull();
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            // NOTE: original called setAsRejected(null) here — fixed to setAsConfirmed(null)
            assertThrows(
                NullPointerException.class,
                () -> repository.setAsConfirmed(null)
            );
        }
    }

    @Nested
    @DisplayName("set delivered to link partner at")
    class SetDeliveredToLinkPartnerAt {
        @Test
        @WithMessageData
        void should_set_the_delivered_to_link_partner_timestamp_successfully() {
            var message = jpaRepository.findByIdentifier(MESSAGE_ID);
            assertThat(message).isNotNull();
            assertThat(message.getDeliveredToLinkPartnerAt()).isNull();

            var update = repository.setDeliveredToLinkPartnerAt(message.getIdentifier());

            assertThat(update).isNotNull();
            assertThat(update.deliveredToLinkPartnerAt()).isNotNull();
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.setDeliveredToLinkPartnerAt(null)
            );
        }
    }

    @Nested
    @DisplayName("find by conversation identifier")
    class FindByConversationIdentifier {
        @Test
        @WithMessageData
        void should_find_the_matching_messages() {
            var messages = repository.findByConversationIdentifier(
                "5abe51ce-e94a-4df6-8b77-41b10bc47da7");

            assertThat(messages).isNotNull();
            assertThat(messages).hasSize(1);
        }

        @Test
        void should_return_an_empty_list_when_the_conversation_identifier_is_unknown() {
            var messages = repository.findByConversationIdentifier(
                "unknown-conversation-identifier");

            assertThat(messages).isNotNull();
            assertThat(messages).isEmpty();
        }

        @Test
        void should_fail_if_the_conversation_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByConversationIdentifier(null)
            );
        }
    }

    @Nested
    @DisplayName("find all by identifiers")
    class FindAllByIdentifier {
        @Test
        @WithMessageData
        void should_find_the_matching_messages() {
            var messages = repository.findAllByIdentifier(
                List.of("7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu")
            );

            assertThat(messages).isNotNull();
            assertThat(messages).hasSize(1);
        }

        @Test
        void should_fail_if_the_identifiers_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findAllByIdentifier(null)
            );
        }
    }

    @Nested
    @DisplayName("find by identifier")
    class FindByIdentifier {
        @Test
        @WithMessageData
        void should_find_the_message_with_its_attachments_and_evidences() {
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

        @Test
        void should_return_null_when_the_identifier_is_unknown() {
            var message = repository.findByIdentifier("unknown-identifier");
            assertThat(message).isNull();
        }

        @Test
        void should_fail_if_the_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.findByIdentifier(null)
            );
        }
    }

    @Nested
    @DisplayName("update the backend identifier")
    class UpdateBackendIdentifier {
        @Test
        @WithMessageData
        void should_update_the_backend_identifier_successfully() {
            var message = repository.updateBackendIdentifier(
                "7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu", "backend-identifier"
            );

            assertThat(message).isNotNull();
            assertThat(message.backendMessageIdentifier()).isEqualTo("backend-identifier");
        }

        @Test
        void should_fail_if_the_message_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendIdentifier(null, "backend-identifier")
            );
        }

        @Test
        void should_fail_if_the_backend_identifier_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendIdentifier("message-identifier", null)
            );
        }

        @Test
        void should_fail_if_both_arguments_are_null() {
            assertThrows(
                NullPointerException.class,
                () -> repository.updateBackendIdentifier(null, null)
            );
        }
    }
}
