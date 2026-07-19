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
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorRegisterMessageTransportStep} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRegisterMessageTransportStepService
    implements ConnectorRegisterMessageTransportStep {
    private static final String TRANSPORT_ID_FORMAT = "%s@%s_%s";

    private static final Set<ConnectorMessageTransportStatus> ALLOWED_STATUSES = Set.of(
        ConnectorMessageTransportStatus.SUBMITTED,
        ConnectorMessageTransportStatus.DELIVERED,
        ConnectorMessageTransportStatus.FAILED
    );

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

        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                "Status must be one of [%s]".formatted(ALLOWED_STATUSES)
            );
        }

        log.debug(
            "Registering transport step for message [{}] with status [{}]",
            message.identifier(), status
        );

        var transportStep = ConnectorMessageTransportStep
            .builder()
            .identifier(buildIdentifier(message))
            .transportedMessage(message)
            .status(status)
            .numberOfAttempts(ALLOWED_STATUSES.contains(status) ? 1 : 0)
            .linkPartnerName(resolveLinkPartnerName(message))
            .build();

        return transportStepRepository.save(transportStep);
    }

    private String buildIdentifier(ConnectorMessage message) {
        var config = processingConfigurationProvider.getConfiguration();
        return TRANSPORT_ID_FORMAT.formatted(
            UUID.randomUUID(),
            config.transportIdSuffix(),
            resolveLinkPartnerName(message)
        );
    }

    private String resolveLinkPartnerName(ConnectorMessage message) {
        return message.direction() == ConnectorMessageDirection.BACKEND_TO_GATEWAY
            ? message.gatewayName()
            : message.backendName();
    }
}
