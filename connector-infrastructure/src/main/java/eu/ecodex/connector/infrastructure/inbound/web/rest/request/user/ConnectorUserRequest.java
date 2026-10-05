/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.user;

import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;


/**
 * Represents a request for creating or updating a user in the connector system.
 */
@Builder(toBuilder = true)
public record ConnectorUserRequest(@NotNull(message = "Username is mandatory")
                                   @NotBlank(message = "Username must not be blank")
                                   String username,
                                   @Size(min = 6, message = "Password must have at least 6 digits")
                                   String password,
                                   @Email
                                   String email,
                                   @NotNull(message = "Enable must not be null")
                                   Boolean enabled,
                                   Set<String> roles
) {

    /**
     * Map a request user into a domain user.
     *
     * @param userRequest user to map
     *
     * @return domain user
     */
    public static ConnectorUser toDomain(ConnectorUserRequest userRequest) {
        return ConnectorUser.builder()
            .username(userRequest.username())
            .password(userRequest.password())
            .email(userRequest.email())
            .enabled(userRequest.enabled())
            .roles(getRoles(userRequest))
            .build();
    }

    private static Set<ConnectorRole> getRoles(ConnectorUserRequest request) {
        if (request.roles() == null) {
            return null;
        }

        return request.roles().stream()
            .map(role ->
                ConnectorRole.builder()
                    .name(role)
                    .build())
            .collect(Collectors.toUnmodifiableSet());
    }
}
