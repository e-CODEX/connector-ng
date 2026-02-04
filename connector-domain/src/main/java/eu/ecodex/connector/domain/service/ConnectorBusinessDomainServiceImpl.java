/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainException;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@link ConnectorBusinessDomainService} interface.
 */
@Slf4j
@DomainService
public class ConnectorBusinessDomainServiceImpl implements ConnectorBusinessDomainService {
    private final ConnectorBusinessDomainRepository businessDomainRepository;

    /**
     * Constructs a new instance of {@code ConnectorBusinessDomainServiceImpl}.
     *
     * @param businessDomainRepository the repository used for managing
     *                                 {@code ConnectorBusinessDomain} entities. This parameter must
     *                                 not be null.
     */
    public ConnectorBusinessDomainServiceImpl(
            ConnectorBusinessDomainRepository businessDomainRepository) {
        this.businessDomainRepository = businessDomainRepository;
    }

    @Override
    public ConnectorBusinessDomain register(@NonNull ConnectorBusinessDomain businessDomain) {
        log.debug("creating new business domain: [{}]", businessDomain);

        var foundBusinessDomain = this.businessDomainRepository.findByIdentifier(
                businessDomain.identifier()
        );

        if (foundBusinessDomain != null) {
            throw new ConnectorBusinessDomainException("business domain already exists");
        }

        return this.businessDomainRepository.save(businessDomain);
    }

    @Override
    public ConnectorBusinessDomain findByIdentifier(
            @NonNull ConnectorBusinessDomainIdentifier identifier) {
        log.debug("finding business domain by identifier: [{}]", identifier);

        var businessDomain = this.businessDomainRepository.findByIdentifier(identifier);

        if (businessDomain == null) {
            throw new ConnectorBusinessDomainNotFoundException("business domain not found");
        }

        return businessDomain;
    }
}
