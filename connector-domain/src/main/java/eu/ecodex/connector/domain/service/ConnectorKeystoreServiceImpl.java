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
import eu.ecodex.connector.domain.api.service.ConnectorKeystoreService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@link ConnectorKeystoreService}.
 */
@Slf4j
@DomainService
public class ConnectorKeystoreServiceImpl implements ConnectorKeystoreService {
    private final ConnectorKeystoreRepository keystoreRepository;

    public ConnectorKeystoreServiceImpl(ConnectorKeystoreRepository keystoreRepository) {
        this.keystoreRepository = keystoreRepository;
    }

    @Override
    public ConnectorKeystore persist(
            @NonNull ConnectorKeystore keystore,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug("persisting keystore: [{}]", keystore);

        return this.keystoreRepository.save(keystore, businessDomainIdentifier);
    }
}
