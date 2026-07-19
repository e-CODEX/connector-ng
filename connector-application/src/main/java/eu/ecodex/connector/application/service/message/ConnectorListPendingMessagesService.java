/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.port.api.transport.ConnectorListPendingMessages;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorListPendingMessages} service.
 */
@Service
@Transactional
public class ConnectorListPendingMessagesService implements ConnectorListPendingMessages {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageTransportStepRepository transportStepRepository;

    public ConnectorListPendingMessagesService(
        ConnectorMessageRepository messageRepository,
        ConnectorMessageTransportStepRepository transportStepRepository) {
        this.messageRepository = messageRepository;
        this.transportStepRepository = transportStepRepository;
    }

    @Override
    public List<ConnectorMessage> execute(@NonNull String backendName) {
        var pendingIds = this.transportStepRepository.findPendingMessagesIds(backendName);
        return this.messageRepository.findAllByIdentifier(pendingIds);
    }
}
