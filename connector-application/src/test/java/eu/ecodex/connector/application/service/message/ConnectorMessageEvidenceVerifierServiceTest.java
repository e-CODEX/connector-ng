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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorEvidenceException;
import eu.ecodex.connector.application.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)

@DisplayName("ConnectorMessageEvidenceVerifierService")
public class ConnectorMessageEvidenceVerifierServiceTest {
    private static final String ALREADY_REJECTED_MESSAGE =
        "The processed evidence is ignored, because the business message is "
            + "already in rejected state";

    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorMessageEvidenceVerifierService messageEvidenceVerifierService;

    @Nested
    @DisplayName("when the evidence is confirmation")
    class WhenConfirming {
        @ParameterizedTest
        @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
        void should_process_the_message_as_confirmed(ConnectorEvidenceType evidenceType) {
            var message = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage();
            when(messageRepository.findByIdentifier(any())).thenReturn(message);
            when(messageRepository.setAsConfirmed(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());

            messageEvidenceVerifierService.verify(evidenceType, message);
        }
    }

    @Nested
    @DisplayName("when the evidence is rejection")
    class WhenRejecting {
        @ParameterizedTest
        @EnumSource(
            value = ConnectorEvidenceType.class,
            names = {
                "SUBMISSION_REJECTION",
                "NON_DELIVERY",
                "NON_RETRIEVAL",
                "RELAY_REMMD_REJECTION",
                "RELAY_REMMD_FAILURE"
            }
        )
        void should_process_the_message_as_rejected(ConnectorEvidenceType evidenceType) {
            var message = MessageTestFixtures.createRejectedMessage();
            when(messageRepository.setAsRejected(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());

            messageEvidenceVerifierService.verify(evidenceType, message);
        }
    }

    @Nested
    @DisplayName("when the evidence is not relevant")
    class WhenNotRelevant {
        @ParameterizedTest
        @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
        void should_fail_when_the_message_is_already_rejected(ConnectorEvidenceType evidenceType) {
            var message = MessageTestFixtures.createRejectedMessage();
            when(messageRepository.findByIdentifier(any())).thenReturn(message);

            var exception = assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> messageEvidenceVerifierService.verify(evidenceType, message)
            );

            assertThat(exception.getMessage()).contains(ALREADY_REJECTED_MESSAGE);
        }

        @Test
        void should_fail_when_the_evidence_priority_is_lower_than_the_current_highest() {
            var message = MessageTestFixtures.createRejectedMessage();

            assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> messageEvidenceVerifierService.verify(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    message
                )
            );
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_transported_evidences_are_null() {
            var message = MessageTestFixtures.createRejectedMessage()
                                             .toBuilder()
                                             .transportedEvidences(null)
                                             .build();

            assertThrows(
                ConnectorEvidenceException.class,
                () -> messageEvidenceVerifierService.verify(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    message
                )
            );
        }

        @Test
        void should_fail_when_the_evidence_type_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> messageEvidenceVerifierService.verify(
                    null,
                    MessageTestFixtures.createOutboundBusinessMessage()
                )
            );
        }

        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> messageEvidenceVerifierService.verify(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    null
                )
            );
        }

        @Test
        void should_fail_when_the_message_identifier_is_null() {
            var message = MessageTestFixtures.createOutboundBusinessMessage()
                                             .toBuilder()
                                             .identifier(null)
                                             .build();

            assertThrows(
                IllegalStateException.class,
                () -> messageEvidenceVerifierService.verify(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message)
            );
        }
    }
}

