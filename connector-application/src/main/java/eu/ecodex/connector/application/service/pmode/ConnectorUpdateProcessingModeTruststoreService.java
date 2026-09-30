/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.pmode;

import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.api.pmode.ConnectorUpdateProcessingModeTruststore;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ConnectorUpdateProcessingModeTruststore} service.
 */
@Service
@Transactional
public class ConnectorUpdateProcessingModeTruststoreService implements
    ConnectorUpdateProcessingModeTruststore {
    private final ConnectorProcessingModeRepository processingModeRepository;

    public ConnectorUpdateProcessingModeTruststoreService(
        ConnectorProcessingModeRepository processingModeRepository) {
        this.processingModeRepository = processingModeRepository;
    }

    @Override
    public ConnectorTruststore execute(String pmodeUuid, ConnectorTruststore truststore) {
        var processingMode = processingModeRepository.findByUuid(pmodeUuid);

        if (processingMode == null) {
            throw new ConnectorProcessingModeNotFoundException(
                "Processing mode with UUID %s not found".formatted(pmodeUuid)
            );
        }

        var updated = this.processingModeRepository.updateTruststore(pmodeUuid, truststore);

        return updated.truststore();
    }
}
