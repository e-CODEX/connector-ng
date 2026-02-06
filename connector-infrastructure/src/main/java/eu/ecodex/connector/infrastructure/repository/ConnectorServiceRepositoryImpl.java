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

import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorServiceRepository}.
 */
@Component
public class ConnectorServiceRepositoryImpl implements ConnectorServiceRepository {
    @Override
    public ConnectorService findByNameAndBusinessDomain(
            String serviceName,
            String businessDomainIdentifier) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
