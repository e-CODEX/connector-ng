/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.pmode;

import eu.ecodex.connector.application.port.spi.pmode.ConnectorActionRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorActionEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorActionJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorProcessingModeJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorActionRepository}.
 */
@Component
public class ConnectorActionRepositoryImpl implements ConnectorActionRepository {
    private final ConnectorActionJpaRepository actionJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    public ConnectorActionRepositoryImpl(
        ConnectorActionJpaRepository actionJpaRepository,
        ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.actionJpaRepository = actionJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorAction} object and a {@link ConnectorProcessingModeEntity} into a
     * {@link ConnectorActionEntity} object.
     *
     * @param action         the action containing the details to be mapped to the entity
     * @param processingMode the processing mode entity associated with the action
     *
     * @return a new instance of {@link ConnectorActionEntity} populated with data from the provided
     *     arguments
     */
    public static ConnectorActionEntity toEntity(
        ConnectorAction action,
        ConnectorProcessingModeEntity processingMode) {
        return ConnectorActionEntity
            .builder()
            .name(action.name())
            .processingMode(processingMode)
            .build();
    }

    /**
     * Converts a {@link ConnectorActionEntity} to a {@link ConnectorAction}.
     *
     * @param entity the {@link ConnectorActionEntity} to be converted
     *
     * @return the corresponding {@link ConnectorAction} instance
     */
    public static ConnectorAction toDomain(ConnectorActionEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorAction
            .builder()
            .name(entity.getName())
            .build();
    }

    @Override
    public List<ConnectorAction> saveAll(
        @NonNull List<ConnectorAction> actions,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
            businessDomainIdentifier.messageLaneIdentifier()
        );
        var savedActions = this.actionJpaRepository.saveAll(
            actions.stream().map(action -> toEntity(action, processingMode)).toList()
        );

        return savedActions.stream().map(ConnectorActionRepositoryImpl::toDomain).toList();
    }

    @Override
    public ConnectorAction findByNameAndBusinessDomain(
        @NonNull String name,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var action = this.actionJpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
            name, businessDomainIdentifier.messageLaneIdentifier()
        );

        return toDomain(action);
    }

    @Override
    public List<ConnectorAction> findAllByBusinessDomainIdentifier(
        @NonNull ConnectorBusinessDomainIdentifier identifier) {
        var actions = this.actionJpaRepository.findByProcessingModeBusinessDomainIdentifier(
            identifier.messageLaneIdentifier());

        return actions.stream().map(ConnectorActionRepositoryImpl::toDomain).toList();
    }
}
