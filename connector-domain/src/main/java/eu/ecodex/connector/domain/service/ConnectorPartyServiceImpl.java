/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorPartyService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.spi.ConnectorPartyRepository;

/**
 * Default implementation of {@link ConnectorPartyService}.
 */
@DomainService
public class ConnectorPartyServiceImpl implements ConnectorPartyService {
    private final ConnectorPartyRepository partyRepository;

    public ConnectorPartyServiceImpl(ConnectorPartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Override
    public boolean exists(
            ConnectorParty party,
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {

        return this.partyRepository.findByPartyAndBusinessDomain(
                party, businessDomainIdentifier
        ) != null;
    }
}
