/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.AS4PropertiesTestFixtures;
import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import java.util.Collections;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorMessage")
public class ConnectorMessageTest {
    @Nested
    @DisplayName("check if the message has been rejected")
    class CheckIfRejected {
        @Test
        void should_return_true_if_the_message_has_been_rejected() {
            var message = BusinessMessageTestFixtures.createRejectedMessage();

            assertThat(message.isRejected()).isTrue();
        }

        @Test
        void should_return_false_if_the_message_has_not_been_rejected() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage();

            assertThat(message.isRejected()).isFalse();
        }
    }

    @Nested
    @DisplayName("check if the message is a business message")
    class CheckIfBusinessMessage {
        @Test
        void should_return_true_when_message_is_business_message() {
            var message = BusinessMessageTestFixtures.createInboundBusinessMessage();
            assertThat(message.isBusinessMessage()).isTrue();
        }

        @Test
        void should_return_false_when_message_is_not_a_business_message() {
            var message = BusinessMessageTestFixtures.createEvidenceTriggerMessage();
            assertThat(message.isBusinessMessage()).isFalse();
        }
    }

    @Nested
    @DisplayName("check if the message is an evidence message")
    class CheckIfEvidenceMessage {
        @Test
        void should_return_true_when_message_is_evidence_message() {
            var message = BusinessMessageTestFixtures.createDeliveryEvidenceMessage();
            assertThat(message.isEvidenceMessage()).isTrue();
        }

        @Test
        void should_return_false_when_message_is_not_an_evidence_message() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage();
            assertThat(message.isEvidenceMessage()).isFalse();
        }
    }

    @Nested
    @DisplayName("check if the message is an evidence trigger message")
    class CheckIfEvidenceTriggerMessage {
        @Test
        void should_return_true_when_message_is_evidence_trigger_message() {
            var message = BusinessMessageTestFixtures.createEvidenceTriggerMessage();
            assertThat(message.isEvidenceTriggerMessage()).isTrue();
        }

        @Test
        void should_return_false_when_message_is_not_an_evidence_trigger_message() {
            var message = BusinessMessageTestFixtures.createInboundBusinessMessage();
            assertThat(message.isEvidenceTriggerMessage()).isFalse();
        }

        @Test
        void should_return_false_when_evidences_is_null() {
            var message = BusinessMessageTestFixtures.createEvidenceTriggerMessage()
                                                     .toBuilder()
                                                     .evidences(null)
                                                     .transportedEvidences(null)
                                                     .build();

            assertThat(message.isEvidenceTriggerMessage()).isFalse();
        }

        @Test
        void should_return_false_when_evidences_is_empty() {
            var message = BusinessMessageTestFixtures.createEvidenceTriggerMessage()
                                                     .toBuilder()
                                                     .evidences(Collections.emptyList())
                                                     .transportedEvidences(Collections.emptyList())
                                                     .build();

            assertThat(message.isEvidenceTriggerMessage()).isFalse();
        }
    }

    @Nested
    @DisplayName("switch message direction")
    class SwitchDirection {
        @Test
        void should_fail_when_direction_is_null() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage()
                                                     .toBuilder()
                                                     .direction(null)
                                                     .build();

            assertThrows(
                IllegalStateException.class,
                message::switchDirection
            );
        }

        @Test
        void should_fail_when_from_party_is_null() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage()
                                                     .toBuilder()
                                                     .as4Properties(AS4PropertiesTestFixtures.createAS4PropertiesWithoutFromParty())
                                                     .build();

            assertThrows(
                IllegalStateException.class,
                message::switchDirection
            );
        }

        @Test
        void should_fail_when_to_party_is_null() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage()
                                                     .toBuilder()
                                                     .as4Properties(AS4PropertiesTestFixtures.createAS4PropertiesWithoutToParty())
                                                     .build();

            assertThrows(
                IllegalStateException.class,
                message::switchDirection
            );
        }

        @Test
        void should_switch_direction_from_outbound_to_inbound() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage();

            var switched = message.switchDirection();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(switched.direction())
                      .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
                softly.assertThat(switched.as4Properties().originalSender())
                      .isEqualTo(message.as4Properties().finalRecipient());
                softly.assertThat(switched.as4Properties().finalRecipient())
                      .isEqualTo(message.as4Properties().originalSender());
                softly.assertThat(switched.as4Properties().fromParty())
                      .isEqualTo(message.as4Properties()
                                        .toParty()
                                        .toBuilder()
                                        .roleType(ConnectorPartyRoleType.INITIATOR)
                                        .build());
                softly.assertThat(switched.as4Properties().toParty())
                      .isEqualTo(message.as4Properties()
                                        .fromParty()
                                        .toBuilder()
                                        .roleType(ConnectorPartyRoleType.RESPONDER)
                                        .build());
            });
        }

        @Test
        void should_not_mutate_the_original_message_when_switching() {
            var message = BusinessMessageTestFixtures.createOutboundBusinessMessage();

            message.switchDirection();

            assertThat(message.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
        }
    }
}
