/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorBusinessDomainRepository}.
 */
@Component
public class ConnectorBusinessDomainRepositoryImpl implements ConnectorBusinessDomainRepository {
    @Override
    public ConnectorBusinessDomain save(ConnectorBusinessDomain businessDomain) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ConnectorBusinessDomain findByIdentifier(ConnectorBusinessDomainIdentifier identifier) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
