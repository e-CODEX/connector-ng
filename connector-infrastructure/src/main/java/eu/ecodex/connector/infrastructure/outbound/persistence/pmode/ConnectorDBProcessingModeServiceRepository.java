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

import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModeServiceRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeService;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorProcessingModeServiceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorProcessingModeJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorProcessingModeServiceJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * RDBMS implementation of {@link ConnectorProcessingModeServiceRepository}.
 */
@Component
public class ConnectorDBProcessingModeServiceRepository
    implements ConnectorProcessingModeServiceRepository {
    private final ConnectorProcessingModeServiceJpaRepository serviceJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    /**
     * Constructs a new instance of {@code ConnectorServiceRepositoryImpl}.
     *
     * @param serviceJpaRepository        the {@link ConnectorProcessingModeServiceJpaRepository}
     *                                    used to perform CRUD operations on ConnectorService
     *                                    entities.
     * @param processingModeJpaRepository the {@link ConnectorProcessingModeJpaRepository} used to
     *                                    perform CRUD operations on ConnectorProcessingMode
     *                                    entities and retrieve processing mode configurations.
     */
    public ConnectorDBProcessingModeServiceRepository(
        ConnectorProcessingModeServiceJpaRepository serviceJpaRepository,
        ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.serviceJpaRepository = serviceJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorProcessingModeService} and a {@link ConnectorProcessingModeEntity}
     * into a {@link ConnectorProcessingModeServiceEntity}.
     *
     * @param service        the {@link ConnectorProcessingModeService} to be converted
     * @param processingMode the {@link ConnectorProcessingModeEntity} to associate with the
     *                       resulting {@link ConnectorProcessingModeServiceEntity}
     *
     * @return a new {@link ConnectorProcessingModeServiceEntity} instance with values derived from
     *     the provided {@link ConnectorProcessingModeService} and
     *     {@link ConnectorProcessingModeEntity}
     */
    public static ConnectorProcessingModeServiceEntity toEntity(
        ConnectorProcessingModeService service,
        ConnectorProcessingModeEntity processingMode) {
        return ConnectorProcessingModeServiceEntity
            .builder()
            .name(service.name())
            .type(service.type())
            .processingMode(processingMode)
            .build();
    }

    /**
     * Converts a {@link ConnectorProcessingModeServiceEntity} instance to a
     * {@link ConnectorProcessingModeService} domain object.
     *
     * @param entity the {@link ConnectorProcessingModeServiceEntity} to be converted
     *
     * @return a new {@link ConnectorProcessingModeService} instance populated with data from the
     *     provided entity, or {@code null} if the input entity is {@code null}
     */
    public static ConnectorProcessingModeService toDomain(
        ConnectorProcessingModeServiceEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorProcessingModeService
            .builder()
            .name(entity.getName())
            .type(entity.getType())
            .build();
    }

    @Override
    public List<ConnectorProcessingModeService> saveAll(
        @NonNull List<ConnectorProcessingModeService> services,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
            businessDomainIdentifier.messageLaneIdentifier()
        );

        var savedServices = this.serviceJpaRepository.saveAll(
            services.stream().map(service -> toEntity(service, processingMode)).toList()
        );

        return savedServices.stream()
                            .map(ConnectorDBProcessingModeServiceRepository::toDomain)
                            .toList();
    }

    @Override
    public ConnectorProcessingModeService findByNameAndBusinessDomain(
        @NonNull String name,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var service = this.serviceJpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
            name, businessDomainIdentifier.messageLaneIdentifier()
        );

        return toDomain(service);
    }

    @Override
    public List<ConnectorProcessingModeService> findAllByBusinessDomainIdentifier(
        @NonNull ConnectorBusinessDomainIdentifier identifier) {
        var services = this.serviceJpaRepository.findByProcessingModeBusinessDomainIdentifier(
            identifier.messageLaneIdentifier(),
            Sort.by(Sort.Order.asc("name"))
        );

        return services.stream().map(ConnectorDBProcessingModeServiceRepository::toDomain).toList();
    }
}
