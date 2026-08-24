/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.EvidenceMessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorGatewaySubmissionException;
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
 * Unit tests for the {@code ConnectorOutboundMessagePipeline}.
 */
@SuppressWarnings("DataFlowIssue")

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorOutboundMessagePipeline")
public class ConnectorOutboundMessagePipelineTest {
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> validationStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> securityStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> gatewayNameStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> ebmsIdStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> acceptanceStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> confirmationStep;
    @Mock
    private ConnectorMessageStep<ConnectorBusinessMessage, ConnectorEvidenceMessage> rejectionStep;
    @Mock
    private ConnectorMessageStep<ConnectorMessage, ConnectorMessage> linkSubmissionStep;

    private ConnectorMessagePipeline outboundMessagePipeline;

    @BeforeEach
    void setUp() {
        outboundMessagePipeline = new ConnectorOutboundMessagePipeline(
            validationStep,
            securityStep,
            gatewayNameStep,
            ebmsIdStep,
            acceptanceStep,
            confirmationStep,
            rejectionStep,
            linkSubmissionStep
        );
    }

    @Nested
    @DisplayName("when processing succeeds")
    class WhenProcessingSucceeds {
        @Test
        void should_run_all_steps_and_submit_the_message() {
            var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage();
            var confirmationMessage = EvidenceMessageTestFixtures.createConfirmedMessage();

            when(validationStep.execute(any())).thenReturn(outboundMessage);
            when(securityStep.execute(any())).thenReturn(outboundMessage);
            when(gatewayNameStep.execute(any())).thenReturn(outboundMessage);
            when(ebmsIdStep.execute(any())).thenReturn(outboundMessage);
            when(acceptanceStep.execute(any())).thenReturn(outboundMessage);
            when(confirmationStep.execute(any())).thenReturn(confirmationMessage);
            when(linkSubmissionStep.execute(any())).thenReturn(outboundMessage);

            outboundMessagePipeline.process(outboundMessage);

            verify(validationStep).execute(outboundMessage);
            verify(securityStep).execute(outboundMessage);
            verify(gatewayNameStep).execute(outboundMessage);
            verify(acceptanceStep).execute(outboundMessage);
            verify(confirmationStep).execute(outboundMessage);
            verify(linkSubmissionStep).execute(confirmationMessage);
            verify(linkSubmissionStep).execute(outboundMessage);
        }
    }

    @Nested
    @DisplayName("when processing fails")
    class WhenProcessingFails {
        @Test
        void should_send_a_rejection_evidence_and_fail_when_a_security_error_occurs() {
            var outboundMessage = BusinessMessageTestFixtures.createOutboundMessage();
            var rejectionMessage = EvidenceMessageTestFixtures.createRejectedMessage();

            when(validationStep.execute(any())).thenReturn(outboundMessage);
            doThrow(RuntimeException.class).when(securityStep).execute(any());
            when(rejectionStep.execute(any())).thenReturn(rejectionMessage);
            when(linkSubmissionStep.execute(any())).thenReturn(outboundMessage);

            assertThrows(
                ConnectorGatewaySubmissionException.class,
                () -> outboundMessagePipeline.process(outboundMessage)
            );

            verify(validationStep).execute(outboundMessage);
            verify(securityStep).execute(outboundMessage);
            verify(gatewayNameStep, never()).execute(outboundMessage);
            verify(acceptanceStep, never()).execute(outboundMessage);
            verify(confirmationStep, never()).execute(outboundMessage);
            verify(rejectionStep).execute(outboundMessage);
            verify(linkSubmissionStep).execute(rejectionMessage);
        }

        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> outboundMessagePipeline.process(null)
            );
        }
    }
}
