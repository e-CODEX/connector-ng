/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.exception.ConnectorGatewaySubmissionException;
import eu.ecodex.connector.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorOutboundMessagePipeline}.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorOutboundMessagePipelineTest {
    private ConnectorMessagePipeline outboundMessagePipeline;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageValidationStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageSecurityStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageGatewayValidationStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageEbmsIdStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageSubmissionAcceptanceStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageConfirmationStep;
    @Mock
    private ConnectorMessageStep connectorOutboundMessageRejectionStep;
    @Mock
    private ConnectorLinkSubmissionService linkSubmissionService;

    @BeforeEach
    void setUp() {
        outboundMessagePipeline = new ConnectorOutboundMessagePipeline(
                connectorOutboundMessageValidationStep, connectorOutboundMessageSecurityStep,
                connectorOutboundMessageGatewayValidationStep, connectorOutboundMessageEbmsIdStep,
                connectorOutboundMessageSubmissionAcceptanceStep,
                connectorOutboundMessageConfirmationStep, connectorOutboundMessageRejectionStep,
                linkSubmissionService
        );
    }

    @Test
    void should_process_outbound_message_pipeline_successfully() {
        var outboundMessage = MessageUtil.createValidOutboundBusinessMessage();

        when(connectorOutboundMessageValidationStep.execute(any())).thenReturn(outboundMessage);
        when(connectorOutboundMessageSecurityStep.execute(any())).thenReturn(outboundMessage);
        when(connectorOutboundMessageGatewayValidationStep.execute(any())).thenReturn(
                outboundMessage);
        when(connectorOutboundMessageEbmsIdStep.execute(any())).thenReturn(outboundMessage);
        when(connectorOutboundMessageSubmissionAcceptanceStep.execute(any())).thenReturn(
                outboundMessage);
        when(connectorOutboundMessageConfirmationStep.execute(any())).thenReturn(outboundMessage);
        doNothing().when(linkSubmissionService).submit(any());

        outboundMessagePipeline.process(outboundMessage);

        verify(connectorOutboundMessageValidationStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageSecurityStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageGatewayValidationStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageSubmissionAcceptanceStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageConfirmationStep, times(1)).execute(outboundMessage);
        verify(linkSubmissionService, times(2)).submit(outboundMessage);
    }

    @Test
    void should_send_back_successfully_submission_rejection_evidence_when_security_error_occurs_during_outbound_message_processing() {
        var outboundMessage = MessageUtil.createValidOutboundBusinessMessage();

        when(connectorOutboundMessageValidationStep.execute(any())).thenReturn(outboundMessage);
        doThrow(RuntimeException.class).when(
                connectorOutboundMessageSecurityStep).execute(any());
        doNothing().when(linkSubmissionService).submit(any());

        assertThrows(
                ConnectorGatewaySubmissionException.class,
                () -> outboundMessagePipeline.process(outboundMessage)
        );

        verify(connectorOutboundMessageValidationStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageSecurityStep, times(1)).execute(outboundMessage);
        verify(connectorOutboundMessageGatewayValidationStep, times(0)).execute(outboundMessage);
        verify(connectorOutboundMessageSubmissionAcceptanceStep, times(0)).execute(outboundMessage);
        verify(connectorOutboundMessageConfirmationStep, times(0)).execute(outboundMessage);
        verify(connectorOutboundMessageRejectionStep, times(1)).execute(outboundMessage);
        verify(linkSubmissionService, times(1)).submit(any());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> outboundMessagePipeline.process(null)
        );
    }
}
