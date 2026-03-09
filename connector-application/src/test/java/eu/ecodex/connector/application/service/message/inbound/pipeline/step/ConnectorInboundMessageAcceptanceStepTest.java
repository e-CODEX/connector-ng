/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound.pipeline.step;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.inbound.pipeline.step.ConnectorInboundMessageAcceptanceStep;
import eu.ecodex.connector.application.service.usecase.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageAcceptanceStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageAcceptanceStepTest {
    @Mock
    private ConnectorEvidenceMessageCreator evidenceMessageCreatorService;
    @Mock
    private ConnectorMessageEvidenceCreator evidenceCreatorService;
    @Mock
    private ConnectorMessageEvidenceVerifier evidenceVerifierService;

    private ConnectorMessageStep acceptanceCreationStep;

    @BeforeEach
    void setUp() {
        acceptanceCreationStep = new ConnectorInboundMessageAcceptanceStep(
                evidenceMessageCreatorService, evidenceCreatorService, evidenceVerifierService
        );
    }

    @Test
    void should_create_inbound_message_execute_relay_remmd_acceptance_evidence() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessage();
        var evidence = EvidenceTestFixtures.createRelayREMMDAcceptanceEvidence();

        when(evidenceCreatorService.createSuccess(any(), any())).thenReturn(evidence);
        when(evidenceMessageCreatorService.create(any(), any()))
                .thenReturn(MessageTestFixtures.createRelayRMMDAcceptanceEvidenceMessage());
        doNothing().when(evidenceVerifierService).verify(any(), any());

        var outputMessage = acceptanceCreationStep.execute(inboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage.isEvidenceMessage()).isTrue();
        assertThat(outputMessage.evidences()).isNotEmpty();
        assertThat(outputMessage.evidences()).hasSize(1);
        assertThat(outputMessage.evidences().getFirst().type())
                .isEqualTo(ConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE);
        assertThat(outputMessage.direction()).isNotEqualTo(inboundMessage.direction());
        assertThat(outputMessage.direction())
                .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> acceptanceCreationStep.execute(null)
        );
    }
}
