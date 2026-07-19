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

import eu.ecodex.connector.application.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveTransportStep;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service implementation of the {@link ConnectorRetrieveTransportStep} interface.
 */
@Service
public class ConnectorRetrieveTransportStepService implements ConnectorRetrieveTransportStep {
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorRetrieveTransportStepService(
        ConnectorMessageTransportStepRepository transportStepRepository) {
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public ConnectorMessageTransportStep execute(@NonNull String messageIdentifier) {
        var transportStep = this.transportStepRepository.findByMessageIdentifierOrRemoteSystemId(
            messageIdentifier);

        if (transportStep == null) {
            throw new ConnectorMessageTransportStepNotFoundException(
                "No transport step found for identifier [%s]".formatted(messageIdentifier)
            );
        }

        return transportStep;
    }
}
