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

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRegisterBusinessDomain} service.
 */
@Slf4j
@Service
public class ConnectorRegisterBusinessDomainService implements ConnectorRegisterBusinessDomain {
    private final ConnectorBusinessDomainRepository businessDomainRepository;

    public ConnectorRegisterBusinessDomainService(
            ConnectorBusinessDomainRepository businessDomainRepository) {
        this.businessDomainRepository = businessDomainRepository;
    }

    @Override
    public ConnectorBusinessDomain execute(@NonNull ConnectorBusinessDomain businessDomain) {
        log.debug("creating new business domain: [{}]", businessDomain);

        var foundBusinessDomain = this.businessDomainRepository.findByIdentifier(
                businessDomain.identifier()
        );

        if (foundBusinessDomain != null) {
            throw new ConnectorBusinessDomainAlreadyExistsException(
                    "business domain already exists"
            );
        }

        return this.businessDomainRepository.save(businessDomain);
    }
}
