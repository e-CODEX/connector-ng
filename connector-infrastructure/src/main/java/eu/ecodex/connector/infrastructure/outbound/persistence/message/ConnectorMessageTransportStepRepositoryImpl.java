/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStepStatus;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepStatusEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.ConnectorMessageTransportStepJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.ConnectorMessageTransportStepStatusJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport.specification.TransportStepSpecification;
import eu.ecodex.connector.infrastructure.outbound.persistence.PaginationMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageTransportStepRepository}.
 */
@Slf4j
@Component
public class ConnectorMessageTransportStepRepositoryImpl
    implements ConnectorMessageTransportStepRepository {
    private final ConnectorMessageTransportStepJpaRepository transportStepJpaRepository;
    private final ConnectorMessageTransportStepStatusJpaRepository stepStatusJpaRepository;
    private final PaginationMapper paginationMapper;
    private final ObjectMapper objectMapper;

    /**
     * Constructs an instance of {@code ConnectorMessageTransportStepRepositoryImpl}.
     *
     * @param transportStepJpaRepository the repository for managing
     *                                   {@code ConnectorMessageTransportStepEntity} instances
     * @param stepStatusJpaRepository    the repository for managing
     *                                   {@code ConnectorMessageTransportStepStatusEntity}
     *                                   instances
     */
    public ConnectorMessageTransportStepRepositoryImpl(
        ConnectorMessageTransportStepJpaRepository transportStepJpaRepository,
        ConnectorMessageTransportStepStatusJpaRepository stepStatusJpaRepository,
        PaginationMapper paginationMapper,
        ObjectMapper objectMapper) {
        this.transportStepJpaRepository = transportStepJpaRepository;
        this.stepStatusJpaRepository = stepStatusJpaRepository;
        this.paginationMapper = paginationMapper;
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ConnectorMessageTransportStep save(
        @NonNull ConnectorMessageTransportStep transportStep) {
        try {
            var transportStepEntity = toEntity(transportStep);

            var stepStatusEntity = toEntity(transportStep.status());
            var savedTransportStep = transportStepJpaRepository.save(transportStepEntity);
            stepStatusEntity.setTransportStep(savedTransportStep);
            var savedStepStatus = stepStatusJpaRepository.save(stepStatusEntity);

            savedTransportStep.getStatuses().add(savedStepStatus);

            return toDomain(savedTransportStep);
        } catch (JsonProcessingException e) {
            log.error("Could not parse transported message", e);
            throw new IllegalArgumentException("Could not parse transported message", e);
        }
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
    public ConnectorMessageTransportStep findByMessageIdentifierOrRemoteSystemId(
        @NonNull String identifier) {
        var entity = this.transportStepJpaRepository
            .findByTransportedMessageIdentifierOrRemoteSystemIdentifier(identifier);

        return toDomain(entity);
    }

    @Override
    public ConnectorMessageTransportStep findByIdentifier(@NonNull String identifier) {
        var entity = this.transportStepJpaRepository.findByIdentifier(identifier);

        return toDomain(entity);
    }

    @Override
    public List<String> findPendingTransportSteps(@NonNull String backendName) {
        return transportStepJpaRepository.findAllPendingByBackendName(backendName);
    }

    @Override
    public List<String> findPendingMessagesIds(@NonNull String backendName) {
        return transportStepJpaRepository.findAllPendingMessageIdsByBackendName(backendName);
    }

    @Override
    public ConnectorPageResult<ConnectorMessageTransportStep> findAll(
        @NonNull ConnectorPageRequest request,
        String messageOrRemoteSystemIdentifier,
        String linkPartnerName) {
        var pageable = paginationMapper.toPageable(request);
        var specification = TransportStepSpecification.withFilters(
            messageOrRemoteSystemIdentifier,
            linkPartnerName
        );
        var transportSteps = transportStepJpaRepository.findAll(specification, pageable)
                                                       .map(this::toDomain);

        return paginationMapper.toPageResult(transportSteps);
    }

    private ConnectorMessageTransportStep toDomain(ConnectorMessageTransportStepEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            var transportedMessage = objectMapper.readValue(
                entity.getTransportedMessage(),
                ConnectorMessage.class
            );

            return ConnectorMessageTransportStep
                .builder()
                .identifier(entity.getIdentifier())
                .remoteSystemIdentifier(entity.getRemoteSystemIdentifier())
                .transportedMessageIdentifier(entity.getTransportedMessageIdentifier())
                .numberOfAttempts(entity.getNumberOfAttempts())
                .linkPartnerName(entity.getLinkPartnerName())
                .transportedMessage(transportedMessage)
                .status(entity.getStatus())
                .statuses(toStatuses(entity.getStatuses()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        } catch (JsonProcessingException e) {
            log.error("Could not parse transported message", e);
            return null;
        }
    }

    private ConnectorMessageTransportStepEntity toEntity(
        ConnectorMessageTransportStep transportStep) throws JsonProcessingException {
        var transportedMessage = transportStep.transportedMessage();
        return ConnectorMessageTransportStepEntity
            .builder()
            .identifier(transportStep.identifier())
            .numberOfAttempts(transportStep.numberOfAttempts())
            .linkPartnerName(transportStep.linkPartnerName())
            .transportedMessageIdentifier(transportStep.transportedMessage().identifier())
            .remoteSystemIdentifier(
                transportedMessage.direction()
                    == ConnectorMessageDirection.BACKEND_TO_GATEWAY
                    ? transportedMessage.as4Properties().ebmsMessageIdentifier()
                    : transportedMessage.backendMessageIdentifier()
            )
            .transportedMessage(
                objectMapper.writeValueAsString(transportStep.transportedMessage()))
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
