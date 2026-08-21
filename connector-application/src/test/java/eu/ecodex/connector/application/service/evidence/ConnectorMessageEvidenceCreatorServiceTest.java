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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.port.spi.ConnectorEvidenceToolkit;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorMessageEvidenceCreatorService")
public class ConnectorMessageEvidenceCreatorServiceTest {
    @Mock
    private ConnectorEvidenceToolkit evidenceToolkit;
    @Mock
    private ConnectorMessageEvidenceRepository evidenceRepository;
    @InjectMocks
    private ConnectorMessageEvidenceCreatorService evidenceService;

    @Nested
    @DisplayName("create a success evidence")
    class CreateSuccess {
        @Test
        void should_create_the_submission_acceptance_evidence_successfully() {
            var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();
            when(evidenceToolkit.create(
                any(),
                eq(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE),
                any()
            )).thenReturn(evidence);
            when(evidenceRepository.save(any(), any())).thenReturn(evidence);

            var created = evidenceService.createSuccess(
                ConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                BusinessMessageTestFixtures.createOutboundMessage()
            );

            assertThat(created).isNotNull();
            assertThat(created.type()).isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
            assertThat(created.content()).isNotNull();
        }

        @Test
        void should_fail_when_the_evidence_type_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> evidenceService.createSuccess(
                    null,
                    BusinessMessageTestFixtures.createOutboundMessage()
                )
            );
        }

        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> evidenceService.createSuccess(
                    ConnectorEvidenceType.SUBMISSION_ACCEPTANCE, null)
            );
        }
    }

    @Nested
    @DisplayName("create a failure evidence")
    class CreateFailure {
        @Test
        void should_create_the_rejection_evidence_successfully() {
            var evidence = EvidenceTestFixtures.createRelayREMMDRejectionEvidence();
            when(evidenceToolkit.create(
                any(),
                eq(ConnectorEvidenceType.RELAY_REMMD_REJECTION),
                any()
            )).thenReturn(evidence);
            when(evidenceRepository.save(any(), any())).thenReturn(evidence);

            var created = evidenceService.createFailure(
                ConnectorEvidenceType.RELAY_REMMD_REJECTION,
                BusinessMessageTestFixtures.createOutboundMessage(),
                ConnectorMessageRejectionReason.OTHER
            );

            assertThat(created).isNotNull();
            assertThat(created.type()).isEqualTo(ConnectorEvidenceType.RELAY_REMMD_REJECTION);
            assertThat(created.content()).isNotNull();
        }

        @Test
        void should_fail_when_the_evidence_type_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> evidenceService.createFailure(
                    null,
                    BusinessMessageTestFixtures.createOutboundMessage(),
                    ConnectorMessageRejectionReason.OTHER
                )
            );
        }

        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> evidenceService.createFailure(
                    ConnectorEvidenceType.RELAY_REMMD_REJECTION,
                    null,
                    ConnectorMessageRejectionReason.OTHER
                )
            );
        }
    }
}

