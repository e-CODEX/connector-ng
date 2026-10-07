/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.user;

import eu.ecodex.connector.application.exception.role.ConnectorRoleInUseException;
import eu.ecodex.connector.application.exception.role.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserRoleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository to handle user's roles.
 */
@Slf4j
@Service
public class ConnectorDBRoleRepository implements ConnectorRoleRepository {
    private final ConnectorUserRoleJpaRepository jpaRepository;
    private final ConnectorUserJpaRepository userJpaRepository;

    public ConnectorDBRoleRepository(ConnectorUserRoleJpaRepository jpaRepository,
                                     ConnectorUserJpaRepository userJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public ConnectorRole save(@NonNull ConnectorRole userRole) {
        // TODO check if this call could be optimized
        var existing = jpaRepository.findByUuid(userRole.uuid());
        ConnectorRoleEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setName(userRole.name());
            entity.setUuid(userRole.uuid());
        } else {
            entity = toEntity(userRole);
        }
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ConnectorRole> findByUuid(@NonNull String identifier) {
        var found = jpaRepository.findByUuid(identifier);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorRole> findByName(@NonNull String name) {
        var found = jpaRepository.findByName(name);
        return found.map(this::toDomain);
    }

    @Override
    public List<ConnectorRole> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByUuid(@NonNull String identifier) {
        var entity = jpaRepository.findByUuid(identifier).orElseThrow(() ->
            new ConnectorRoleNotFoundException("No user role found with id " + identifier));

        if (userJpaRepository.existsByRolesId(entity.getId())) {
            throw new ConnectorRoleInUseException("User role is currently in use");
        }
        jpaRepository.delete(entity);
    }

    @Override
    public Set<ConnectorRole> findByNameIn(@NonNull Set<String> names) {
        return jpaRepository
            .findByNameIn(names)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toUnmodifiableSet());
    }


    /**
     * Converts a {@link ConnectorRole} domain object into a {@link ConnectorRoleEntity}.
     *
     * @param domainUserRole the domain object representing a connector role to be converted
     *
     * @return a new {@link ConnectorRoleEntity} object based on the provided domain object
     */
    private ConnectorRoleEntity toEntity(@NonNull ConnectorRole domainUserRole) {
        return ConnectorRoleEntity.builder()
            .uuid(domainUserRole.uuid())
            .name(domainUserRole.name())
            .build();
    }

    /**
     * Converts a {@link ConnectorRoleEntity} persistence entity into a {@link ConnectorRole} domain
     * model.
     *
     * @param entity the persistence entity representing a connector role
     *
     * @return a new {@link ConnectorRole} object based on the provided persistence entity
     */
    private ConnectorRole toDomain(@NonNull ConnectorRoleEntity entity) {
        return ConnectorRole.builder()
            .uuid(entity.getUuid())
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
