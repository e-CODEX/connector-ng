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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.spi.ConnectorBusinessDomainRepository;
import eu.ecodex.connector.infrastructure.database.entity.ConnectorBusinessDomainEntity;
import eu.ecodex.connector.infrastructure.database.repository.ConnectorBusinessDomainJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ConnectorBusinessDomainRepository}.
 */
@Component
public class ConnectorBusinessDomainRepositoryImpl implements ConnectorBusinessDomainRepository {
    private final ConnectorBusinessDomainJpaRepository jpaRepository;

    /**
     * Constructs a new instance of {@code ConnectorBusinessDomainRepositoryImpl} with the provided
     * JPA repository.
     *
     * @param jpaRepository the repository used for accessing and managing
     *                      {@link ConnectorBusinessDomainEntity} entities in the database; must not
     *                      be null.
     */
    public ConnectorBusinessDomainRepositoryImpl(
            ConnectorBusinessDomainJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConnectorBusinessDomain save(ConnectorBusinessDomain businessDomain) {
        var entity = ConnectorBusinessDomainEntity
                .builder()
                .identifier(businessDomain.identifier().messageLaneIdentifier())
                .enabled(businessDomain.enabled())
                .description(businessDomain.description())
                .source(businessDomain.source())
                .build();

        var savedEntity = jpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public ConnectorBusinessDomain findByIdentifier(ConnectorBusinessDomainIdentifier identifier) {
        var entity = jpaRepository.findByIdentifier(identifier.messageLaneIdentifier());

        return toDomain(entity);
    }

    private ConnectorBusinessDomain toDomain(ConnectorBusinessDomainEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorBusinessDomain
                .builder()
                .uuid(entity.getUuid())
                .description(entity.getDescription())
                .enabled(entity.isEnabled())
                .identifier(
                        ConnectorBusinessDomainIdentifier
                                .builder()
                                .messageLaneIdentifier(entity.getIdentifier()).build()
                )
                .source(entity.getSource())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
