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


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import eu.ecodex.connector.MessageTestFixtures;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageTest {
    // check if the message is a business message

    @Test
    void should_return_true_if_message_is_business_message() {
        var message = MessageTestFixtures.createValidInboundBusinessMessage();
        assertThat(message.isBusinessMessage()).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_a_business_message() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage();
        assertThat(message.isBusinessMessage()).isFalse();
    }

    // check if the message is an evidence message

    @Test
    void should_return_true_if_message_is_evidence_message() {
        var message = MessageTestFixtures.createDeliveryEvidenceMessage();
        assertThat(message.isEvidenceMessage()).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_message() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        assertThat(message.isEvidenceMessage()).isFalse();
    }

    // check if the message is an evidence trigger message

    @Test
    void should_return_true_if_message_is_evidence_trigger_message() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage();
        assertThat(message.isEvidenceTriggerMessage()).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message() {
        var message = MessageTestFixtures.createValidInboundBusinessMessage();
        assertThat(message.isEvidenceTriggerMessage()).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_null() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .evidences(null)
                                         .transportedEvidences(null)
                                         .build();

        assertThat(message.isEvidenceTriggerMessage()).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_empty() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .evidences(Collections.emptyList())
                                         .transportedEvidences(Collections.emptyList())
                                         .build();

        assertThat(message.isEvidenceTriggerMessage()).isFalse();
    }
}
