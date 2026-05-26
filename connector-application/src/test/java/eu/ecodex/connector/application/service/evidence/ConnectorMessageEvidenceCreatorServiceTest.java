/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.evidence;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.evidence.ConnectorMessageEvidenceCreatorService;
import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorMessageEvidenceCreatorServiceTest {
    @Mock
    private ConnectorEvidenceToolkit evidenceToolkit;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @InjectMocks
    private ConnectorMessageEvidenceCreatorService evidenceService;

    // success evidence creation (SUBMISSION_ACCEPTANCE)
    @Test
    void should_create_submission_acceptance_evidence_successfully() {
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();

        when(evidenceToolkit.create(
                any(),
                eq(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE),
                any()
        )).thenReturn(evidence);
        when(evidenceRepository.save(any(), any())).thenReturn(evidence);

        var created = this.evidenceService.createSuccess(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                MessageTestFixtures.createValidOutboundBusinessMessage()
        );

        assertThat(created).isNotNull();
        assertThat(created.type()).isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        assertThat(created.attachment()).isNotNull();
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
    void should_create_failure_acceptance_evidence_successfully() {
        var evidence = EvidenceTestFixtures.createRelayREMMDRejectionEvidence();

        when(evidenceToolkit.create(
                any(),
                eq(ConnectorEvidenceType.RELAY_REMMD_REJECTION),
                any()
        )).thenReturn(evidence);
        when(evidenceRepository.save(any(), any())).thenReturn(evidence);

        var created = this.evidenceService.createFailure(
                ConnectorEvidenceType.RELAY_REMMD_REJECTION,
                MessageTestFixtures.createValidOutboundBusinessMessage(),
                ConnectorMessageRejectionReason.OTHER
        );

        assertThat(created).isNotNull();
        assertThat(created.type()).isEqualTo(ConnectorEvidenceType.RELAY_REMMD_REJECTION);
        assertThat(created.attachment()).isNotNull();
    }


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
    void should_throw_exception_when_message_is_null_during_failure_evidence_creation() {
        assertThrows(
                NullPointerException.class, () -> this.evidenceService.createFailure(
                        ConnectorEvidenceType.RELAY_REMMD_REJECTION,
                        null,
                        ConnectorMessageRejectionReason.OTHER
                )
        );
    }
}
