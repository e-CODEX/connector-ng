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
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.user.ConnectorUserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorUserRepository} interface that provides
 * methods for managing ConnectorUser entities and their persistence using
 * a JPA-based repository.
 * <p>
 * This class acts as a bridge between the domain model representation of
 * ConnectorUser and the database representation, handling the required
 * conversions and delegation to the underlying JPA repository.
 * <p>
 * Responsibilities:
 * - Saving ConnectorUser entities into the database.
 * - Retrieving ConnectorUser entities by ID, username, email, or a combination of username and email.
 * - Listing all ConnectorUser entities.
 * - Deleting ConnectorUser entities by their ID or instance.
 * - Checking the existence of a ConnectorUser entity by its ID.
 * <p>
 * Utility Methods:
 * - Converts domain model objects to JPA entity objects for database persistence.
 * - Converts JPA entity objects back to domain model objects for application consumption.
 * - Handles conversion of roles associated with a ConnectorUser.
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRepositoryImpl implements ConnectorUserRepository {

    ConnectorUserJpaRepository jpaRepository;

    @Override
    public ConnectorUser save(ConnectorUser domainUser) {

        var existing = jpaRepository.findByUuid(
                domainUser.uuid()); // TODO check if this call could be optimized
        ConnectorUserEntity entity = null;
        if (existing.isPresent()) {
            entity = existing.get();
            updateEntity(domainUser, entity);
        } else {
            entity = toEntity(domainUser);
        }

        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ConnectorUser> findById(Long id) {
        var found = jpaRepository.findById(id);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUuId(String identifier) {
        return jpaRepository.findByUuid(identifier).map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsername(String username) {
        var found = jpaRepository.findByUsername(username);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByEmail(String email) {
        var found = jpaRepository.findByEmail(email);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsernameAndEmail(String username, String email) {
        var found = jpaRepository.findByUsernameAndEmail(username, email);
        return found.map(this::toDomain);
    }

    @Override
    public List<ConnectorUser> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteByUuid(String identifier) {
        var entity = jpaRepository.findByUuid(identifier)
                .orElseThrow(() ->
                        new ConnectorUserNotFoundException("No user found by identifier " + identifier));
        jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return jpaRepository.existsByUuid(uuid);
    }


    private ConnectorUserEntity toEntity(ConnectorUser domainUser) {
        return ConnectorUserEntity.builder()
                .uuid(domainUser.uuid())
                .username(domainUser.username())
                .password(domainUser.password())
                .email(domainUser.email())
                .enabled(domainUser.enabled())
                .build();
    }

    private ConnectorUser toDomain(ConnectorUserEntity entity) {
        return ConnectorUser.builder()
                .uuid(entity.getUuid())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .roles(getUserRoles(entity))
                .build();
    }

    private void updateEntity(ConnectorUser domainUser, ConnectorUserEntity entity) {
        entity.setUuid(domainUser.uuid());
        entity.setPassword(domainUser.password());
        entity.setEnabled(domainUser.enabled());
        entity.setUsername(domainUser.username());
        entity.setEmail(domainUser.email());
    }

    private static Set<ConnectorUserRole> getUserRoles(ConnectorUserEntity entity) {
        return entity.getRoles() == null ? null :
                entity.getRoles().stream()
                        .map(getUserRoleFunction())
                        .collect(Collectors.toUnmodifiableSet());
    }

    private static Function<ConnectorUserRoleEntity, ConnectorUserRole> getUserRoleFunction() {
        return role ->
                ConnectorUserRole.builder()
                        .uuid(role.getUuid())
                        .name(role.getName())
                        .build();
    }
}
