/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;


import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorTriggeredEvidenceMessageVerifierService")
public class ConnectorTriggeredEvidenceMessageVerifierServiceTest {
    private static final String REF_ID = "ref-message-001";
    private static final String BACKEND_ID =
        "b27c88a3-31f8-4a09-baec-80361f813aeb@connector.ecodex.eu";
    private static final ConnectorMessageDirection TRIGGER_DIRECTION =
        ConnectorMessageDirection.BACKEND_TO_GATEWAY;

    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorTriggeredEvidenceMessageVerifierService service;

    private ConnectorBusinessMessage inboundBusinessMessage() {
        return BusinessMessageTestFixtures.createInboundMessage();
    }

    private ConnectorTriggeredEvidenceMessage triggeredEvidenceMessage(
        String referenceToIdentifier,
        String backendMessageIdentifier) {
        return ConnectorTriggeredEvidenceMessage
            .builder()
            .identifier("2df7c881-5548-4edd-8bcd-868af750b859@connector.ecodex.eu")
            .backendMessageIdentifier(backendMessageIdentifier)
            .referenceToBackendMessageIdentifier(backendMessageIdentifier)
            .backendName("backend_alice")
            .direction(TRIGGER_DIRECTION)
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .referenceToIdentifier(referenceToIdentifier)
            .build();
    }

    @Nested
    @DisplayName("when the reference is invalid")
    class WhenReferenceIsInvalid {
        @Test
        void should_fail_when_both_reference_ids_are_blank() {
            assertThatThrownBy(() -> service.verify(triggeredEvidenceMessage("", "")))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("refToMessageId");
        }
    }

    @Nested
    @DisplayName("when no referenced message is found")
    class WhenNoMessageIsFound {
        @Test
        void should_fail_when_no_message_matches() {
            when(messageRepository.findReferencedBusinessMessage(REF_ID, TRIGGER_DIRECTION))
                .thenReturn(null);

            assertThatThrownBy(() -> service.verify(triggeredEvidenceMessage(REF_ID, "")))
                .isInstanceOf(ConnectorMessageNotFoundException.class)
                .hasMessageContaining(REF_ID);
        }
    }

    @Nested
    @DisplayName("when the referenced message is found")
    class WhenMessageIsFound {
        @Test
        void should_succeed_when_the_direction_is_gateway_to_backend() {
            when(messageRepository.findReferencedBusinessMessage(BACKEND_ID, TRIGGER_DIRECTION))
                .thenReturn(inboundBusinessMessage());

            assertThatNoException()
                .isThrownBy(() -> service.verify(triggeredEvidenceMessage("", BACKEND_ID)));
        }

        @Test
        void should_fail_when_the_direction_is_not_gateway_to_backend() {
            when(messageRepository.findReferencedBusinessMessage(BACKEND_ID, TRIGGER_DIRECTION))
                .thenReturn(inboundBusinessMessage().toBuilder()
                                                    .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                                                    .build());

            assertThatThrownBy(() -> service.verify(triggeredEvidenceMessage("", BACKEND_ID)))
                .isInstanceOf(ConnectorEvidenceException.class)
                .hasMessageContaining("GATEWAY_TO_BACKEND")
                .hasMessageContaining("BACKEND_TO_GATEWAY");
        }
    }
}