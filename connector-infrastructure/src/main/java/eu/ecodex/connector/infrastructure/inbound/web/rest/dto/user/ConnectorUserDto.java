/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user;

import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

/**
 * A Data Transfer Object (DTO) that represents a user in the connector system.
 * This class is a record that encapsulates user-related data and performs
 * validation on some of its fields.
 *
 * <ul>
 *   <li>It provides a builder for creating immutable instances of the class.</li>
 *   <li>Includes methods for mapping between the domain model {@code ConnectorUser}
 *       and the DTO model.</li>
 * </ul>
 */
@Builder(toBuilder = true)
public record ConnectorUserDto(
        String identifier,
        String username,
        String email,
        Boolean enabled,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Converts a {@link ConnectorUser} instance into a {@link ConnectorUserDto} instance.
     *
     * @param user the {@link ConnectorUser} to be converted
     *
     * @return a new {@link ConnectorUserDto} instance containing the mapped values
     */
    public static ConnectorUserDto from(ConnectorUser user) {
        return ConnectorUserDto
                .builder()
                .identifier(user.uuid())
                .username(user.username())
                .email(user.email())
                .enabled(user.enabled())
                .roles(getRoles(user))
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    private static Set<String> getRoles(ConnectorUser user) {
        if (user == null || user.roles() == null) {
            return null;
        }
        return user
                .roles()
                .stream()
                .map(ConnectorRole::name)
                .collect(Collectors.toUnmodifiableSet());
    }

}
