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
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageEvidenceVerifierService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageEvidenceVerifierServiceTest {
    @Mock
    private ConnectorMessageRepository messageRepository;

    @InjectMocks
    private ConnectorMessageEvidenceVerifierService messageEvidenceVerifierService;

    @ParameterizedTest
    @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
    void should_process_a_message_as_confirmed_successfully_if_its_evidence_is_delivery_or_retrieval(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage();

        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        when(messageRepository.setAsConfirmed(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());

        this.messageEvidenceVerifierService.verify(evidenceType, message);
    }

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
    void should_process_a_message_as_rejected_successfully_if_its_evidence_is_not_delivery_or_retrieval(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createRejectedMessage();

        when(messageRepository.setAsRejected(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());

        this.messageEvidenceVerifierService.verify(evidenceType, message);
    }

    @ParameterizedTest
    @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
    void should_throw_exception_when_processing_message_as_delivery_or_retrieval_if_it_has_been_rejected(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createRejectedMessage();

        when(messageRepository.findByIdentifier(any())).thenReturn(message);

        var exception = assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> this.messageEvidenceVerifierService.verify(evidenceType, message)
        );

        assertThat(exception.getMessage()).contains(
                "The processed evidence is ignored, because the business message is already in rejected state");
    }

    @Test
    void should_throw_exception_when_processing_message_as_retrieval_if_it_has_been_rejected() {
        var message = MessageTestFixtures.createRejectedMessage();

        when(messageRepository.findByIdentifier(any())).thenReturn(message);

        var exception = assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> this.messageEvidenceVerifierService.verify(
                        ConnectorEvidenceType.RETRIEVAL,
                        message
                )
        );

        assertThat(exception.getMessage())
                .contains(
                        "The processed evidence is ignored, because the business message is "
                                + "already in rejected state"
                );
    }

    @Test
    void should_throw_exception_if_evidence_priority_is_lower_than_message_highest_evidence_priority() {
        var message = MessageTestFixtures.createRejectedMessage();

        assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> this.messageEvidenceVerifierService.verify(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_transported_evidences() {
        var message = MessageTestFixtures.createRejectedMessage()
                                         .toBuilder()
                                         .transportedEvidences(null)
                                         .build();

        assertThrows(
                ConnectorEvidenceException.class,
                () -> this.messageEvidenceVerifierService.verify(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_evidence_type() {
        assertThrows(
                NullPointerException.class,
                () -> this.messageEvidenceVerifierService.verify(
                        null,
                        MessageTestFixtures.createOutboundBusinessMessage()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_message() {
        assertThrows(
                NullPointerException.class,
                () -> this.messageEvidenceVerifierService.verify(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        null
                )
        );
    }
}
