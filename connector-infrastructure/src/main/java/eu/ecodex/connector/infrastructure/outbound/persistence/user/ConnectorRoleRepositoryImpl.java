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

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserRoleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository to handle user's roles.
 */
@Slf4j
@Service
public class ConnectorRoleRepositoryImpl implements ConnectorRoleRepository {
    private final ConnectorUserRoleJpaRepository jpaRepository;

    public ConnectorRoleRepositoryImpl(ConnectorUserRoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConnectorRole save(@NonNull ConnectorRole userRole) {
        var existing =
            jpaRepository.findByUuid(userRole.uuid()); // TODO check if this call could be optimized
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
            new ConnectorUserNotFoundException("No user role found with id " + identifier));

        if (!CollectionUtils.isEmpty(entity.getUsers())) {
            entity.getUsers().forEach(user -> user.removeRole(entity));
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
    private ConnectorRoleEntity toEntity(ConnectorRole domainUserRole) {
        return ConnectorRoleEntity
            .builder()
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
    private ConnectorRole toDomain(ConnectorRoleEntity entity) {
        return new ConnectorRole(entity.getUuid(), entity.getName(), entity.getCreatedAt(),
            entity.getUpdatedAt());
    }
}
