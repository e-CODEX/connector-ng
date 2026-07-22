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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectorListLinkPartnersServiceTest {
    @Mock
    private ConnectorLinkPartnerRepository linkPartnerRepository;

    @InjectMocks
    private ConnectorListLinkPartnersService listLinkPartnersService;

    @Test
    void should_list_all_link_partners() {
        when(linkPartnerRepository.findAll())
            .thenReturn(List.of(LinkPartnerTestFixtures.createAliceBackendLinkPartner()));

        var linkPartners = listLinkPartnersService.execute(null);

        assertThat(linkPartners).isNotNull();
        assertThat(linkPartners).hasSize(1);
    }

    @Test
    void should_return_empty_list_if_no_link_partners_match_the_link_type() {
        when(linkPartnerRepository.findAll())
            .thenReturn(List.of(LinkPartnerTestFixtures.createDefaultGatewayLinkPartner()));

        var linkPartners = listLinkPartnersService.execute(ConnectorLinkType.BACKEND);

        assertThat(linkPartners).isNotNull();
        assertThat(linkPartners).isEmpty();
    }
}
