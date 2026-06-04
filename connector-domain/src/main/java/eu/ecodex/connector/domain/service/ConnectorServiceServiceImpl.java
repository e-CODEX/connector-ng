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
import eu.ecodex.connector.domain.api.service.ConnectorServiceService;
import eu.ecodex.connector.domain.exception.ConnectorServiceNotFoundException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.pmode.ConnectorServiceRepository;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default Implementation of the {@link ConnectorServiceService}.
 */
@Slf4j
@DomainService
public class ConnectorServiceServiceImpl implements ConnectorServiceService {
    private final ConnectorServiceRepository serviceRepository;

    public ConnectorServiceServiceImpl(ConnectorServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ConnectorService> persistAll(
            @NonNull List<ConnectorService> services,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug(
                "Saving services [{}] for business domain [{}]", services,
                businessDomainIdentifier
        );

        return this.serviceRepository.saveAll(services, businessDomainIdentifier);
    }

    @Override
    public ConnectorService findByNameAndBusinessDomain(
            @NonNull String serviceName,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug(
                "Finding service with name [{}] and business domain [{}]", serviceName,
                businessDomainIdentifier
        );

        var service = this.serviceRepository.findByNameAndBusinessDomain(
                serviceName, businessDomainIdentifier
        );

        if (service == null) {
            log.warn(
                    "Service with name [{}] and business domain [{}] not found",
                    serviceName, businessDomainIdentifier
            );
            throw new ConnectorServiceNotFoundException("Service not found");
        }

        return service;
    }
}
