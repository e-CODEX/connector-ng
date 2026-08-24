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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorLinkPartnerException;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorFindLinkPartnerService")
public class ConnectorFindLinkPartnerServiceTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;

    @InjectMocks
    private ConnectorFindLinkPartnerService findLinkPartnerService;

    @Nested
    @DisplayName("find by certificate DN")
    class FindByCertificateDn {
        @Test
        void should_return_the_matching_link_partner() {
            when(linkPartnerRepository.findByCertificateDn("cn=alice"))
                .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner());

            var linkPartner = findLinkPartnerService.findByCertificateDn("cn=alice");

            assertThat(linkPartner).isNotNull();
            assertThat(linkPartner.name().name()).isEqualTo("backend_alice");
            assertThat(linkPartner.certificateDn()).isEqualTo("cn=alice");
        }

        @Test
        void should_fail_when_no_link_partner_matches() {
            when(linkPartnerRepository.findByCertificateDn("cn=alice")).thenReturn(null);

            assertThrows(
                ConnectorLinkPartnerException.class,
                () -> findLinkPartnerService.findByCertificateDn("cn=alice")
            );
        }

        @Test
        void should_fail_when_the_certificate_dn_is_null() {
            assertThrows(
                NullPointerException.class,
                () -> findLinkPartnerService.findByCertificateDn(null)
            );
        }
    }
}
