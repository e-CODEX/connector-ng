/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * e-Codex Connector message service implementation.
 */
@Slf4j
@DomainService
public class ConnectorMessageServiceImpl implements ConnectorMessageService {
    private final ConnectorMessageRepository messageRepository;

    /**
     * Creates an instance of {@code ConnectorMessageServiceImpl}.
     *
     * @param messageRepository               the repository responsible for managing connector
     *                                        messages; must not be null.
     */
    public ConnectorMessageServiceImpl(
            ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public ConnectorMessage findByIdentifierAndDirection(
            @NonNull ConnectorMessage message, @NonNull ConnectorMessageDirection direction) {
        log.debug(
                "Finding message with identifier: [{}] and direction: [{}]",
                message.identifier(), direction
        );

        var foundMessage = this.messageRepository.findByIdentifierAndDirection(message, direction);

        if (foundMessage == null) {
            throw new ConnectorMessageNotFoundException("Message not found");
        }

        return foundMessage;
    }

    @Override
    public boolean isEvidenceMessage(@NonNull ConnectorMessage message) {
        return message.businessContent() == null
               && message.evidences() != null
               && !message.evidences().isEmpty();
    }

    @Override
    public boolean isEvidenceTriggerMessage(@NonNull ConnectorMessage message) {
        if (message.businessContent() != null) {
            return false;
        }
        var transported = message.transportedEvidences();
        return transported != null
               && transported.size() == 1
               && transported.getFirst().attachment() == null;
    }
}
