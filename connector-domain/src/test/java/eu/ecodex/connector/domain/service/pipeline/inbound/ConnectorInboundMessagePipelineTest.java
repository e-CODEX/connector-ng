/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorInboundMessagePipeline}.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorInboundMessagePipelineTest {
    private ConnectorMessagePipeline inboundMessagePipeline;
    @Mock
    private ConnectorMessageStep connectorInboundMessageValidationStep;
    @Mock
    private ConnectorMessageStep connectorInboundMessageBackendValidationStep;
    @Mock
    private ConnectorMessageStep connectorInboundMessageAcceptanceStep;
    @Mock
    private ConnectorMessageStep connectorInboundMessageSecurityStep;
    @Mock
    private ConnectorMessageStep connectorInboundMessageNonDeliveryStep;
    @Mock
    private ConnectorLinkSubmissionService linkSubmissionService;

    @BeforeEach
    void setUp() {
        inboundMessagePipeline = new ConnectorInboundMessagePipeline(
                connectorInboundMessageValidationStep, connectorInboundMessageBackendValidationStep,
                connectorInboundMessageAcceptanceStep, connectorInboundMessageSecurityStep,
                connectorInboundMessageNonDeliveryStep, linkSubmissionService
        );
    }

    @Test
    void should_process_inbound_message_pipeline_successfully() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessage();

        when(connectorInboundMessageValidationStep.execute(any())).thenReturn(inboundMessage);
        when(connectorInboundMessageBackendValidationStep.execute(any())).thenReturn(inboundMessage);
        when(connectorInboundMessageAcceptanceStep.execute(any())).thenReturn(inboundMessage);
        when(connectorInboundMessageSecurityStep.execute(any())).thenReturn(inboundMessage);
        doNothing().when(linkSubmissionService).submit(any());

        inboundMessagePipeline.process(inboundMessage);

        verify(connectorInboundMessageValidationStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageBackendValidationStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageAcceptanceStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageSecurityStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageNonDeliveryStep, times(0)).execute(any());
        verify(linkSubmissionService, times(2)).submit(any());
    }

    @Test
    void should_send_back_successfully_non_delivery_evidence_message_when_security_error_occurs_during_inbound_message_processing() {
        var inboundMessage = MessageTestFixtures.createValidInboundBusinessMessage();

        when(connectorInboundMessageValidationStep.execute(any())).thenReturn(inboundMessage);
        when(connectorInboundMessageBackendValidationStep.execute(any())).thenReturn(inboundMessage);
        when(connectorInboundMessageAcceptanceStep.execute(any())).thenReturn(inboundMessage);
        doThrow(RuntimeException.class).when(connectorInboundMessageSecurityStep).execute(any());
        doNothing().when(linkSubmissionService).submit(any());

        inboundMessagePipeline.process(inboundMessage);

        verify(connectorInboundMessageValidationStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageBackendValidationStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageAcceptanceStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageSecurityStep, times(1)).execute(inboundMessage);
        verify(connectorInboundMessageNonDeliveryStep, times(1)).execute(any());
        verify(linkSubmissionService, times(2)).submit(any());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> inboundMessagePipeline.process(null)
        );
    }
}
