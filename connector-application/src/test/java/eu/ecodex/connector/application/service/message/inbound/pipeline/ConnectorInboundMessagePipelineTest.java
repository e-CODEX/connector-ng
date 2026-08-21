/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound.pipeline;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessagePipeline}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorInboundMessagePipeline")
public class ConnectorInboundMessagePipelineTest {
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> backendNameStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> acceptanceStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> securityStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> nonDeliveryStep;
    @Mock
    private ConnectorMessageStep<ConnectorMessage, ConnectorMessage> linkSubmissionStep;

    private ConnectorMessagePipeline inboundMessagePipeline;

    @BeforeEach
    void setUp() {
        inboundMessagePipeline = new ConnectorInboundMessagePipeline(
            backendNameStep,
            acceptanceStep,
            securityStep,
            nonDeliveryStep,
            linkSubmissionStep
        );
    }

    @Nested
    @DisplayName("when processing a message")
    class WhenProcessing {
        @Test
        void should_run_all_steps_and_submit_the_message() {
            var inboundMessage = BusinessMessageTestFixtures.createInboundMessage();

            when(backendNameStep.execute(any())).thenReturn(inboundMessage);
            when(acceptanceStep.execute(any()))
                .thenReturn(EvidenceMessageTestFixtures.createRelayRMMDAcceptanceEvidenceMessage());
            when(securityStep.execute(any())).thenReturn(inboundMessage);
            when(linkSubmissionStep.execute(any())).thenReturn(inboundMessage);

            inboundMessagePipeline.process(inboundMessage);

            verify(backendNameStep).execute(inboundMessage);
            verify(acceptanceStep).execute(inboundMessage);
            verify(securityStep).execute(inboundMessage);
            verify(nonDeliveryStep, never()).execute(any());
            verify(linkSubmissionStep, times(2)).execute(any());
        }

        @Test
        void should_send_a_non_delivery_evidence_when_a_security_error_occurs() {
            var inboundMessage = BusinessMessageTestFixtures.createInboundMessage();

            when(backendNameStep.execute(any())).thenReturn(inboundMessage);
            when(acceptanceStep.execute(any()))
                .thenReturn(EvidenceMessageTestFixtures.createRelayREMMDRejectionEvidenceEvidenceMessage());
            doThrow(RuntimeException.class).when(securityStep).execute(any());
            when(linkSubmissionStep.execute(any())).thenReturn(inboundMessage);

            inboundMessagePipeline.process(inboundMessage);

            verify(backendNameStep).execute(inboundMessage);
            verify(acceptanceStep).execute(inboundMessage);
            verify(securityStep).execute(inboundMessage);
            verify(nonDeliveryStep).execute(any());
            verify(linkSubmissionStep, times(2)).execute(any());
        }
    }

    @Nested
    @DisplayName("when the input is invalid")
    class WhenInputIsInvalid {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> inboundMessagePipeline.process(null)
            );
        }
    }
}
