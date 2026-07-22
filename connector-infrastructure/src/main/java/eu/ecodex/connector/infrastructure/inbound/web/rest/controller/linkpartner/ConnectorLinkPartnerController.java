/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.linkpartner;

import eu.ecodex.connector.application.port.api.link.ConnectorListLinkPartners;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing link partner within the connector system.
 */
@RestController
public class ConnectorLinkPartnerController implements ConnectorLinkPartnerApi {
    private final ConnectorListLinkPartners listLinkPartners;

    public ConnectorLinkPartnerController(ConnectorListLinkPartners listLinkPartners) {
        this.listLinkPartners = listLinkPartners;
    }

    @Override
    public List<ConnectorLinkPartner> listLinkPartners(ConnectorLinkType linkType) {
        return listLinkPartners.execute(linkType);
    }
}
