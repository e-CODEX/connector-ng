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

import eu.ecodex.connector.application.service.usecase.transport.ConnectorAcknowledgeMessageTransportStep;
import eu.ecodex.connector.application.service.usecase.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepException;
import eu.ecodex.connector.domain.exception.NotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageErrorRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link ConnectorAcknowledgeMessageTransportStep} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorAcknowledgeMessageTransportStepService implements
        ConnectorAcknowledgeMessageTransportStep {
    private final ConnectorMessageTransportStepRepository transportStepRepository;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorMessageErrorRepository messageErrorRepository;

    /**
     * Constructor for the ConnectorUpdateMessageTransportStepService class. Initializes the service
     * with the required repositories.
     *
     * @param transportStepRepository the repository used for managing message transport steps
     * @param messageRepository       the repository used for managing connector messages
     * @param messageErrorRepository  the repository used for managing connector message errors
     */
    public ConnectorAcknowledgeMessageTransportStepService(
            ConnectorMessageTransportStepRepository transportStepRepository,
            ConnectorMessageRepository messageRepository,
            ConnectorMessageEvidenceRepository evidenceRepository,
            ConnectorMessageErrorRepository messageErrorRepository) {
        this.transportStepRepository = transportStepRepository;
        this.messageRepository = messageRepository;
        this.evidenceRepository = evidenceRepository;
        this.messageErrorRepository = messageErrorRepository;
    }

    @Override
    public void execute(
            @NonNull String messageIdentifier,
            @NonNull UpdateMessageTransportCommand command) {
        if (!StringUtils.hasText(messageIdentifier)) {
            throw new IllegalArgumentException("Transport identifier must not be empty");
        }

        if (command.status() == ConnectorMessageTransportStatus.FAILED
                && (command.errors() == null || command.errors().isEmpty())) {
            throw new IllegalArgumentException("Errors must be provided for failed transport");
        }

        var existingStep = this.transportStepRepository.findByMessageIdentifier(messageIdentifier);

        if (existingStep == null) {
            throw new NotFoundException(
                    "No transport step found for identifier: " + messageIdentifier
            );
        }

        if (existingStep.status() != ConnectorMessageTransportStatus.DOWNLOADED) {
            throw new ConnectorMessageTransportStepException(
                    "Message with identifier [" + messageIdentifier + "] is already in "
                            + "transport status [" + command.status() + "]"
            );
        }

        var attempts = existingStep.numberOfAttempts() + 1;
        var transportStep = ConnectorMessageTransportStep.builder()
                                                         .status(command.status())
                                                         .numberOfAttempts(attempts)
                                                         .build();
        this.transportStepRepository.update(existingStep.identifier(), transportStep);

        if (command.status() == ConnectorMessageTransportStatus.SUBMITTED) {
            this.updateMessage(
                    existingStep.transportedMessage(),
                    command.remoteMessageIdentifier()
            );
        } else if (command.status() == ConnectorMessageTransportStatus.FAILED) {
            this.registerErrors(existingStep.transportedMessage(), command.errors());
        }
    }

    private void updateMessage(ConnectorMessage transportedMessage, String backendIdentifier) {
        if (transportedMessage.isBusinessMessage()) {
            var messageIdentifier = transportedMessage.identifier();

            var message = this.messageRepository.findByIdentifier(transportedMessage.identifier());

            if (message == null) {
                log.warn("Message [{}] not found", messageIdentifier);
                return;
            }

            this.messageRepository.setDeliveredToBackendAt(messageIdentifier);
            this.messageRepository.updateBackendIdentifier(messageIdentifier, backendIdentifier);
            log.info("Message [{}] has been delivered to backend", messageIdentifier);
        } else {
            var transportedEvidences = transportedMessage.transportedEvidences();

            if (transportedEvidences == null || transportedEvidences.isEmpty()) {
                throw new IllegalStateException(
                        "The evidence message contains no transported evidence"
                );
            }

            evidenceRepository.setDeliveredToLinkPartnerAt(
                    transportedEvidences.getFirst().uuid()
            );
        }
    }

    private void registerErrors(ConnectorMessage transportedMessage, List<ConnectorMessageError> errors) {
        if (transportedMessage.isBusinessMessage()) {
            this.messageErrorRepository.save(transportedMessage.identifier(), errors);
        }
    }
}
