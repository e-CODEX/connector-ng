/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.transport;

import eu.ecodex.connector.application.service.usecase.transport.ConnectorListTransportSteps;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorListTransportSteps} service.
 */
@Service
public class ConnectorListTransportStepsService implements ConnectorListTransportSteps {
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorListTransportStepsService(
            ConnectorMessageTransportStepRepository transportStepRepository) {
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public ConnectorPageResult<ConnectorMessageTransportStep> execute(
            @NonNull ConnectorPageRequest pageRequest,
            String messageOrRemoteSystemIdentifier,
            String linkPartnerName) {
        return transportStepRepository.findAll(pageRequest,
                                               messageOrRemoteSystemIdentifier, linkPartnerName);
    }
}
