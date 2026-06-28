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

import eu.ecodex.connector.application.service.usecase.transport.ConnectorSetMessagesTransportStepToDownload;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorSetMessagesTransportStepToDownload} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorSetMessagesTransportStepToDownloadService
        implements ConnectorSetMessagesTransportStepToDownload {
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorSetMessagesTransportStepToDownloadService(
            ConnectorMessageTransportStepRepository transportStepRepository) {
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public void execute(@NonNull String backendName) {
        var identifiers = this.transportStepRepository.findPendingTransportSteps(backendName);
        this.transportStepRepository.updateStatus(
                identifiers,
                ConnectorMessageTransportStatus.DOWNLOADED
        );
    }
}
