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

import eu.ecodex.connector.application.service.usecase.transport.ConnectorChangePendingMessagesStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.ConnectorMessageTransportStepRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorChangePendingMessagesStatus} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorChangePendingMessagesStatusService
        implements ConnectorChangePendingMessagesStatus {
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorChangePendingMessagesStatusService(
            ConnectorMessageTransportStepRepository transportStepRepository) {
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public void execute(
            @NonNull String backendName,
            @Nonnull ConnectorMessageTransportStatus status) {
        var identifiers = this.transportStepRepository.findPendingTransportSteps(backendName);
        this.transportStepRepository.updateStatus(
                identifiers,
                status
        );
    }
}
