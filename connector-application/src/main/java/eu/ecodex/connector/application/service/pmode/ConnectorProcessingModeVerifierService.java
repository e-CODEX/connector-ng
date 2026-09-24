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

import eu.ecodex.connector.application.exception.ConnectorProcessingModeInvalidTruststoreException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.application.port.api.pmode.ConnectorProcessingModeVerifier;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * A service implementation of {@link ConnectorProcessingModeVerifier} responsible for verifying the
 * processing mode for a given business domain identifier.
 */
@Service
public class ConnectorProcessingModeVerifierService implements ConnectorProcessingModeVerifier {
    private final ConnectorProcessingModeRepository processingModeRepository;

    public ConnectorProcessingModeVerifierService(
        ConnectorProcessingModeRepository processingModeRepository
    ) {
        this.processingModeRepository = processingModeRepository;
    }

    @Override
    public void execute(@NonNull ConnectorBusinessDomainIdentifier identifier) {
        var processingMode = processingModeRepository.findByBusinessDomainIdentifier(identifier);

        if (processingMode == null) {
            throw new ConnectorProcessingModeNotFoundException(
                "Processing mode not found for business domain [%s]".formatted(identifier)
            );
        }

        if (!processingMode.truststore().isValid()) {
            throw new ConnectorProcessingModeInvalidTruststoreException(
                "Invalid processing mode truststore"
            );
        }
    }
}
