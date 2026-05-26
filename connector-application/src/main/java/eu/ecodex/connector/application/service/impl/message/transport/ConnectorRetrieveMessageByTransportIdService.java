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

import eu.ecodex.connector.application.service.usecase.transport.ConnectorRetrieveMessageByTransportId;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorRetrieveMessageByTransportId} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRetrieveMessageByTransportIdService implements
        ConnectorRetrieveMessageByTransportId {
    private final ConnectorMessageTransportStepRepository transportStepRepository;
    private final ConnectorMessageRepository messageRepository;

    public ConnectorRetrieveMessageByTransportIdService(
            ConnectorMessageTransportStepRepository transportStepRepository,
            ConnectorMessageRepository messageRepository) {
        this.transportStepRepository = transportStepRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public ConnectorMessage execute(String transportIdentifier) {
        log.info("Retrieving message for transport identifier: [{}]", transportIdentifier);

        var transportStep = this.transportStepRepository.findByIdentifier(transportIdentifier);

        if (transportStep == null) {
            throw new NotFoundException(
                    "No transport step found for identifier: " + transportIdentifier
            );
        }

        if (transportStep.status() != ConnectorMessageTransportStatus.PENDING) {
            throw new ConnectorMessageTransportStepException(
                    "The message with transport id ["
                            + transportIdentifier + "] is not in pending state!"
            );
        }

        return this.messageRepository.findByIdentifier(transportStep.message().identifier());
    }
}
