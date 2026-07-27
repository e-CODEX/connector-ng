/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;


import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorRoleName;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import java.util.Set;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class ConnectorUserTestFixtures {
    public static ConnectorUser createDefaultUser() {
        return ConnectorUser
            .builder()
            .uuid("0ecd850c-3f8e-47a8-b95d-d56d336bb83a")
            .username("test_user")
            .email("test_user@email.com")
            .password("encoded")
            .enabled(true)
            .build();
    }

    public static ConnectorUser createDefaultUserWithRoles() {
        return ConnectorUser
            .builder()
            .uuid("0ecd850c-3f8e-47a8-b95d-d56d336bb83a")
            .username("test_user")
            .email("test_user@email.com")
            .password("encoded")
            .enabled(true)
            .roles(Set.of(ConnectorRole
                .builder()
                .name("ROLE_".concat(ConnectorRoleName.ADMIN.name()))
                .build()))
            .build();
    }

    public static ConnectorUserDto createUserDto() {
        return ConnectorUserDto.builder()
            .username("test_user")
            .identifier("0ecd850c-3f8e-47a8-b95d-d56d336bb83a")
            .email("test_user@email.com")
            .enabled(true)
            .build();
    }

    public static ConnectorUserDto createUserDtoWithRoles() {
        return ConnectorUserDto.builder()
            .username("test_user")
            .identifier("0ecd850c-3f8e-47a8-b95d-d56d336bb83a")
            .email("test_user@email.com")
            .enabled(true)
            .roles(Set.of("ROLE_ADMIN"))
            .build();
    }

    public static ConnectorUserRequest createDefaultUserRequest() {
        return ConnectorUserRequest.builder()
            .username("test_user")
            .email("test_user@email.com")
            .password("test_password")
            .enabled(true)
            .build();
    }

    public static ConnectorUserRequest createDefaultUserPatchRequest() {
        return ConnectorUserRequest.builder()
            .username("test_user")
            .email("test_user@email.com")
            .build();
    }

    public static ConnectorUser createDefaultUserPatched() {
        return ConnectorUser.builder()
            .username("test_user")
            .email("test_user@email.com")
            .build();
    }

    public static ConnectorUserRequest createDefaultUserRequestWithRoles() {
        return ConnectorUserRequest.builder()
            .username("test_user")
            .email("test_user@email.com")
            .password("test_password")
            .enabled(true)
            .roles(Set.of("ROLE_ADMIN"))
            .build();
    }

    public static ConnectorUserRequest createUserRequest(String username, String email,
                                                         String password) {
        return ConnectorUserRequest.builder()
            .username(username)
            .email(email)
            .password(password)
            .enabled(true)
            .build();
    }

    public static ConnectorUserDetails createUserDetails() {
        return new ConnectorUserDetails(createDefaultUserWithRoles());
    }
}

