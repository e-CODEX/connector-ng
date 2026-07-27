/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * Data Transfer Object representing the role of connector user.
 *
 * <p>This record encapsulates metadata describing an attachment, including
 * its identifier, file information, storage location, and lifecycle timestamps. It does not contain
 * the binary content itself.
 *
 * @param identifier the unique identifier of the user
 * @param name       role name
 * @param createdAt  the timestamp when the attachment was created
 * @param updatedAt  the timestamp when the attachment was last updated
 */

@Validated
@Builder(toBuilder = true)
public record ConnectorUserRoleDto(
        Long identifier,
        @NonNull
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    public static ConnectorUserRoleDto from(ConnectorUserRole userRole) {
        return ConnectorUserRoleDto
                .builder()
                .identifier(userRole.identifier())
                .name(userRole.name())
                .createdAt(userRole.createdAt())
                .updatedAt(userRole.updatedAt())
                .build();
    }

    public static ConnectorUserRole toDomain(ConnectorUserRoleDto userRoleDto) {
        return ConnectorUserRole
                .builder()
                .identifier(userRoleDto.identifier())
                .name(userRoleDto.name())
                .createdAt(userRoleDto.createdAt())
                .updatedAt(userRoleDto.updatedAt())
                .build();
    }
}
