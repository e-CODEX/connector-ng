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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.application.service.impl.link.ConnectorLinkPartnerVerifierService;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@code ConnectorGatewayLinkEventHandler} implementation.
 */
@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorLinkPartnerVerifierServiceTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;

    @InjectMocks
    private ConnectorLinkPartnerVerifierService gatewayLinkEventHandler;

    @Test
    void should_submit_outbound_message_successfully_to_gateway_if_link_partner_is_valid() {
        when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultGatewayLinkPartner());

        var message = MessageTestFixtures.createOutboundBusinessMessage();

        gatewayLinkEventHandler.verify(message);

        verify(linkPartnerRepository, times(1)).findByName(any());
    }

    @Test
    void should_throw_exception_if_link_partner_does_not_exist_when_submitting_outbound_message_to_gateway() {
        when(linkPartnerRepository.findByName(any())).thenReturn(null);

        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> gatewayLinkEventHandler.verify(
                        MessageTestFixtures.createOutboundBusinessMessage()
                )
        );

        verify(linkPartnerRepository, times(1)).findByName(any());
    }

    @Test
    void should_throw_exception_if_message_is_null_when_submitting_outbound_message_to_gateway() {
        assertThrows(
                NullPointerException.class, () -> gatewayLinkEventHandler.verify(null)
        );
    }

    @Test
    void should_throw_exception_when_submitting_message_to_gateway_with_backend_link_partner() {
        when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultBackendLinkPartner());

        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> gatewayLinkEventHandler.verify(
                        MessageTestFixtures.createOutboundBusinessMessage())
        );
    }

    @Test
    void should_throw_exception_when_submitting_message_to_backend_with_gateway_link_partner() {
        when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultGatewayLinkPartner());

        assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> gatewayLinkEventHandler.verify(
                        MessageTestFixtures.createInboundBusinessMessage())
        );
    }
}
