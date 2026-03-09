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

import static eu.ecodex.connector.domain.model.message.ConnectorMessageDirectionType.BACKEND;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.service.ConnectorEvidenceService;
import eu.ecodex.connector.domain.api.service.ConnectorMessageService;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link ConnectorEvidenceService} interface, responsible for managing and
 * processing evidences related to messages in the connector domain.
 *
 * <p>This service handles various operations, including creating evidences of specific types,
 * processing evidence-triggered states and validations, and managing the rejection or confirmation
 * status of messages based on evidence data. It ensures that evidence is processed according to
 * defined priorities and rules.
 */
@Slf4j
@DomainService
public class ConnectorEvidenceServiceImpl implements ConnectorEvidenceService {
    private final ConnectorMessageService messageService;

    public ConnectorEvidenceServiceImpl(
            ConnectorMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void isEvidenceTriggeringAllowed(@NonNull ConnectorMessage message) {
        if (!this.messageService.isEvidenceMessage(message)) {
            throw new ConnectorEvidenceException("the message is not an evidence trigger message!");
        }

        var source = message.direction().getSource();

        if (source != BACKEND) {
            throw new ConnectorEvidenceException("only backend can generate trigger messages");
        }
    }
}
