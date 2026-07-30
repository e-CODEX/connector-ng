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

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import jakarta.validation.constraints.Email;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@Builder(toBuilder = true)
public record ConnectorUserRequest(@NonNull
                                   String username,
                                   String password,
                                   @Email
                                   String email,
                                   Boolean enabled,
                                   Set<String> roles
) {

    public static ConnectorUserRequest from(ConnectorUser user) {
        return ConnectorUserRequest
                .builder()
                .username(user.username())
                .password(user.password())
                .email(user.email())
                .enabled(user.enabled())
                .roles(getRoles(user))
                .build();
    }

    public static ConnectorUser toDomain(ConnectorUserRequest userRequest) {
        return ConnectorUser
                .builder()
                .username(userRequest.username())
                .password(userRequest.password())
                .email(userRequest.email())
                .enabled(userRequest.enabled())
                .roles(getRoles(userRequest))
                .build();
    }

    private static Set<String> getRoles(ConnectorUser user) {
        return CollectionUtils.isEmpty(user.roles()) ? Set.of() :
                user.roles().stream().map(ConnectorUserRole::name)
                        .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<ConnectorUserRole> getRoles(ConnectorUserRequest request) {
        if(request.roles() == null) {
            return null;
        }

        return request.roles().stream().map(role ->
                ConnectorUserRole.builder().name(role).build()).collect(Collectors.toUnmodifiableSet());
    }
}
