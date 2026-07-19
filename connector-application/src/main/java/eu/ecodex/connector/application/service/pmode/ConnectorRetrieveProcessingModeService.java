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
import eu.ecodex.connector.application.port.api.pmode.ConnectorRetrieveProcessingMode;
import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeRepository;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveProcessingMode} service.
 */
@Service
public class ConnectorRetrieveProcessingModeService implements ConnectorRetrieveProcessingMode {
    private final ConnectorProcessingModeRepository processingModeRepository;

    public ConnectorRetrieveProcessingModeService(
        ConnectorProcessingModeRepository processingModeRepository) {
        this.processingModeRepository = processingModeRepository;
    }

    @Override
    public ConnectorProcessingMode execute(@NonNull String uuid) {
        var processingMode = processingModeRepository.findByUuid(uuid);

        if (processingMode == null) {
            throw new ConnectorProcessingModeNotFoundException(
                "Processing mode not found for uuid: " + uuid
            );
        }

        return processingMode;
    }
}
