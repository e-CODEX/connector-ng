/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.pmode;

import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeActionRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeAction;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeActionEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModeActionJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModeJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * RDBMS implementation of {@link ConnectorProcessingModeActionRepository}.
 */
@Component
public class ConnectorDBProcessingModeActionRepository
    implements ConnectorProcessingModeActionRepository {
    private final ConnectorProcessingModeActionJpaRepository actionJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    public ConnectorDBProcessingModeActionRepository(
        ConnectorProcessingModeActionJpaRepository actionJpaRepository,
        ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.actionJpaRepository = actionJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorProcessingModeAction} object and a
     * {@link ConnectorProcessingModeEntity} into a {@link ConnectorProcessingModeActionEntity}
     * object.
     *
     * @param action         the action containing the details to be mapped to the entity
     * @param processingMode the processing mode entity associated with the action
     *
     * @return a new instance of {@link ConnectorProcessingModeActionEntity} populated with data
     *     from the provided arguments
     */
    public static ConnectorProcessingModeActionEntity toEntity(
        ConnectorProcessingModeAction action,
        ConnectorProcessingModeEntity processingMode) {
        return ConnectorProcessingModeActionEntity
            .builder()
            .name(action.name())
            .processingMode(processingMode)
            .build();
    }

    /**
     * Converts a {@link ConnectorProcessingModeActionEntity} to a
     * {@link ConnectorProcessingModeAction}.
     *
     * @param entity the {@link ConnectorProcessingModeActionEntity} to be converted
     *
     * @return the corresponding {@link ConnectorProcessingModeAction} instance
     */
    public static ConnectorProcessingModeAction toDomain(
        ConnectorProcessingModeActionEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorProcessingModeAction
            .builder()
            .name(entity.getName())
            .build();
    }

    @Override
    public List<ConnectorProcessingModeAction> saveAll(
        @NonNull List<ConnectorProcessingModeAction> actions,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
            businessDomainIdentifier.messageLaneIdentifier()
        );
        var savedActions = this.actionJpaRepository.saveAll(
            actions.stream().map(action -> toEntity(action, processingMode)).toList()
        );

        return savedActions.stream()
                           .map(ConnectorDBProcessingModeActionRepository::toDomain)
                           .toList();
    }

    @Override
    public ConnectorProcessingModeAction findByNameAndBusinessDomain(
        @NonNull String name,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var action = this.actionJpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
            name, businessDomainIdentifier.messageLaneIdentifier()
        );

        return toDomain(action);
    }

    @Override
    public List<ConnectorProcessingModeAction> findAllByBusinessDomainIdentifier(
        @NonNull ConnectorBusinessDomainIdentifier identifier) {
        var actions = this.actionJpaRepository.findByProcessingModeBusinessDomainIdentifier(
            identifier.messageLaneIdentifier(),
            Sort.by(Sort.Order.asc("name"))
        );

        return actions.stream().map(ConnectorDBProcessingModeActionRepository::toDomain).toList();
    }
}
