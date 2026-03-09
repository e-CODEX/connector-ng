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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.outbound.pipeline.ConnectorOutboundMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.exception.ConnectorGatewaySubmissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessagePipeline}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessagePipelineTest {
    private ConnectorMessagePipeline outboundMessagePipeline;
    @Mock
    private ConnectorMessageStep validationStep;
    @Mock
    private ConnectorMessageStep securityStep;
    @Mock
    private ConnectorMessageStep gatewayNameStep;
    @Mock
    private ConnectorMessageStep ebmsIdStep;
    @Mock
    private ConnectorMessageStep acceptanceStep;
    @Mock
    private ConnectorMessageStep confirmationStep;
    @Mock
    private ConnectorMessageStep rejectionStep;
    @Mock
    private ConnectorMessageStep linkSubmissionStep;

    @BeforeEach
    void setUp() {
        outboundMessagePipeline = new ConnectorOutboundMessagePipeline(
                validationStep, securityStep, gatewayNameStep, ebmsIdStep, acceptanceStep,
                confirmationStep, rejectionStep, linkSubmissionStep
        );
    }

    @Test
    void should_process_outbound_message_pipeline_successfully() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(validationStep.execute(any())).thenReturn(outboundMessage);
        when(securityStep.execute(any())).thenReturn(outboundMessage);
        when(gatewayNameStep.execute(any())).thenReturn(
                outboundMessage);
        when(ebmsIdStep.execute(any())).thenReturn(outboundMessage);
        when(acceptanceStep.execute(any())).thenReturn(
                outboundMessage);
        when(confirmationStep.execute(any())).thenReturn(outboundMessage);
        when(linkSubmissionStep.execute(any())).thenReturn(outboundMessage);

        outboundMessagePipeline.process(outboundMessage);

        verify(validationStep, times(1)).execute(outboundMessage);
        verify(securityStep, times(1)).execute(outboundMessage);
        verify(gatewayNameStep, times(1)).execute(outboundMessage);
        verify(acceptanceStep, times(1)).execute(outboundMessage);
        verify(confirmationStep, times(1)).execute(outboundMessage);
        verify(linkSubmissionStep, times(2)).execute(outboundMessage);
    }

    @Test
    void should_send_back_successfully_submission_rejection_evidence_when_security_error_occurs_during_outbound_message_processing() {
        var outboundMessage = MessageTestFixtures.createValidOutboundBusinessMessage();

        when(validationStep.execute(any())).thenReturn(outboundMessage);
        doThrow(RuntimeException.class).when(securityStep).execute(any());
        when(rejectionStep.execute(any())).thenReturn(outboundMessage);
        when(linkSubmissionStep.execute(any())).thenReturn(outboundMessage);

        assertThrows(
                ConnectorGatewaySubmissionException.class,
                () -> outboundMessagePipeline.process(outboundMessage)
        );

        verify(validationStep, times(1)).execute(outboundMessage);
        verify(securityStep, times(1)).execute(outboundMessage);
        verify(gatewayNameStep, times(0)).execute(outboundMessage);
        verify(acceptanceStep, times(0)).execute(outboundMessage);
        verify(confirmationStep, times(0)).execute(outboundMessage);
        verify(rejectionStep, times(1)).execute(outboundMessage);
        verify(linkSubmissionStep, times(1)).execute(outboundMessage);
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> outboundMessagePipeline.process(null)
        );
    }
}
