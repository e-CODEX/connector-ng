/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorMessageService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;

    private ConnectorMessageService connectorMessageService;

    @BeforeEach
    void setUp() {
        this.connectorMessageService = new ConnectorMessageServiceImpl(
                messageRepository
        );
    }

    // find by uuid and direction
    @Test
    void should_find_message_by_identifier_and_direction_successfully() {
        when(messageRepository.findByIdentifierAndDirection(any(), any()))
                .thenReturn(MessageTestFixtures.createValidOutboundBusinessMessage());

        var message = connectorMessageService.findByIdentifierAndDirection(
                MessageTestFixtures.createValidOutboundBusinessMessage(),
                ConnectorMessageDirection.BACKEND_TO_GATEWAY
        );

        assertThat(message).isNotNull();
        assertThat(message.identifier()).isEqualTo(
                "223caef9-cae9-4387-a38c-ad4879f94b4e@connector.ecodex.eu");
        assertThat(message.direction()).isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_throw_exception_when_message_does_not_exist_by_its_identifier_and_direction() {
        when(messageRepository.findByIdentifierAndDirection(any(), any()))
                .thenReturn(null);

        assertThrows(
                ConnectorMessageNotFoundException.class,
                () -> this.connectorMessageService.findByIdentifierAndDirection(
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ConnectorMessageDirection.BACKEND_TO_GATEWAY
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_identifier_is_null_when_searching_by_identifier_and_direction() {
        assertThrows(
                NullPointerException.class,
                () -> connectorMessageService.findByIdentifierAndDirection(
                        null, ConnectorMessageDirection.BACKEND_TO_GATEWAY
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_message_direction_is_null_when_searching_by_identifier_and_direction() {
        assertThrows(
                NullPointerException.class,
                () -> connectorMessageService.findByIdentifierAndDirection(
                        null, null
                )
        );
    }

    // check if the message is an evidence trigger message
    @Test
    void should_return_true_if_message_is_evidence_trigger_message() {
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(
                MessageTestFixtures.createEvidenceTriggerMessage()
        );

        assertThat(isEvidenceTrigger).isTrue();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message() {
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(
                MessageTestFixtures.createValidInboundBusinessMessage()
        );

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_null() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                .toBuilder()
                .evidences(null)
                .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_is_empty() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                 .toBuilder()
                                 .evidences(Collections.emptyList())
                                 .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_return_false_if_message_is_not_an_evidence_trigger_message_when_evidences_content_is_empty() {
        var message = MessageTestFixtures.createEvidenceTriggerMessage()
                                 .toBuilder()
                                 .evidences(
                                         Collections.singletonList(
                                                 EvidenceTestFixtures.createEvidenceTrigger()
                                                                     .toBuilder()
                                                                     .content(new byte[1])
                                                                     .build()
                                         )
                                 )
                                 .build();
        var isEvidenceTrigger = this.connectorMessageService.isEvidenceTriggerMessage(message);

        assertThat(isEvidenceTrigger).isFalse();
    }

    @Test
    void should_throw_exception_if_message_is_null_when_checking_if_message_is_an_evidence_trigger_message() {
        assertThrows(
                NullPointerException.class,
                () -> this.connectorMessageService.isEvidenceTriggerMessage(null)
        );
    }
}
