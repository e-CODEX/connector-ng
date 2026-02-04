/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.link;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorLinkService;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import jakarta.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the {@link ConnectorLinkService} interface.
 */
@DomainService
public class ConnectorLinkServiceImpl implements ConnectorLinkService {
    private final Map<ConnectorLinkPartnerName, ConnectorLinkPartner> linkPartners;

    public ConnectorLinkServiceImpl() {
        this.linkPartners = new ConcurrentHashMap<>();
    }

    @Override
    public void add(@Nonnull ConnectorLinkPartner linkPartner) {
        this.linkPartners.put(linkPartner.name(), linkPartner);
    }

    @Override
    public ConnectorLinkPartner getByLinkPartnerName(
            @Nonnull ConnectorLinkPartnerName linkPartnerName) {
        return this.linkPartners.get(linkPartnerName);
    }
}
