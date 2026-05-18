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

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.usecase.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.spi.ConnectorMessageTransportStepRepository;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRegisterMessageTransportStep} service.
 */
@Service
public class ConnectorRegisterMessageTransportStepService
        implements ConnectorRegisterMessageTransportStep {
    private static final int INITIAL_ATTEMPTS = 1;

    private final ConnectorMessageTransportStepRepository transportStepRepository;
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    public ConnectorRegisterMessageTransportStepService(
            ConnectorMessageTransportStepRepository transportStepRepository,
            ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.transportStepRepository = transportStepRepository;
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    @Override
    public ConnectorMessageTransportStep execute(
            @NonNull ConnectorMessage message,
            @NonNull ConnectorMessageTransportStatus status) {
        if (message.identifier() == null) {
            throw new IllegalArgumentException("Message identifier must not be null");
        }

        var existingStep = this.transportStepRepository.findByMessageIdentifier(
                message.identifier());

        int attempts = existingStep != null
                ? existingStep.numberOfAttempts() + 1
                : INITIAL_ATTEMPTS;

        var configuration = this.processingConfigurationProvider.getConfiguration();
        var identifier = String.format(
                "%s@%s_%s",
                UUID.randomUUID(),
                configuration.transportIdSuffix(),
                message.backendName()
        );

        var transportStep = ConnectorMessageTransportStep.builder()
                                                         .identifier(identifier)
                                                         .message(message)
                                                         .status(status)
                                                         .numberOfAttempts(attempts)
                                                         .build();

        return existingStep != null
                ? transportStepRepository.update(existingStep.identifier(), transportStep)
                : transportStepRepository.save(transportStep);
    }
}
