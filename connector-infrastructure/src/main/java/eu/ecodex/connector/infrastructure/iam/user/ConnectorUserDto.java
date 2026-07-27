/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.iam.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.validation.constraints.Email;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * Data Transfer Object representing an attachment managed by the connector.
 *
 * <p>This record encapsulates metadata describing an attachment, including
 * its identifier, file information, storage location, and lifecycle timestamps. It does not contain
 * the binary content itself.
 *
 * @param identifier the unique identifier of the user
 * @param username   user name
 * @param password   the user password
 * @param email      the user email
 * @param enabled    user is enabled or not
 * @param createdAt  the timestamp when the attachment was created
 * @param updatedAt  the timestamp when the attachment was last updated
 */

@Validated
@Builder(toBuilder = true)
public record ConnectorUserDto(
        Long identifier,
        @NonNull
        String username,
        String password,
        @Email
        String email,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {

    public static ConnectorUserDto from(ConnectorUser user) {
        return ConnectorUserDto
                .builder()
                .identifier(user.identifier())
                .username(user.username())
                .password(user.password())
                .email(user.email())
                .enabled(user.enabled())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    public static ConnectorUser toDomain(ConnectorUserDto userDto) {
        return ConnectorUser
                .builder()
                .identifier(userDto.identifier())
                .username(userDto.username())
                .password(userDto.password())
                .email(userDto.email())
                .enabled(userDto.enabled())
                .createdAt(userDto.createdAt())
                .updatedAt(userDto.updatedAt())
                .build();
    }
}
