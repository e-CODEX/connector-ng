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

import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorEvidenceMessage")
public class ConnectorEvidenceMessageTest {
    @Nested
    @DisplayName("check if the message is an evidence trigger message")
    class CheckIfEvidenceTriggerMessage {
        @Test
        void should_return_true_when_message_is_evidence_trigger_message() {
            var message = EvidenceMessageTestFixtures.createEvidenceTriggerMessage();
            assertThat(message.isEvidenceTriggerMessage()).isTrue();
        }

        @Test
        void should_return_false_when_message_is_not_an_evidence_trigger_message() {
            var message = EvidenceMessageTestFixtures.createConfirmedMessage();
            assertThat(message.isEvidenceTriggerMessage()).isFalse();
        }
    }

    @Nested
    @DisplayName("switch message direction")
    class SwitchDirection {
        @Test
        void should_switch_direction_from_outbound_to_inbound() {
            var message = EvidenceMessageTestFixtures.createConfirmedMessage();

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
            var message = EvidenceMessageTestFixtures.createConfirmedMessage();

            message.switchDirection();

            assertThat(message.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
        }
    }
}
