/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.service.message.ConnectorMessageIdGeneratorService;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundEvidenceMessageReceiverService")
public class ConnectorOutboundEvidenceMessageReceiverServiceTest {
    private static final String MESSAGE_ID =
        "28c86f29-5953-42d5-8336-1a03f7e86951@eu.ecodex.connector";

    @Mock
    private ConnectorMessageEventPublisher<ConnectorTriggeredEvidenceMessage> evidenceTriggerPublisher;
    @Mock
    private ConnectorMessageIdGeneratorService messageIdGenerator;
    @Mock
    private ConnectorTriggeredEvidenceMessageVerifier verifyTriggeredEvidenceService;

    private ConnectorOutboundEvidenceMessageReceiverService evidenceMessageReceiverService;

    @BeforeEach
    void setUp() {
        evidenceMessageReceiverService = new ConnectorOutboundEvidenceMessageReceiverService(
            messageIdGenerator,
            verifyTriggeredEvidenceService,
            evidenceTriggerPublisher
        );
    }

    private ConnectorOutboundEvidenceMessageCommand createEvidenceMessageCommand() {
        return ConnectorOutboundEvidenceMessageCommand
            .builder()
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .backendName("backend_alice")
            .backendMessageIdentifier("85964ab5-b04b-4d45-97d1-962b565e22df@connector.ecodex.eu")
            .referenceToIdentifier(null)
            .build();
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> evidenceMessageReceiverService.execute(null)
            );

            verifyNoInteractions(evidenceTriggerPublisher, verifyTriggeredEvidenceService);
        }
    }

    @Nested
    @DisplayName("when receiving an evidence trigger message")
    class WhenReceivingAnEvidenceTriggerMessage {
        @ParameterizedTest
        @ValueSource(classes = {
            ConnectorEvidenceException.class,
            ConnectorMessageNotFoundException.class
        })
        void should_fail_when_the_triggered_evidence_verification_fails(
            Class<? extends Exception> exceptionClass) {
            doThrow(exceptionClass).when(verifyTriggeredEvidenceService)
                                   .verify(any());
            when(messageIdGenerator.generateIdentifier()).thenReturn(MESSAGE_ID);

            var evidenceMessageCommand = createEvidenceMessageCommand();

            assertThrows(
                exceptionClass,
                () -> evidenceMessageReceiverService.execute(evidenceMessageCommand)
            );

            verifyNoInteractions(evidenceTriggerPublisher);
        }

        @Test
        void should_submit_the_message_to_the_evidence_queue() {
            doNothing().when(verifyTriggeredEvidenceService).verify(any());
            when(messageIdGenerator.generateIdentifier()).thenReturn(MESSAGE_ID);

            var evidenceMessageCommand = createEvidenceMessageCommand();

            var message = evidenceMessageReceiverService.execute(evidenceMessageCommand);

            assertThat(message.identifier()).isNotNull();
            assertThat(message.identifier()).isEqualTo(MESSAGE_ID);
        }
    }
}

