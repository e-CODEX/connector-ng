/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.domain.api.service.ConnectorLinkService;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.service.link.ConnectorLinkServiceImpl;
import eu.ecodex.connector.link.LinkPartnerTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorLinkService} implementation.
 */
@SuppressWarnings("DataFlowIssue")
public class ConnectorLinkServiceTest {
    private ConnectorLinkService connectorLinkService;

    @BeforeEach
    void setUp() {
        this.connectorLinkService = new ConnectorLinkServiceImpl();
    }

    @Test
    void should_succeed_to_add_link_partner() {
        this.connectorLinkService.add(LinkPartnerTestFixtures.createLinkPartner());
        var linkPartnerName = new ConnectorLinkPartnerName("default_gateway");
        var linkPartner = this.connectorLinkService.getByLinkPartnerName(linkPartnerName);
        assertThat(linkPartner).isNotNull();
    }

    @Test
    void should_throw_null_pointer_exception_when_adding_link_partner_if_the_value_is_null() {
        assertThrows(NullPointerException.class, () -> this.connectorLinkService.add(null));
    }

    @Test
    void should_succeed_to_find_link_partner_by_link_partner_name() {
        var linkPartnerName = new ConnectorLinkPartnerName("default_gateway");
        var linkPartner = this.connectorLinkService.getByLinkPartnerName(linkPartnerName);
        assertThat(linkPartner).isNull();

        this.connectorLinkService.add(LinkPartnerTestFixtures.createLinkPartner());

        linkPartner = this.connectorLinkService.getByLinkPartnerName(linkPartnerName);
        assertThat(linkPartner).isNotNull();
        assertThat(linkPartner.name()).isEqualTo(linkPartnerName);
    }

    @Test
    void should_throw_null_pointer_exception_when_finding_link_partner_by_link_partner_name_if_the_value_is_null() {
        assertThrows(NullPointerException.class, () -> this.connectorLinkService.getByLinkPartnerName(null));
    }
}
