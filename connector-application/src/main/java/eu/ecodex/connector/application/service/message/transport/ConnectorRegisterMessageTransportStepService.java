/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.transport;

import eu.ecodex.connector.application.port.api.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.application.port.spi.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessageStatus;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ConnectorRegisterMessageTransportStep}.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRegisterMessageTransportStepService
    implements ConnectorRegisterMessageTransportStep {
    private static final String TRANSPORT_ID_FORMAT = "%s@%s_%s";

    private static final Set<ConnectorMessageTransportStatus> ALLOWED_STATUSES = EnumSet.of(
        ConnectorMessageTransportStatus.SUBMITTED,
        ConnectorMessageTransportStatus.DELIVERED,
        ConnectorMessageTransportStatus.FAILED,
        ConnectorMessageTransportStatus.READY_FOR_PULL
    );

    private final ConnectorMessageTransportStepRepository transportStepRepository;
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;
    private final ConnectorMessageRepository messageRepository;

    /**
     * Creates the service.
     *
     * @param transportStepRepository         the repository used to save the registered transport
     *                                        steps (must not be null)
     * @param processingConfigurationProvider the provider of the message processing configuration,
     *                                        which parameterizes how transport steps are registered
     *                                        (must not be null)
     * @param messageRepository               the repository used to read and update the message
     *                                        whose transport step is registered (must not be null)
     */
    public ConnectorRegisterMessageTransportStepService(
        ConnectorMessageTransportStepRepository transportStepRepository,
        ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider,
        ConnectorMessageRepository messageRepository) {
        this.transportStepRepository = transportStepRepository;
        this.processingConfigurationProvider = processingConfigurationProvider;
        this.messageRepository = messageRepository;
    }

    @Override
    public ConnectorMessageTransportStep execute(
        @NonNull ConnectorMessage message,
        @NonNull ConnectorMessageTransportStatus status) {
        validate(message, status);

        var transportStep =
            transportStepRepository.findByTransportedMessageIdentifier(message.identifier());

        if (transportStep != null && transportStep.isRetryable()) {
            return recordNewAttempt(message, status, transportStep);
        }

        return createTransportStep(message, status);
    }

    private void validate(ConnectorMessage message, ConnectorMessageTransportStatus status) {
        if (message.identifier() == null) {
            throw new IllegalArgumentException("Message identifier must not be null");
        }

        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                "Status must be one of %s".formatted(ALLOWED_STATUSES)
            );
        }
    }

    private ConnectorMessageTransportStep createTransportStep(
        ConnectorMessage message,
        ConnectorMessageTransportStatus status) {
        log.debug(
            "Registering transport step for message [{}] with status [{}]",
            message.identifier(), status
        );
        var linkPartnerName = resolveLinkPartnerName(message);

        updateMessageStatus(message, status);

        var transportStep = ConnectorMessageTransportStep
            .builder()
            .identifier(buildIdentifier(linkPartnerName))
            .transportedMessageIdentifier(message.identifier())
            .transportedMessage(withTransportStatus(message, status))
            .status(status)
            .numberOfAttempts(1)
            .linkPartnerName(linkPartnerName)
            .build();

        return transportStepRepository.save(transportStep);
    }

    private ConnectorMessageTransportStep recordNewAttempt(
        ConnectorMessage message,
        ConnectorMessageTransportStatus status,
        ConnectorMessageTransportStep transportStep) {
        log.debug(
            "Updating transport step [{}] for message [{}] by incrementing number of attempts",
            transportStep.identifier(), message.identifier()
        );

        var updated = transportStepRepository.update(
            transportStep.identifier(),
            status,
            transportStep.numberOfAttempts() + 1
        );

        // keep the business message in line with its transport step, as for a new step
        updateMessageStatus(message, status);

        return updated;
    }

    private String buildIdentifier(String linkPartnerName) {
        var config = processingConfigurationProvider.getConfiguration();
        return TRANSPORT_ID_FORMAT.formatted(
            UUID.randomUUID(),
            config.transportIdSuffix(),
            linkPartnerName
        );
    }

    private String resolveLinkPartnerName(ConnectorMessage message) {
        return message.direction() == ConnectorMessageDirection.BACKEND_TO_GATEWAY
            ? message.gatewayName()
            : message.backendName();
    }

    private void updateMessageStatus(
        ConnectorMessage message,
        ConnectorMessageTransportStatus status) {
        if (message instanceof ConnectorBusinessMessage) {
            messageRepository.updateStatus(
                message.identifier(),
                ConnectorBusinessMessageStatus.fromTransportStep(status.name())
            );
        }
    }

    private ConnectorMessage withTransportStatus(
        ConnectorMessage message,
        ConnectorMessageTransportStatus status) {
        if (message instanceof ConnectorBusinessMessage businessMessage) {
            return businessMessage
                .toBuilder()
                .status(ConnectorBusinessMessageStatus.fromTransportStep(status.name()))
                .build();
        }
        return message;
    }
}
