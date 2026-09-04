/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.initializer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRole;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.property.auth.jwt.ConnectorAdminUserProperties;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
class ConnectorAdminUserInitializerTest {
    @Mock
    ConnectorRegisterUser registerUserService;

    @Mock
    ConnectorRegisterRole registerUserRoleService;

    @Mock
    ConnectorRetrieveUser retrieveUserService;

    @Mock
    ConnectorRetrieveRole retrieveUserRoleService;

    ConnectorAdminUserProperties adminUserProperties;

    @Mock
    ApplicationArguments applicationArguments;


    ConnectorAdminUserInitializer initializer;

    private static ConnectorAdminUserProperties properties(
        String username, String role, String pwd, String email
    ) {
        var props = new ConnectorAdminUserProperties();
        props.setEmail(email);
        props.setUsername(username);
        props.setRole(role);
        props.setPassword(pwd);
        return props;
    }

    @BeforeEach
    void setUp() {
        initializer = new ConnectorAdminUserInitializer(
            registerUserService, registerUserRoleService, retrieveUserService,
            retrieveUserRoleService, adminUserProperties
        );
    }


    @Test
    void run_should_register_admin_user_in_props_when_no_admin_user_exists() {
        // Given
        var username = "ADMIN";
        var adminRole = "ROLE_ADMIN";
        var pwd = "password";
        var email = "testadmin@email.com";
        adminUserProperties = properties(username, adminRole, pwd, email);

        var role = ConnectorRole.builder().name(adminRole).build();
        var user = ConnectorUser.builder()
            .username(username)
            .password(pwd)
            .email(email)
            .roles(Set.of(role))
            .enabled(Boolean.TRUE)
            .build();

        when(registerUserRoleService.register(any())).thenReturn(role);
        when(registerUserService.register(any())).thenReturn(user);

        // When
        initializer = new ConnectorAdminUserInitializer(
            registerUserService, registerUserRoleService, retrieveUserService,
            retrieveUserRoleService, adminUserProperties
        );

        initializer.run(applicationArguments);

        // Then
        verify(registerUserRoleService).register(role);
        verify(registerUserService).register(user);

        verifyNoMoreInteractions(registerUserRoleService, registerUserService, retrieveUserService,
            retrieveUserRoleService, applicationArguments);
    }

    @Test
    void run_should_register_default_admin_when_no_user_in_props_and_no_admin_user_exists() {
        // Given
        var defaultAdmin = "admin";
        var roleAdmin = "ROLE_ADMIN";
        var defaultPwd = "123456";

        adminUserProperties = new ConnectorAdminUserProperties();

        var role = ConnectorRole.builder().name(roleAdmin).build();
        var user = ConnectorUser.builder()
            .username(defaultAdmin)
            .password(defaultPwd)
            .roles(Set.of(role))
            .enabled(Boolean.TRUE)
            .build();

        when(registerUserRoleService.register(any())).thenReturn(role);
        when(registerUserService.register(any())).thenReturn(user);
        when(retrieveUserService.findByUsername(any())).thenReturn(Optional.empty());

        // When
        initializer = new ConnectorAdminUserInitializer(
            registerUserService, registerUserRoleService, retrieveUserService,
            retrieveUserRoleService, adminUserProperties
        );

        initializer.run(applicationArguments);

        // Then
        verify(registerUserRoleService).register(role);
        verify(registerUserService).register(user);
        verify(retrieveUserService).findByUsername(defaultAdmin);

        verifyNoMoreInteractions(registerUserRoleService, registerUserService, retrieveUserService,
            retrieveUserRoleService, applicationArguments);
    }

    @Test
    void run_should_do_nothing_when_no_user_in_props_and_admin_user_exists() {
        // Given
        var defaultAdmin = "admin";
        var roleAdmin = "ROLE_ADMIN";
        var defaultPwd = "123456";

        adminUserProperties = new ConnectorAdminUserProperties();

        var role = ConnectorRole.builder().name(roleAdmin).build();
        var user = ConnectorUser.builder()
            .username(defaultAdmin)
            .password(defaultPwd)
            .roles(Set.of(role))
            .enabled(Boolean.TRUE)
            .build();

        when(retrieveUserService.findByUsername(any())).thenReturn(Optional.of(user));

        // When
        initializer = new ConnectorAdminUserInitializer(
            registerUserService, registerUserRoleService, retrieveUserService,
            retrieveUserRoleService, adminUserProperties
        );

        initializer.run(applicationArguments);

        // Then
        verify(retrieveUserService).findByUsername(defaultAdmin);

        verifyNoMoreInteractions(registerUserRoleService, registerUserService, retrieveUserService,
            retrieveUserRoleService, applicationArguments);
    }

    @Test
    void run_should_register_when_no_user_in_props_and_admin_user_exists_with_no_admin_role() {
        // Given
        var defaultAdmin = "admin";
        var roleAdmin = "ROLE_ADMIN";
        var defaultPwd = "123456";
        var identifier = "identifier";

        adminUserProperties = new ConnectorAdminUserProperties();

        var role = ConnectorRole.builder().name(roleAdmin).build();
        var user = ConnectorUser.builder()
            .uuid(identifier)
            .username(defaultAdmin)
            .password(defaultPwd)
            .enabled(Boolean.TRUE)
            .build();

        when(retrieveUserService.findByUsername(any())).thenReturn(Optional.of(user));
        when(registerUserRoleService.register(any())).thenReturn(role);

        // When
        initializer = new ConnectorAdminUserInitializer(
            registerUserService, registerUserRoleService, retrieveUserService,
            retrieveUserRoleService, adminUserProperties
        );

        initializer.run(applicationArguments);

        // Then
        verify(retrieveUserService).findByUsername(defaultAdmin);
        verify(registerUserRoleService).register(role);
        verify(registerUserService).patch(identifier, user.toBuilder().roles(Set.of(role)).build());

        verifyNoMoreInteractions(registerUserRoleService, registerUserService, retrieveUserService,
            retrieveUserRoleService, applicationArguments);
    }
}