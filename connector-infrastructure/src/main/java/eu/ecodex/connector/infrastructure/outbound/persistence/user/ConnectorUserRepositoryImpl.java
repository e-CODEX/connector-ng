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
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserRefreshTokenJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserRoleJpaRepository;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of the ConnectorUserRepository interface that provides
 * operations for managing ConnectorUser entities in the database.
 *
 * <p>This class uses JPA repositories for persistence and mapping
 * entities to domain objects and vice versa. It ensures consistency
 * between the domain and persistence layers and includes functionality
 * for saving, retrieving, updating, and deleting ConnectorUser entities.
 */
@Slf4j
@Service
public class ConnectorUserRepositoryImpl implements ConnectorUserRepository {
    private final ConnectorUserJpaRepository jpaRepository;
    private final ConnectorUserRoleJpaRepository roleRepository;
    private final ConnectorUserRefreshTokenJpaRepository refreshTokenRepository;

    /**
     * Constructs an instance of ConnectorUserRepositoryImpl.
     *
     * @param jpaRepository          the repository for managing ConnectorUser entities
     * @param roleRepository         the repository for managing ConnectorRole entities
     * @param refreshTokenRepository the repository for managing ConnectorRefreshToken entities
     */
    public ConnectorUserRepositoryImpl(ConnectorUserJpaRepository jpaRepository,
                                       ConnectorUserRoleJpaRepository roleRepository,
                                       ConnectorUserRefreshTokenJpaRepository
                                           refreshTokenRepository) {
        this.jpaRepository = jpaRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public ConnectorUser save(@NonNull ConnectorUser domainUser) {
        var existing = jpaRepository.findByUuid(
            domainUser.uuid()); // TODO check if this call could be optimized

        ConnectorUserEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            toEntity(entity, domainUser);
        } else {
            entity = toEntity(domainUser);
        }
        var saved = jpaRepository.save(entity);
        return ConnectorUserMapper.toDomain(saved);
    }

    @Override
    public Optional<ConnectorUser> findByUuid(@NonNull String identifier) {
        return jpaRepository.findByUuid(identifier).map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsername(@NonNull String username) {
        var found = jpaRepository.findByUsername(username);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsernameAndActive(@NonNull String username,
                                                           boolean active) {
        var found = jpaRepository.findByUsernameAndEnabled(username, active);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public List<ConnectorUser> findAllUsers() {
        return jpaRepository
            .findAll()
            .stream()
            .map(ConnectorUserMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByUuid(@Nonnull String identifier) {
        var entity = jpaRepository.findByUuid(identifier).orElseThrow(
            () -> new ConnectorUserNotFoundException("No user found by identifier " + identifier));
        refreshTokenRepository.deleteByUser_Uuid(entity.getUuid());
        jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByUuid(@Nonnull String uuid) {
        return jpaRepository.existsByUuid(uuid);
    }

    @Override
    public boolean existsByUsername(@Nonnull String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(@Nonnull String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndUuidNot(@NonNull String email, @NonNull String identifier) {
        return jpaRepository.existsByEmailAndUuidNot(email, identifier);
    }

    @Override
    public boolean existsByUsernameAndUuidNot(@Nonnull String username,
                                              @Nonnull String identifier) {
        return jpaRepository.existsByUsernameAndUuidNot(username, identifier);
    }


    /**
     * Converts a domain user model into a JPA entity representation.
     *
     * @param domainUser the domain-level user object to be converted
     *
     * @return a {@code ConnectorUserEntity} object representing the JPA entity
     */
    public ConnectorUserEntity toEntity(ConnectorUser domainUser) {
        return ConnectorUserEntity
            .builder()
            .uuid(domainUser.uuid())
            .username(domainUser.username())
            .password(domainUser.password())
            .email(domainUser.email())
            .enabled(domainUser.enabled())
            .roles(toUserRoles(domainUser.roles()))
            .build();
    }


    /**
     * Converts a domain user model into a JPA entity representation.
     *
     * @param entity     the JPA entity to be updated
     * @param domainUser the domain-level user object to be converted
     */
    private void toEntity(ConnectorUserEntity entity, ConnectorUser domainUser) {
        entity.setUuid(domainUser.uuid());
        entity.setPassword(domainUser.password());
        entity.setEnabled(domainUser.enabled());
        entity.setUsername(domainUser.username());
        entity.setEmail(domainUser.email());
        entity.setRoles(toUserRoles(domainUser.roles()));
    }

    /**
     * Converts a set of domain-level user roles into a set of JPA role entities.
     *
     * @param domainUserRoles the set of {@code ConnectorRole} objects representing the domain user
     *                        roles to be converted;
     *                        can be {@code null}.
     *
     * @return a {@code Set} of {@code ConnectorRoleEntity} representing the JPA role entities, or
     *     {@code null}
     *     if the input is {@code null}.
     */
    private Set<ConnectorRoleEntity> toUserRoles(Set<ConnectorRole> domainUserRoles) {
        if (domainUserRoles == null) {
            return null;
        }
        var rolesNames = domainUserRoles.stream()
            .map(ConnectorRole::name)
            .collect(Collectors.toUnmodifiableSet());

        return roleRepository.findByNameIn(rolesNames);
    }
}
