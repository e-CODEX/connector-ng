/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.link;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorLinkSubmissionService} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
class ConnectorLinkSubmissionServiceTest {
    @Mock
    private ConnectorEventPublisher backendLinkEventPublisher;
    @Mock
    private ConnectorEventPublisher gatewayLinkEventPublisher;

    private ConnectorLinkSubmissionService linkSubmissionService;

    @BeforeEach
    void setUp() {
        this.linkSubmissionService = new ConnectorLinkSubmissionServiceImpl(
                backendLinkEventPublisher, gatewayLinkEventPublisher
        );
    }

    @Test
    void should_submit_outgoing_message_to_link_successfully() {
        doNothing().when(gatewayLinkEventPublisher).publish(any());

        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        linkSubmissionService.submit(message);

        verify(gatewayLinkEventPublisher, times(1)).publish(any());
        verify(backendLinkEventPublisher, times(0)).publish(any());
    }

    @Test
    void should_submit_incoming_message_to_link_successfully() {
        doNothing().when(backendLinkEventPublisher).publish(any());

        var message = MessageTestFixtures.createValidInboundBusinessMessage();
        linkSubmissionService.submit(message);

        verify(gatewayLinkEventPublisher, times(0)).publish(any());
        verify(backendLinkEventPublisher, times(1)).publish(any());
    }

    @Test
    void should_not_submit_message_to_link_when_gateway_name_is_unknown() {
        var message = MessageTestFixtures.createValidOutboundBusinessMessageWithoutGatewayName();
        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> linkSubmissionService.submit(message)
        );
    }

    @Test
    void should_not_submit_message_to_link_when_backend_name_is_unknown() {
        var message = MessageTestFixtures.createValidInboundBusinessMessageWithoutBackendName();
        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> linkSubmissionService.submit(message)
        );
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThrows(NullPointerException.class, () -> linkSubmissionService.submit(null));
    }
}
