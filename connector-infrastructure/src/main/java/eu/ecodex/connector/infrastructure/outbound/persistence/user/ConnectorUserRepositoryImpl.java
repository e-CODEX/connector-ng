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
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorRoleJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of the ConnectorUserRepository interface that provides
 * operations for managing ConnectorUser entities in the database.
 *
 * <p>
 * This class uses JPA repositories for persistence and mapping
 * entities to domain objects and vice versa. It ensures consistency
 * between the domain and persistence layers and includes functionality
 * for saving, retrieving, updating, and deleting ConnectorUser entities.
 *
 * <p>
 * Annotations Used:
 * - {@code @Slf4j}: Enables logging.
 * - {@code @Service}: Indicates that this class is a Spring service component.
 * - {@code @RequiredArgsConstructor}: Generates a constructor with required
 * arguments for dependencies marked as {@code final}.
 * - {@code @FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)}: Sets
 * all fields to private final.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRepositoryImpl implements ConnectorUserRepository {

    ConnectorUserJpaRepository jpaRepository;
    ConnectorRoleJpaRepository roleRepository;

    @Override
    public ConnectorUser save(ConnectorUser domainUser) {

        var existing = jpaRepository.findByUuid(
                domainUser.uuid()); // TODO check if this call could be optimized
        ConnectorUserEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            updateEntity(domainUser, entity);
        } else {
            entity = ConnectorUserMapper.toEntity(domainUser);
        }

        updateUserRoles(domainUser, entity);

        var saved = jpaRepository.save(entity);
        return ConnectorUserMapper.toDomain(saved);
    }


    @Override
    public Optional<ConnectorUser> findById(Long id) {
        var found = jpaRepository.findById(id);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUuid(String identifier) {
        return jpaRepository.findByUuid(identifier).map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsername(String username) {
        var found = jpaRepository.findByUsername(username);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByEmail(String email) {
        var found = jpaRepository.findByEmail(email);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public Optional<ConnectorUser> findByUsernameAndEmail(String username, String email) {
        var found = jpaRepository.findByUsernameAndEmail(username, email);
        return found.map(ConnectorUserMapper::toDomain);
    }

    @Override
    public List<ConnectorUser> findAll() {
        return jpaRepository.findAll().stream().map(ConnectorUserMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByUuid(String identifier) {
        var entity = jpaRepository.findByUuid(identifier)
                .orElseThrow(() ->
                        new ConnectorUserNotFoundException(
                                "No user found by identifier " + identifier));
        jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return jpaRepository.existsByUuid(uuid);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndUuidNot(String email, String identifier) {
        return jpaRepository.existsByEmailAndUuidNot(email, identifier);
    }

    @Override
    public boolean existsByUsernameAndUuidNot(String username, String identifier) {
        return jpaRepository.existsByUsernameAndUuidNot(username, identifier);
    }

    private void updateEntity(ConnectorUser domainUser, ConnectorUserEntity entity) {
        entity.setUuid(domainUser.uuid());
        entity.setPassword(domainUser.password());
        entity.setEnabled(domainUser.enabled());
        entity.setUsername(domainUser.username());
        entity.setEmail(domainUser.email());
    }

    private void updateUserRoles(ConnectorUser domainUser, ConnectorUserEntity entity) {
        if (domainUser.roles() != null) {
            Set<String> rolesNames = domainUser.roles()
                    .stream()
                    .map(ConnectorRole::name)
                    .collect(Collectors.toUnmodifiableSet());

            Set<ConnectorRoleEntity> managedRoles = roleRepository.findByNameIn(rolesNames);
            entity.getRoles().clear();
            entity.getRoles().addAll(managedRoles);
        }
    }
}
