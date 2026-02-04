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
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.api.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.api.service.ConnectorLinkService;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.utils.MessageUtil;
import eu.ecodex.connector.utils.link.LinkPartnerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorGatewayLinkEventHandler} implementation.
 */
@SuppressWarnings({"DataFlowIssue", "checkstyle:LineLength"})
@ExtendWith(MockitoExtension.class)
public class ConnectorGatewayLinkEventHandlerTest {
    @Mock
    private ConnectorLinkService linkService;
    @Mock
    private ConnectorLinkTransportStrategy linkTransportStrategy;
    private ConnectorGatewayLinkEventHandler gatewayLinkEventHandler;

    @BeforeEach
    void setUp() {
        gatewayLinkEventHandler = new ConnectorGatewayLinkEventHandler(
                linkService, linkTransportStrategy
        );
    }

    @Test
    void should_submit_outbound_message_successfully_to_gateway_if_link_partner_is_valid() {
        when(linkService.getByLinkPartnerName(any()))
                .thenReturn(LinkPartnerUtil.createDefaultGatewayLinkPartner());
        doNothing().when(linkTransportStrategy).process(any(), any());

        var message = MessageUtil.createValidOutboundBusinessMessage();
        gatewayLinkEventHandler.handle(message);

        verify(linkService, times(1)).getByLinkPartnerName(any());
        verify(linkTransportStrategy, times(1)).process(any(), any());
    }

    @Test
    void should_throw_exception_if_link_partner_does_not_exist_when_submitting_outbound_message_to_gateway() {
        when(linkService.getByLinkPartnerName(any())).thenReturn(null);

        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> gatewayLinkEventHandler.handle(
                        MessageUtil.createValidOutboundBusinessMessage())
        );

        verify(linkService, times(1)).getByLinkPartnerName(any());
        verify(linkTransportStrategy, times(0)).process(any(), any());
    }

    @Test
    void should_throw_exception_if_message_is_null_when_submitting_outbound_message_to_gateway() {
        assertThrows(
                NullPointerException.class, () -> gatewayLinkEventHandler.handle(null)
        );
    }
}
