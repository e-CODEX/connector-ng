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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.api.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorBackendLinkEventHandler} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorBackendLinkEventHandlerTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;
    @Mock
    private ConnectorLinkTransportStrategy linkTransportStrategy;

    private ConnectorEventHandler backendLinkEventHandler;

    @BeforeEach
    void setUp() {
        backendLinkEventHandler = new ConnectorBackendLinkEventHandler(
                linkPartnerRepository, linkTransportStrategy
        );
    }

    @Test
    void should_submit_inbound_message_successfully_to_backend_if_link_partner_is_valid() {
        when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultBackendLinkPartner());
        doNothing().when(linkTransportStrategy).process(any(), any());

        var message = MessageTestFixtures.createValidInboundBusinessMessage();
        backendLinkEventHandler.handle(message);

        verify(linkPartnerRepository, times(1)).findByName(any());
        verify(linkTransportStrategy, times(1)).process(any(), any());
    }

    @Test
    void should_throw_exception_if_link_partner_does_not_exist_when_submitting_inbound_message_to_backend() {
        when(linkPartnerRepository.findByName(any())).thenReturn(null);

        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> backendLinkEventHandler.handle(
                        MessageTestFixtures.createValidInboundBusinessMessage())
        );

        verify(linkPartnerRepository, times(1)).findByName(any());
        verify(linkTransportStrategy, times(0)).process(any(), any());
    }

    @Test
    void should_throw_exception_if_message_is_null_when_submitting_inbound_message_to_backend() {
        assertThrows(
                NullPointerException.class, () -> backendLinkEventHandler.handle(null)
        );
    }
}
