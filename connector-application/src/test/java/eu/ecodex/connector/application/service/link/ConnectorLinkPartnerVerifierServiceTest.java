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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorLinkPartnerSubmissionException;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("ConnectorLinkPartnerVerifierService")
public class ConnectorLinkPartnerVerifierServiceTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;

    @InjectMocks
    private ConnectorLinkPartnerVerifierService verifierService;

    @Nested
    @DisplayName("when verification succeeds")
    class WhenVerificationSucceeds {
        @Test
        void should_pass_when_the_link_partner_is_valid() {
            when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultGatewayLinkPartner());

            var message = BusinessMessageTestFixtures.createOutboundMessage();

            assertThatCode(() -> verifierService.verify(message)).doesNotThrowAnyException();

            verify(linkPartnerRepository).findByName(any());
        }
    }

    @Nested
    @DisplayName("when verification fails")
    class WhenVerificationFails {
        @Test
        void should_fail_when_the_message_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> verifierService.verify(null)
            );
        }

        @Test
        void should_fail_when_the_link_partner_does_not_exist() {
            when(linkPartnerRepository.findByName(any())).thenReturn(null);

            assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> verifierService.verify(BusinessMessageTestFixtures.createOutboundMessage())
            );

            verify(linkPartnerRepository).findByName(any());
        }

        @Test
        void should_fail_when_an_outbound_message_targets_a_backend_link_partner() {
            when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultBackendLinkPartner());

            assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> verifierService.verify(BusinessMessageTestFixtures.createOutboundMessage())
            );
        }

        @Test
        void should_fail_when_an_inbound_message_targets_a_gateway_link_partner() {
            when(linkPartnerRepository.findByName(any()))
                .thenReturn(LinkPartnerTestFixtures.createDefaultGatewayLinkPartner());

            assertThrows(
                ConnectorLinkPartnerSubmissionException.class,
                () -> verifierService.verify(BusinessMessageTestFixtures.createInboundMessage())
            );
        }
    }
}
