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
import eu.ecodex.connector.application.port.api.evidence.ConnectorMessageEvidenceCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageEvidenceVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessageNonDeliveryStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessageNonDeliveryStepTest {
    @Mock
    private ConnectorEvidenceMessageCreator evidenceMessageCreatorService;
    @Mock
    private ConnectorMessageEvidenceCreator evidenceCreatorService;
    @Mock
    private ConnectorMessageEvidenceVerifier evidenceVerifierService;

    @InjectMocks
    private ConnectorInboundMessageNonDeliveryStep nonDeliveryCreationStep;

    @Test
    void should_create_inbound_message_non_delivery_evidence_successfully() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessage();
        var evidence = EvidenceTestFixtures.createNonDeliveryEvidence();

        when(evidenceCreatorService.createFailure(any(), any(), any())).thenReturn(evidence);
        when(evidenceMessageCreatorService.create(any(), any()))
            .thenReturn(MessageTestFixtures.createNonDeliveryEvidenceMessage());
        doNothing().when(evidenceVerifierService).verify(any(), any());

        var outputMessage = nonDeliveryCreationStep.execute(inboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage.isEvidenceMessage()).isTrue();
        assertThat(outputMessage.evidences()).isNotEmpty();
        assertThat(outputMessage.evidences()).hasSize(1);
        assertThat(outputMessage.evidences().getFirst().type())
            .isEqualTo(ConnectorEvidenceType.NON_DELIVERY);
        assertThat(outputMessage.direction()).isNotEqualTo(inboundMessage.direction());
        assertThat(outputMessage.direction())
            .isEqualTo(ConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> nonDeliveryCreationStep.execute(null)
        );
    }
}
