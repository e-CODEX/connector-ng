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

import eu.ecodex.connector.application.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.application.port.api.message.ConnectorRetrieveMessage;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * A service class responsible for retrieving a specific connector message by implementing the
 * ConnectorRetrieveMessage interface. It provides the logic to process and return the connector
 * message corresponding to the given identifier.
 */
@Service
public class ConnectorRetrieveMessageService implements ConnectorRetrieveMessage {
    private final ConnectorMessageRepository messageRepository;

    public ConnectorRetrieveMessageService(ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull String identifier) {
        var message = messageRepository.findByIdentifier(identifier);

        if (message == null) {
            throw new ConnectorMessageNotFoundException(
                "Message not found for identifier: " + identifier
            );
        }

        return message;
    }
}
