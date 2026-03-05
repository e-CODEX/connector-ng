/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorServiceRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.persistence.entity.pmode.ConnectorServiceEntity;
import eu.ecodex.connector.infrastructure.outbound.persistence.repository.ConnectorProcessingModeJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.repository.ConnectorServiceJpaRepository;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorServiceRepository}.
 */
@Slf4j
@Component
public class ConnectorServiceRepositoryImpl implements ConnectorServiceRepository {
    private final ConnectorServiceJpaRepository jpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    /**
     * Constructs a new instance of {@code ConnectorServiceRepositoryImpl}.
     *
     * @param jpaRepository               the {@link ConnectorServiceJpaRepository} used to perform
     *                                    CRUD operations on ConnectorService entities.
     * @param processingModeJpaRepository the {@link ConnectorProcessingModeJpaRepository} used to
     *                                    perform CRUD operations on ConnectorProcessingMode
     *                                    entities and retrieve processing mode configurations.
     */
    public ConnectorServiceRepositoryImpl(
            ConnectorServiceJpaRepository jpaRepository,
            ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorService} and a {@link ConnectorProcessingModeEntity} into a
     * {@link ConnectorServiceEntity}.
     *
     * @param service        the {@link ConnectorService} to be converted
     * @param processingMode the {@link ConnectorProcessingModeEntity} to associate with the
     *                       resulting {@link ConnectorServiceEntity}
     *
     * @return a new {@link ConnectorServiceEntity} instance with values derived from the provided
     *         {@link ConnectorService} and {@link ConnectorProcessingModeEntity}
     */
    public static ConnectorServiceEntity toEntity(
            ConnectorService service, ConnectorProcessingModeEntity processingMode) {
        return ConnectorServiceEntity
                .builder()
                .name(service.name())
                .type(service.type())
                .processingMode(processingMode)
                .build();
    }

    /**
     * Converts a {@link ConnectorServiceEntity} instance to a {@link ConnectorService} domain
     * object.
     *
     * @param entity the {@link ConnectorServiceEntity} to be converted
     *
     * @return a new {@link ConnectorService} instance populated with data from the provided entity,
     *         or {@code null} if the input entity is {@code null}
     */
    public static ConnectorService toDomain(ConnectorServiceEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorService
                .builder()
                .name(entity.getName())
                .type(entity.getType())
                .build();
    }

    @Override
    public List<ConnectorService> saveAll(
            @NonNull List<ConnectorService> services,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug(
                "saving services [{}] for business domain [{}]", services,
                businessDomainIdentifier
        );

        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
                businessDomainIdentifier.messageLaneIdentifier()
        );

        var savedServices = this.jpaRepository.saveAll(
                services.stream().map(service -> toEntity(service, processingMode)).toList()
        );

        return savedServices.stream().map(ConnectorServiceRepositoryImpl::toDomain).toList();
    }

    @Override
    public ConnectorService findByNameAndBusinessDomain(
            @NonNull String name,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var service = this.jpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
                name, businessDomainIdentifier.messageLaneIdentifier()
        );

        return toDomain(service);
    }
}
