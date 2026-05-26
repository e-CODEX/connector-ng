/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.message;

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStepStatus;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepStatusEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.ConnectorMessageTransportStepJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.ConnectorMessageTransportStepStatusJpaRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageTransportStepRepository}.
 */
@Component
public class ConnectorMessageTransportStepRepositoryImpl
        implements ConnectorMessageTransportStepRepository {
    private final ConnectorMessageTransportStepJpaRepository transportStepJpaRepository;
    private final ConnectorMessageTransportStepStatusJpaRepository stepStatusJpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;

    /**
     * Constructs an instance of {@code ConnectorMessageTransportStepRepositoryImpl}.
     *
     * @param transportStepJpaRepository the repository for managing
     *                                   {@code ConnectorMessageTransportStepEntity} instances
     * @param stepStatusJpaRepository    the repository for managing
     *                                   {@code ConnectorMessageTransportStepStatusEntity}
     *                                   instances
     * @param messageJpaRepository       the repository for managing {@code ConnectorMessageEntity}
     *                                   instances
     */
    public ConnectorMessageTransportStepRepositoryImpl(
            ConnectorMessageTransportStepJpaRepository transportStepJpaRepository,
            ConnectorMessageTransportStepStatusJpaRepository stepStatusJpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository) {
        this.transportStepJpaRepository = transportStepJpaRepository;
        this.stepStatusJpaRepository = stepStatusJpaRepository;
        this.messageJpaRepository = messageJpaRepository;
    }

    @Override
    public ConnectorMessageTransportStep save(
            @NonNull ConnectorMessageTransportStep transportStep) {
        var messageEntity = this.messageJpaRepository.findByIdentifier(transportStep.message()
                                                                                    .identifier());

        if (messageEntity == null) {
            throw new IllegalArgumentException(
                    "No message found for identifier: " + transportStep.message().identifier());
        }

        var transportStepEntity = toEntity(transportStep);
        transportStepEntity.setMessage(messageEntity);

        var stepStatusEntity = toEntity(transportStep.status());
        var savedTransportStep = transportStepJpaRepository.save(transportStepEntity);
        stepStatusEntity.setTransportStep(savedTransportStep);
        var savedStepStatus = stepStatusJpaRepository.save(stepStatusEntity);

        savedTransportStep.getStatuses().add(savedStepStatus);

        return toDomain(savedTransportStep);
    }

    @Override
    public ConnectorMessageTransportStep update(
            @NonNull String identifier,
            @NonNull ConnectorMessageTransportStep transportStep) {
        var entity = transportStepJpaRepository.findByIdentifier(identifier);

        if (entity == null) {
            throw new IllegalArgumentException(
                    "No transport step found for identifier: " + identifier
            );
        }

        var updatedEntity = updateEntity(entity, transportStep);
        var updated = this.transportStepJpaRepository.save(updatedEntity);

        var stepStatusEntity = toEntity(transportStep.status());
        stepStatusEntity.setTransportStep(updated);
        var savedStepStatusEntity = this.stepStatusJpaRepository.save(stepStatusEntity);

        updated.getStatuses().add(savedStepStatusEntity);

        return toDomain(updated);
    }

    @Override
    public void updateStatus(
            @NonNull List<String> identifiers,
            @NonNull ConnectorMessageTransportStatus status) {
        this.transportStepJpaRepository.updateStatus(identifiers, status.name());

        this.stepStatusJpaRepository.insert(identifiers, status.name());
    }

    @Override
    public ConnectorMessageTransportStep findByMessageIdentifier(
            @NonNull String messageIdentifier) {
        var entity = this.transportStepJpaRepository.findByMessageIdentifier(messageIdentifier);

        return toDomain(entity);
    }

    @Override
    public ConnectorMessageTransportStep findByIdentifier(@NonNull String identifier) {
        var entity = this.transportStepJpaRepository.findByIdentifier(identifier);

        return toDomain(entity);
    }

    @Override
    public List<String> findPendingTransportSteps(@NonNull String backendName) {
        return transportStepJpaRepository.findAllPendingByMessageBackendName(backendName);
    }

    @Override
    public List<String> findPendingMessagesIds(@NonNull String backendName) {
        return transportStepJpaRepository.findAllPendingMessageIdsByBackendName(backendName);
    }

    private ConnectorMessageTransportStep toDomain(ConnectorMessageTransportStepEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessageTransportStep.builder()
                                            .identifier(entity.getIdentifier())
                                            .numberOfAttempts(entity.getNumberOfAttempts())
                                            .status(entity.getStatus())
                                            .message(ConnectorMessageRepositoryImpl.toShortDomain(
                                                    entity.getMessage()))
                                            .statuses(toStatuses(entity.getStatuses()))
                                            .createdAt(entity.getCreatedAt())
                                            .updatedAt(entity.getUpdatedAt())
                                            .build();
    }

    private ConnectorMessageTransportStepEntity toEntity(
            ConnectorMessageTransportStep transportStep) {
        return ConnectorMessageTransportStepEntity
                .builder()
                .identifier(transportStep.identifier())
                .numberOfAttempts(transportStep.numberOfAttempts())
                .status(transportStep.status())
                .build();
    }

    private ConnectorMessageTransportStepStatusEntity toEntity(
            ConnectorMessageTransportStatus status) {
        return ConnectorMessageTransportStepStatusEntity.builder().status(status).build();
    }

    ConnectorMessageTransportStepEntity updateEntity(
            ConnectorMessageTransportStepEntity entity,
            ConnectorMessageTransportStep transportStep) {
        return entity.toBuilder()
                     .numberOfAttempts(transportStep.numberOfAttempts())
                     .status(transportStep.status())
                     .build();
    }

    private Set<ConnectorMessageTransportStepStatus> toStatuses(
            Set<ConnectorMessageTransportStepStatusEntity> statuses) {
        return statuses.stream()
                       .map(status -> ConnectorMessageTransportStepStatus
                               .builder()
                               .status(status.getStatus())
                               .createdAt(status.getCreatedAt())
                               .build())
                       .collect(Collectors.toSet());
    }
}
