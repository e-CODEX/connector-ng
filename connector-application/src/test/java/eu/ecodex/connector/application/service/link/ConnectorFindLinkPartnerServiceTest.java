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

import eu.ecodex.connector.application.service.impl.link.ConnectorFindLinkPartnerService;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerException;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
public class ConnectorFindLinkPartnerServiceTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;

    @InjectMocks
    private ConnectorFindLinkPartnerService findLinkPartnerService;

    @Test
    void should_find_link_partner_by_certificate_dn_successfully() {
        when(linkPartnerRepository.findByCertificateDn("cn=alice"))
               .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner());

        var linkPartner = this.findLinkPartnerService.findByCertificateDn("cn=alice");

        assertThat(linkPartner).isNotNull();
        assertThat(linkPartner.name().name()).isEqualTo("backend_alice");
        assertThat(linkPartner.certificateDn()).isEqualTo("cn=alice");
    }

    @Test
    void should_throw_null_link_partner_exception_when_searching_link_partner_by_null_certificate_dn() {
        when(linkPartnerRepository.findByCertificateDn("cn=alice")).thenReturn(null);

        assertThrows(
                ConnectorLinkPartnerException.class,
                () -> this.findLinkPartnerService.findByCertificateDn("cn=alice")
        );
    }

    @Test
    void should_throw_null_pointer_exception_when_searching_link_partner_by_null_certificate_dn() {
        assertThrows(
                NullPointerException.class,
                () -> this.findLinkPartnerService.findByCertificateDn(null)
        );
    }
}
