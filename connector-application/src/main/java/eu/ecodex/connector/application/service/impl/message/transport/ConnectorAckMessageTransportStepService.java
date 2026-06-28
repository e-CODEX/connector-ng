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

import eu.ecodex.connector.application.service.usecase.transport.ConnectorAckMessageTransportStep;
import eu.ecodex.connector.application.service.usecase.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageErrorRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link ConnectorAckMessageTransportStep} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorAckMessageTransportStepService implements ConnectorAckMessageTransportStep {
    // TODO exclude FAILED status from this set when retrying is implemented
    private static final Set<ConnectorMessageTransportStatus> TERMINAL_STATUSES = Set.of(
            ConnectorMessageTransportStatus.FAILED,
            ConnectorMessageTransportStatus.DELIVERED
    );

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
     * @param evidenceRepository      the repository used for managing connector message evidences
     * @param messageErrorRepository  the repository used for managing connector message errors
     */
    public ConnectorAckMessageTransportStepService(
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
            @NonNull String messageOrRemoteSystemIdentifier,
            @NonNull UpdateMessageTransportCommand command) {
        validateRequest(messageOrRemoteSystemIdentifier, command);

        var existingStep = this.transportStepRepository.findByMessageIdentifierOrRemoteSystemId(
                messageOrRemoteSystemIdentifier);

        if (existingStep == null) {
            throw new ConnectorMessageTransportStepNotFoundException(
                    "No transport step found for identifier [%s]"
                            .formatted(messageOrRemoteSystemIdentifier)
            );
        }

        if (TERMINAL_STATUSES.contains(existingStep.status())) {
            log.warn(
                    "Message [{}] is already in terminal transport status [{}]",
                    messageOrRemoteSystemIdentifier, existingStep.status()
            );

            return;
        }

        updateTransportStep(existingStep, command);
        handlePostUpdate(existingStep, command);
    }

    private void validateRequest(
            String messageOrRemoteSystemIdentifier,
            UpdateMessageTransportCommand command) {
        if (!StringUtils.hasText(messageOrRemoteSystemIdentifier)) {
            throw new IllegalArgumentException(
                    "Transport message or remote system identifier must not be empty");
        }

        if (command.status() == ConnectorMessageTransportStatus.FAILED
                && (command.errors() == null || command.errors().isEmpty())) {
            throw new IllegalArgumentException("Errors must be provided for failed transport");
        }

        if (command.status() == ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD) {
            throw new IllegalArgumentException(
                    "Ready for download status cannot be apply to an existing transport step");
        }
    }

    private void updateTransportStep(
            ConnectorMessageTransportStep existingStep,
            UpdateMessageTransportCommand command) {

        var update = ConnectorMessageTransportStep.builder()
                                                  .status(command.status())
                                                  .numberOfAttempts(existingStep.numberOfAttempts())
                                                  .build();

        transportStepRepository.update(existingStep.identifier(), update);
    }

    private void handlePostUpdate(
            ConnectorMessageTransportStep existingStep,
            UpdateMessageTransportCommand command) {

        switch (command.status()) {
            case DELIVERED -> updateMessage(
                    existingStep.transportedMessage(),
                    command.remoteMessageIdentifier()
            );
            case FAILED -> registerErrors(existingStep.transportedMessage(), command.errors());
            default -> {
                log.debug("No action required for transport status [{}]", command.status());
            }
        }
    }


    private void updateMessage(
            ConnectorMessage transportedMessage,
            String remoteMessageIdentifier) {
        var identifier = transportedMessage.identifier();

        if (identifier == null) {
            throw new IllegalStateException(
                    "The message identifier is not set for the transported message"
            );
        }

        if (transportedMessage.isBusinessMessage()) {
            updateBusinessMessage(transportedMessage, identifier, remoteMessageIdentifier);
        } else {
            updateEvidenceMessage(transportedMessage, identifier);
        }
    }

    private void updateBusinessMessage(
            ConnectorMessage message,
            String identifier,
            String remoteMessageIdentifier) {
        if (messageRepository.findByIdentifier(identifier) == null) {
            log.warn("Message [{}] not found", identifier);
            return;
        }

        if (message.direction() == ConnectorMessageDirection.GATEWAY_TO_BACKEND) {
            messageRepository.setDeliveredToBackendAt(identifier);
            messageRepository.updateBackendIdentifier(identifier, remoteMessageIdentifier);
            log.info("Message [{}] delivered to backend link partner", identifier);
        } else {
            // TODO see if ebms ID should be updated
            messageRepository.setDeliveredToGatewayAt(identifier);
            log.info("Message [{}] delivered to gateway link partner", identifier);
        }
    }

    private void updateEvidenceMessage(ConnectorMessage message, String identifier) {
        var evidences = message.transportedEvidences();

        if (evidences == null || evidences.isEmpty()) {
            throw new IllegalStateException(
                    "Evidence message [%s] contains no transported evidence".formatted(identifier));
        }

        evidenceRepository.setDeliveredToLinkPartnerAt(evidences.getFirst().uuid());
    }

    private void registerErrors(
            ConnectorMessage transportedMessage,
            List<ConnectorMessageError> errors) {
        // TODO for evidence messages, the errors should be registered to the step
        if (transportedMessage.isBusinessMessage()) {
            this.messageErrorRepository.save(transportedMessage.identifier(), errors);
        }
    }
}
