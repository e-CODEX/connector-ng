/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.pmode;

import eu.ecodex.connector.application.service.usecase.pmode.ConnectorListProcessingMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListProcessingMode} service.
 */
@Service
public class ConnectorListProcessingModeService implements ConnectorListProcessingMode {
    private final ConnectorProcessingModeRepository processingModeRepository;

    public ConnectorListProcessingModeService(
            ConnectorProcessingModeRepository processingModeRepository) {
        this.processingModeRepository = processingModeRepository;
    }

    @Override
    public List<ConnectorProcessingMode> execute() {
        return processingModeRepository.findAll();
    }
}
