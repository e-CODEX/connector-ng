/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageSubmissionAcceptanceStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundMessageAcceptanceStep")
public class ConnectorOutboundMessageAcceptanceStepTest {
    @Mock
    private ConnectorMessageEvidenceCreator evidenceCreator;
    @Mock
    private ConnectorMessageEvidenceVerifier evidenceVerifier;

    @InjectMocks
    private ConnectorOutboundMessageAcceptanceStep acceptanceStep;

    @Test
    void should_create_the_submission_acceptance_evidence() {
        var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage();
        var evidence = EvidenceTestFixtures.createSubmissionAcceptanceEvidence();

        when(evidenceCreator.createSuccess(any(), any())).thenReturn(evidence);
        doNothing().when(evidenceVerifier).verify(any(), any());

        var outputMessage = acceptanceStep.execute(outboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage.evidences()).hasSize(1);
        assertThat(outputMessage.evidences().getFirst().type())
            .isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        assertThat(outputMessage.direction()).isEqualTo(outboundMessage.direction());
        assertThat(outputMessage.direction())
            .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_fail_when_the_message_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> acceptanceStep.execute(null)
        );
    }
}
