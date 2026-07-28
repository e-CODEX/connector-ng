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
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@Builder(toBuilder = true)
public record ConnectorUserRequest(@NonNull
                                   String username,
                                   String password,
                                   @Email
                                   String email,
                                   Boolean enabled
) {

    public static ConnectorUserRequest from(ConnectorUser user) {
        return ConnectorUserRequest
                .builder()
                .username(user.username())
                .password(user.password())
                .email(user.email())
                .enabled(user.enabled())
                .build();
    }

    public static ConnectorUser toDomain(ConnectorUserRequest userRequest) {
        return ConnectorUser
                .builder()
                .username(userRequest.username())
                .password(userRequest.password())
                .email(userRequest.email())
                .enabled(userRequest.enabled())
                .build();
    }
}
