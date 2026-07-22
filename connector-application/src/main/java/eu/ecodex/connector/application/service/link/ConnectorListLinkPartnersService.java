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

import eu.ecodex.connector.application.port.api.link.ConnectorListLinkPartners;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorListLinkPartners}.
 */
@Service
public class ConnectorListLinkPartnersService implements ConnectorListLinkPartners {
    private final ConnectorLinkPartnerRepository linkPartnerRepository;

    public ConnectorListLinkPartnersService(
        ConnectorLinkPartnerRepository linkPartnerRepository) {
        this.linkPartnerRepository = linkPartnerRepository;
    }

    @Override
    public List<ConnectorLinkPartner> execute(ConnectorLinkType linkType) {
        if (linkType == null) {
            return linkPartnerRepository.findAll();
        }

        return linkPartnerRepository.findAll()
                                    .stream()
                                    .filter(partner -> partner.type() == linkType)
                                    .toList();
    }
}
