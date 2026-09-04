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

import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Helper class to map domain user into entity user and vice versa.
 */
@UtilityClass
public class ConnectorUserMapper {

    /**
     * Map an entity user into a domain user.
     *
     * @param entity entity user
     *
     * @return domain user
     */
    public ConnectorUser toDomain(ConnectorUserEntity entity) {
        return ConnectorUser
            .builder()
            .uuid(entity.getUuid())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .email(entity.getEmail())
            .enabled(entity.isEnabled())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .roles(toDomainRoles(entity))
            .build();
    }


    /**
     * Map a user entity role.
     *
     * @param entity entity to map
     *
     * @return Set of domain roles
     */
    public Set<ConnectorRole> toDomainRoles(ConnectorUserEntity entity) {
        return entity.getRoles() == null ? null :
            entity.getRoles().stream()
                .map(ConnectorUserMapper::toDomainRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    private ConnectorRole toDomainRole(ConnectorRoleEntity role) {
        return ConnectorRole
            .builder()
            .uuid(role.getUuid())
            .name(role.getName())
            .createdAt(role.getCreatedAt())
            .updatedAt(role.getUpdatedAt())
            .build();
    }

}