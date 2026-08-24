/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.api.link.ConnectorFindLinkPartner;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectorBackendClientVerifier")
public class ConnectorBackendClientVerifierTest {
    @Mock
    private ConnectorFindLinkPartner findLinkPartnerService;

    private ConnectorBackendClientVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new ConnectorBackendClientVerifier(findLinkPartnerService);
    }

    @Test
    void should_return_the_backend_client_name() {
        when(findLinkPartnerService.findByCertificateDn("cn=alice"))
            .thenReturn(LinkPartnerTestFixtures.createAliceBackendLinkPartner());

        var name = verifier.getBackendClient("cn=alice");

        assertThat(name).isEqualTo("backend_alice");
    }

    @Test
    void should_fail_when_the_certificate_dn_is_null() {
        assertThrows(
            NullPointerException.class,
            () -> verifier.getBackendClient(null)
        );
    }
}
