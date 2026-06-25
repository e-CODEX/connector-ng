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

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotEnabledException;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorBusinessDomainVerifier} service.
 */
@Slf4j
@Service
public class ConnectorBusinessDomainVerifierService implements ConnectorBusinessDomainVerifier {
    private final ConnectorBusinessDomainRepository businessDomainRepository;

    public ConnectorBusinessDomainVerifierService(
            ConnectorBusinessDomainRepository businessDomainRepository) {
        this.businessDomainRepository = businessDomainRepository;
    }

    @Override
    public void execute(@NonNull ConnectorBusinessDomainIdentifier identifier) {
        log.debug("checking business domain [{}] is enabled", identifier);
        var domain = businessDomainRepository.findByIdentifier(identifier);
        if (domain == null) {
            throw new ConnectorBusinessDomainNotFoundException(
                    "Business domain not found: " + identifier
            );
        }
        if (!domain.enabled()) {
            throw new ConnectorBusinessDomainNotEnabledException(
                    "Business domain is not enabled: " + identifier
            );
        }
    }
}
