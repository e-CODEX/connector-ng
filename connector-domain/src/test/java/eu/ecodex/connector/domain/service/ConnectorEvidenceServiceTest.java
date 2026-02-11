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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotRelevantException;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.property.ConnectorMessageProcessingConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorEvidenceService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
class ConnectorEvidenceServiceTest {
    @Mock
    private ConnectorEvidenceToolkit evidenceToolkit;
    @Mock
    private ConnectorMessageRepository messageRepository;
    @Mock
    private ConnectorMessageProcessingConfigProvider messageProcessingConfigProvider;
    private ConnectorEvidenceService evidenceService;

    @BeforeEach
    void setUp() {
        var messageService = new ConnectorMessageServiceImpl(
                messageRepository, messageProcessingConfigProvider
        );
        this.evidenceService = new ConnectorEvidenceServiceImpl(evidenceToolkit, messageService);
    }

    // success evidence creation (SUBMISSION_ACCEPTANCE)
    @Test
    void should_create_submission_acceptance_evidence_successfully() {
        when(evidenceToolkit.create(
                any(),
                eq(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE),
                any()
        )).thenReturn(EvidenceTestFixtures.createSubmissionAcceptanceEvidence());
        var evidence = this.evidenceService.createSuccess(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                MessageTestFixtures.createValidOutboundBusinessMessage()
        );

        assertThat(evidence).isNotNull();
        assertThat(evidence.type()).isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        assertThat(evidence.content()).isNotEmpty();
    }

    @Test
    void should_throw_null_pointer_exception_when_creating_evidence_with_null_message() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.createSuccess(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_creating_evidence_with_null_evidence_type() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.createSuccess(
                        null,
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                )
        );
    }

    // failure or rejection evidence creation

    @Test
    void should_throw_exception_when_evidence_type_is_null_during_failure__evidence_creation() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.createFailure(
                        null,
                        MessageTestFixtures.createValidOutboundBusinessMessage(),
                        ConnectorMessageRejectionReason.OTHER
                )
        );
    }

    @Test
    void should_throw_exception_when_message_is_null_during_failure__evidence_creation() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.createFailure(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        null,
                        ConnectorMessageRejectionReason.OTHER
                )
        );
    }

    // message processing
    @ParameterizedTest
    @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
    void should_process_a_message_as_confirmed_successfully_if_its_evidence_is_delivery_or_retrieval(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        when(messageRepository.setAsConfirmed(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());
        this.evidenceService.processMessage(evidenceType, message);
    }

    @ParameterizedTest
    @EnumSource(
            value = ConnectorEvidenceType.class,
            names = {"SUBMISSION_REJECTION", "NON_DELIVERY", "NON_RETRIEVAL", "RELAY_REMMD_REJECTION", "RELAY_REMMD_FAILURE"}
    )
    void should_process_a_message_as_rejected_successfully_if_its_evidence_is_not_delivery_or_retrieval(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createRejectedMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        when(messageRepository.setAsRejected(any())).thenReturn(
                MessageTestFixtures.createConfirmedMessage());
        this.evidenceService.processMessage(evidenceType, message);
    }

    @ParameterizedTest
    @EnumSource(value = ConnectorEvidenceType.class, names = {"DELIVERY", "RETRIEVAL"})
    void should_throw_exception_when_processing_message_as_delivery_or_retrieval_if_it_has_been_rejected(
            ConnectorEvidenceType evidenceType) {
        var message = MessageTestFixtures.createRejectedMessage();
        when(messageRepository.findByIdentifier(any())).thenReturn(message);
        var exception = assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> this.evidenceService.processMessage(evidenceType, message)
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
                () -> this.evidenceService.processMessage(ConnectorEvidenceType.RETRIEVAL, message)
        );
        assertThat(
                exception.getMessage()
        ).contains(
                "The processed evidence is ignored, because the business message is already in "
                + "rejected state"
        );
    }

    @Test
    void should_throw_exception_if_evidence_priority_is_lower_than_message_highest_evidence_priority() {
        var message = MessageTestFixtures.createRejectedMessage();
        assertThrows(
                ConnectorEvidenceNotRelevantException.class,
                () -> this.evidenceService.processMessage(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message)
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_transported_evidences() {
        var message = MessageTestFixtures.createRejectedMessage()
                                 .toBuilder()
                                 .transportedEvidences(null)
                                 .build();

        assertThrows(
                ConnectorEvidenceException.class, () -> this.evidenceService.processMessage(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_evidence_type() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.processMessage(
                        null,
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                )
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_processing_message_with_null_message() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.processMessage(
                        ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null)
        );
    }

    // isEvidenceTriggeringAllowed
    @Test
    void should_return_true_when_checking_is_evidence_trigger_and_message_is_evidence_trigger() {
        var businessMessage = MessageTestFixtures.createEvidenceTriggerMessage();
        this.evidenceService.isEvidenceTriggeringAllowed(businessMessage);
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(null)
        );
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_is_not_evidence() {
        var businessMessage = MessageTestFixtures.createValidOutboundBusinessMessage();
        assertThrows(
                ConnectorEvidenceException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(businessMessage)
        );
    }

    @Test
    void should_throw_exception_when_checking_is_evidence_trigger_and_message_direction_is_backend() {
        var businessMessage = MessageTestFixtures.createEvidenceTriggerMessage()
                                         .toBuilder()
                                         .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
                                         .build();
        assertThrows(
                ConnectorEvidenceException.class,
                () -> this.evidenceService.isEvidenceTriggeringAllowed(businessMessage)
        );
    }
}
