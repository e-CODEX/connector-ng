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
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorUserRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.user.ConnectorUserRoleJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserRoleRepositoryImpl implements ConnectorUserRoleRepository {

    ConnectorUserRoleJpaRepository jpaRepository;

    @Override
    public ConnectorUserRole save(ConnectorUserRole userRole) {
        var existing = jpaRepository.findByUuid(
                userRole.uuid()); // TODO check if this call could be optimized
        ConnectorUserRoleEntity entity = null;
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
    public Optional<ConnectorUserRole> findByUuid(String identifier) {
        var found = jpaRepository.findByUuid(identifier);
        return found.map(this::toDomain);
    }

    @Override
    public Optional<ConnectorUserRole> findByName(String name) {
        var found = jpaRepository.findByName(name);
        return found.map(this::toDomain);
    }

    @Override
    public List<ConnectorUserRole> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteByUuid(String identifier) {
        var entity = jpaRepository.findByUuid(identifier)
                .orElseThrow(() ->
                        new ConnectorUserNotFoundException(
                                "No user role found with id " + identifier));
        jpaRepository.delete(entity);
    }


    private ConnectorUserRoleEntity toEntity(ConnectorUserRole domainUserRole) {
        return ConnectorUserRoleEntity.builder()
                .uuid(domainUserRole.uuid())
                .name(domainUserRole.name())
                .build();
    }

    private ConnectorUserRole toDomain(ConnectorUserRoleEntity entity) {
        return new ConnectorUserRole(entity.getUuid(), entity.getName(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
