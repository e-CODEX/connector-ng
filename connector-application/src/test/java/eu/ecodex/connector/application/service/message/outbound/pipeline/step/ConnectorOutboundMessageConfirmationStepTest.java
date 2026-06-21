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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.EvidenceTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step.ConnectorOutboundMessageConfirmationStep;
import eu.ecodex.connector.application.service.usecase.message.ConnectorEvidenceMessageCreator;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessageConfirmationStep}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessageConfirmationStepTest {
    @Mock
    ConnectorEvidenceMessageCreator messageCreator;

    @InjectMocks
    private ConnectorOutboundMessageConfirmationStep outboundMessageConfirmationCreationStep;

    @Test
    void should_execute_outbound_message_confirmation_successfully() {
        var outboundMessage = MessageTestFixtures
                .createOutboundBusinessMessage()
                .toBuilder()
                .evidences(Collections.singletonList(
                        EvidenceTestFixtures.createSubmissionAcceptanceEvidence()))
                .transportedEvidences(Collections.singletonList(
                        EvidenceTestFixtures.createSubmissionAcceptanceEvidence()))
                .build();

        when(messageCreator.create(any(), any()))
                .thenReturn(MessageTestFixtures.createSubmissionAcceptanceEvidenceMessage());

        var outputMessage = outboundMessageConfirmationCreationStep.execute(outboundMessage);

        assertThat(outputMessage).isNotNull();
        assertThat(outputMessage.isEvidenceMessage()).isTrue();
        assertThat(outputMessage.evidences()).isNotEmpty();
        assertThat(outputMessage.evidences()).hasSize(1);
        assertNotNull(outputMessage.evidences());
        assertThat(outputMessage.evidences().getFirst().type())
                .isEqualTo(ConnectorEvidenceType.SUBMISSION_ACCEPTANCE);
        assertThat(outputMessage.direction()).isNotEqualTo(outboundMessage.direction());
        assertThat(outputMessage.direction())
                .isEqualTo(ConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }

    @Test
    void should_throw_exception_executing_outbound_message_confirmation_when_transported_evidences_is_null() {
        var outboundMessage = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .transportedEvidences(null)
                                         .build();

        assertThrows(
                IllegalStateException.class,
                () -> outboundMessageConfirmationCreationStep.execute(outboundMessage)
        );
    }

    @Test
    void should_throw_exception_executing_outbound_message_confirmation_when_transported_evidences_is_empty() {
        var outboundMessage = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .evidences(Collections.emptyList())
                                         .transportedEvidences(Collections.emptyList())
                                         .build();
        assertThrows(
                IllegalStateException.class,
                () -> outboundMessageConfirmationCreationStep.execute(outboundMessage)
        );
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> outboundMessageConfirmationCreationStep.execute(null)
        );
    }
}
