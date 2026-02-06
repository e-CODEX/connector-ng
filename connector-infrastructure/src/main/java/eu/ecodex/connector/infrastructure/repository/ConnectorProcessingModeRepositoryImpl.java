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
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorProcessingModeRepository}.
 */
@Component
public class ConnectorProcessingModeRepositoryImpl implements ConnectorProcessingModeRepository {
    @Override
    public ConnectorProcessingMode save(
            ConnectorBusinessDomain businessDomain,
            ConnectorProcessingMode processingMode) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorProcessingMode findByBusinessDomainIdentifier(
            ConnectorBusinessDomainIdentifier identifier) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
