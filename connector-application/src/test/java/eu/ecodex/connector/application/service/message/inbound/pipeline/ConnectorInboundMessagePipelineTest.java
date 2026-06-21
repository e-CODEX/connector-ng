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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.message.inbound.pipeline.ConnectorInboundMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessageStep;
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
    private ConnectorMessageStep backendNameStep;
    @Mock
    private ConnectorMessageStep acceptanceStep;
    @Mock
    private ConnectorMessageStep securityStep;
    @Mock
    private ConnectorMessageStep nonDeliveryStep;
    @Mock
    private ConnectorMessageStep linkSubmissionStep;

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

    @Test
    void should_process_inbound_message_pipeline_successfully() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessage();

        when(backendNameStep.execute(any())).thenReturn(inboundMessage);
        when(acceptanceStep.execute(any())).thenReturn(inboundMessage);
        when(securityStep.execute(any())).thenReturn(inboundMessage);
        when(linkSubmissionStep.execute(any())).thenReturn(inboundMessage);

        inboundMessagePipeline.process(inboundMessage);

        verify(backendNameStep, times(1)).execute(inboundMessage);
        verify(acceptanceStep, times(1)).execute(inboundMessage);
        verify(securityStep, times(1)).execute(inboundMessage);
        verify(nonDeliveryStep, times(0)).execute(any());
        verify(linkSubmissionStep, times(2)).execute(any());
    }

    @Test
    void should_send_back_successfully_non_delivery_evidence_message_when_security_error_occurs_during_inbound_message_processing() {
        var inboundMessage = MessageTestFixtures.createInboundBusinessMessage();

        when(backendNameStep.execute(any())).thenReturn(inboundMessage);
        when(acceptanceStep.execute(any())).thenReturn(inboundMessage);
        doThrow(RuntimeException.class).when(securityStep).execute(any());
        when(linkSubmissionStep.execute(any())).thenReturn(inboundMessage);

        inboundMessagePipeline.process(inboundMessage);

        verify(backendNameStep, times(1)).execute(inboundMessage);
        verify(acceptanceStep, times(1)).execute(inboundMessage);
        verify(securityStep, times(1)).execute(inboundMessage);
        verify(nonDeliveryStep, times(1)).execute(any());
        verify(linkSubmissionStep, times(2)).execute(any());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(
                NullPointerException.class, () -> inboundMessagePipeline.process(null)
        );
    }
}
