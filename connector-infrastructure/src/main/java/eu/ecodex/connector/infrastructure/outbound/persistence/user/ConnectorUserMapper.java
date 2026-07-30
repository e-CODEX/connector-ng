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

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConnectorUserMapper {

    public ConnectorUserEntity toEntity(ConnectorUser domainUser) {
        return ConnectorUserEntity.builder()
                .uuid(domainUser.uuid())
                .username(domainUser.username())
                .password(domainUser.password())
                .email(domainUser.email())
                .enabled(domainUser.enabled())
                .build();
    }


    public ConnectorUser toDomain(ConnectorUserEntity entity) {
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


    public static Set<ConnectorUserRole> getUserRoles(ConnectorUserEntity entity) {
        return entity.getRoles() == null ? null :
                entity.getRoles().stream()
                        .map(toRoleDomain())
                        .collect(Collectors.toSet());
    }

    private static Function<ConnectorRoleEntity, ConnectorUserRole> toRoleDomain() {
        return role ->
                ConnectorUserRole.builder()
                        .uuid(role.getUuid())
                        .name(role.getName()).build();

    }

}