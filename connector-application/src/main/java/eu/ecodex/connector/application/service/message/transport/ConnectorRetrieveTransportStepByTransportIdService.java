/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.transport;

import eu.ecodex.connector.application.exception.message.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStepByTransportId;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveTransportStepByTransportId} service.
 */
@Service
public class ConnectorRetrieveTransportStepByTransportIdService implements
    ConnectorRetrieveTransportStepByTransportId {
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorRetrieveTransportStepByTransportIdService(
        ConnectorMessageTransportStepRepository transportStepRepository) {
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public ConnectorMessageTransportStep execute(@NonNull String transportIdentifier) {
        var step = transportStepRepository.findByIdentifier(transportIdentifier);

        if (step == null) {
            throw new ConnectorMessageTransportStepNotFoundException(
                "No transport step found with identifier %s".formatted(transportIdentifier)
            );
        }

        return step;
    }
}
