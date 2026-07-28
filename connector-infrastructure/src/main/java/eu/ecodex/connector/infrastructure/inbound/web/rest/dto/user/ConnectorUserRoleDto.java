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

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * Data Transfer Object (DTO) representing a user role in the connector system.
 * This class is implemented as an immutable record and is used for transferring user role
 * information between different layers of the application.
 */
@Validated
@Builder(toBuilder = true)
public record ConnectorUserRoleDto(
        String identifier,
        @NonNull
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    public static ConnectorUserRoleDto from(ConnectorUserRole userRole) {
        return ConnectorUserRoleDto
                .builder()
                .identifier(userRole.uuid())
                .name(userRole.name())
                .createdAt(userRole.createdAt())
                .updatedAt(userRole.updatedAt())
                .build();
    }

    public static ConnectorUserRole toDomain(ConnectorUserRoleDto userRoleDto) {
        return ConnectorUserRole
                .builder()
                .uuid(userRoleDto.identifier())
                .name(userRoleDto.name())
                .createdAt(userRoleDto.createdAt())
                .updatedAt(userRoleDto.updatedAt())
                .build();
    }
}
