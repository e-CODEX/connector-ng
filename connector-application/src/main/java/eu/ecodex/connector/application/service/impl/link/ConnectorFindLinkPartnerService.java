/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.link;

import eu.ecodex.connector.application.service.usecase.link.ConnectorFindLinkPartner;
import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.spi.ConnectorLinkPartnerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorFindLinkPartner} service.
 */
@Slf4j
@Service
public class ConnectorFindLinkPartnerService implements ConnectorFindLinkPartner {
    private final ConnectorLinkPartnerRepository linkPartnerRepository;

    public ConnectorFindLinkPartnerService(ConnectorLinkPartnerRepository linkPartnerRepository) {
        this.linkPartnerRepository = linkPartnerRepository;
    }

    @Override
    public ConnectorLinkPartner findByCertificateDn(@NonNull String certificateDn) {
        log.debug("Retrieving link partner by certificate DN: {}", certificateDn);
        var linkPartner = this.linkPartnerRepository.findByCertificateDn(certificateDn);

        if (linkPartner == null) {
            throw new ConnectorLinkPartnerException(
                    "No link partner found for certificateDn: " + certificateDn
            );
        }

        return linkPartner;
    }
}
