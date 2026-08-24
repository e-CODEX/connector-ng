/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.link;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.api.link.ConnectorLinkPartnerVerifier;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkTransportStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorLinkSubmissionService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorLinkSubmitterService")
@ExtendWith(MockitoExtension.class)
class ConnectorLinkSubmitterServiceTest {
    @Mock
    private ConnectorLinkPartnerVerifier linkVerifier;
    @Mock
    private ConnectorLinkTransportStrategy transportStrategy;

    @InjectMocks
    private ConnectorLinkSubmitterService linkSubmissionService;

    @Test
    void should_submit_message_to_link_successfully() {
        doNothing().when(linkVerifier).verify(any());
        doNothing().when(transportStrategy).transport(any());

        var message = BusinessMessageTestFixtures.createOutboundMessage();
        linkSubmissionService.submit(message);

        verify(linkVerifier, times(1)).verify(any());
        verify(transportStrategy, times(1)).transport(any());
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(NullPointerException.class, () -> linkSubmissionService.submit(null));
    }
}
