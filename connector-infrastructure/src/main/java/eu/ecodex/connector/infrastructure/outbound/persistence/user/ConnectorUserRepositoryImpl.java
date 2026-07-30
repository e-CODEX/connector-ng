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
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.user.ConnectorUserJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.user.ConnectorUserRoleJpaRepository;
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
    ConnectorUserRoleJpaRepository roleRepository;

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
                    .map(ConnectorUserRole::name)
                    .collect(Collectors.toUnmodifiableSet());

            Set<ConnectorRoleEntity> managedRoles = roleRepository.findByNameIn(rolesNames);
            entity.getRoles().clear();
            entity.getRoles().addAll(managedRoles);
        }
    }
}
