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

import eu.ecodex.connector.application.port.spi.message.ConnectorMessageErrorRepository;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageErrorEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageErrorJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ConnectorMessageErrorRepository}.
 */
@Component
public class ConnectorMessageErrorRepositoryImpl implements ConnectorMessageErrorRepository {
    private final ConnectorMessageErrorJpaRepository errorJpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;

    public ConnectorMessageErrorRepositoryImpl(
        ConnectorMessageErrorJpaRepository errorJpaRepository,
        ConnectorMessageJpaRepository messageJpaRepository) {
        this.errorJpaRepository = errorJpaRepository;
        this.messageJpaRepository = messageJpaRepository;
    }

    /**
     * Converts a {@link ConnectorMessageErrorEntity} instance to a {@link ConnectorMessageError}
     * domain object.
     *
     * @param entity The {@link ConnectorMessageErrorEntity} to be converted. Must not be null and
     *               should contain valid error information.
     *
     * @return A {@link ConnectorMessageError} object built from the provided entity. This includes
     *     the label, details, source, and timestamps (createdAt, updatedAt) extracted from the
     *     entity.
     */
    public static ConnectorMessageError toDomain(ConnectorMessageErrorEntity entity) {
        return ConnectorMessageError.builder()
                                    .label(entity.getLabel())
                                    .details(entity.getDetails())
                                    .source(entity.getSource())
                                    .createdAt(entity.getCreatedAt())
                                    .updatedAt(entity.getUpdatedAt())
                                    .build();
    }

    @Override
    public List<ConnectorMessageError> save(
        @NonNull String messageIdentifier,
        @NonNull List<ConnectorMessageError> errors) {
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors must not be empty");
        }

        var message = messageJpaRepository.findByIdentifier(messageIdentifier);
        var entities = toEntities(errors, message);
        var savedErrors = errorJpaRepository.saveAll(entities);

        return savedErrors.stream().map(ConnectorMessageErrorRepositoryImpl::toDomain).toList();
    }

    private ConnectorMessageErrorEntity toEntity(ConnectorMessageError error) {
        return ConnectorMessageErrorEntity.builder()
                                          .label(error.label())
                                          .details(error.details())
                                          .source(error.source())
                                          .build();
    }

    private List<ConnectorMessageErrorEntity> toEntities(
        List<ConnectorMessageError> errors,
        ConnectorMessageEntity message) {
        return errors.stream().map(error -> {
            var entity = toEntity(error);
            entity.setMessage(message);

            return entity;
        }).toList();
    }
}
