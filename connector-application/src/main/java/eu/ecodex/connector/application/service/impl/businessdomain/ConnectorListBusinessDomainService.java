/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.businessdomain;

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListBusinessDomain} service.
 */
@Slf4j
@Service
public class ConnectorListBusinessDomainService implements ConnectorListBusinessDomain {
    private final ConnectorBusinessDomainRepository businessDomainRepository;

    public ConnectorListBusinessDomainService(
            ConnectorBusinessDomainRepository businessDomainRepository) {
        this.businessDomainRepository = businessDomainRepository;
    }

    @Override
    public List<ConnectorBusinessDomain> execute() {
        return businessDomainRepository.findAll();
    }
}
