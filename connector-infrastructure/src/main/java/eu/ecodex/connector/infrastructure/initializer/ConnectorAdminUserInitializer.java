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

import static eu.ecodex.connector.domain.model.user.ConnectorRole.DEFAULT_ADMIN_ROLE;
import static eu.ecodex.connector.domain.model.user.ConnectorRole.builder;
import static eu.ecodex.connector.domain.model.user.ConnectorRole.defaultAdminRole;

import eu.ecodex.connector.application.exception.ConnectorRoleAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRegisterRole;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRole;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.property.auth.jwt.ConnectorAdminUserProperties;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes an admin user in the connector system during the application startup phase.
 * The class is designed to ensure that a default or configured admin user exists with appropriate
 * roles.
 *
 * <p>This initializer performs the following steps:
 * 1. Checks if admin user properties are provided in the configuration.
 * 2. If no properties are provided, attempts to register a fallback default admin user.
 * 3. If properties are provided, uses them to initialize the admin user with the configured
 * username, password, email, and role.
 *
 * <p>Key operations:
 * - Registers a new admin role if it does not already exist.
 * - Handles situations where the admin user or default admin role already exists.
 * - Updates an existing user with administrative privileges if necessary.
 *
 * <p>Dependencies:
 * - {@link ConnectorRegisterUser}: Service for registering and updating user information.
 * - {@link ConnectorRegisterRole}: Service for registering user roles.
 * - {@link ConnectorRetrieveUser}: Service for retrieving existing user details.
 * - {@link ConnectorAdminUserProperties}: Configuration properties for the admin user.
 *
 * <p>Implements:
 * - {@link ApplicationRunner}: Allows the initialization logic to execute upon application
 * startup.
 *
 * <p>Logging:
 * - Logs events and outcomes during the initialization process for traceability and debugging.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorAdminUserInitializer implements ApplicationRunner {
    ConnectorRegisterUser registerUserService;
    ConnectorRegisterRole registerUserRoleService;
    ConnectorRetrieveUser retrieveUserService;
    ConnectorRetrieveRole retrieveUserRoleService;
    ConnectorAdminUserProperties adminUserProperties;


    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (adminUserProperties == null || adminUserProperties.isEmpty()) {
            log.info("No Administrator user configured in properties");
            registerFallbackAdminUser();
            return;
        }
        initializeAdminUser(adminUserProperties);
    }

    private void registerFallbackAdminUser() {
        log.info("No Administrator user found in configuration; creating default admin user");
        var existingAdmin =
            retrieveUserService.findByUsername(ConnectorUser.DEFAULT_ADMIN_USER_NAME);

        if (existingAdmin.isEmpty()) {
            registerNewAdminUser();
            return;
        }

        var administrator = existingAdmin.get();
        if (administrator.isDefaultAdmin()) {
            log.info("Default Administrator user already exists.");
            return;
        }
        updateWithAdminRole(administrator);
    }

    private void updateWithAdminRole(ConnectorUser administrator) {
        log.info("Administrator user exists but has not admin role; adding {}", DEFAULT_ADMIN_ROLE);
        try {
            registerUserRoleService.register(defaultAdminRole());
            log.info("{} successfully created.", DEFAULT_ADMIN_ROLE);

        } catch (ConnectorRoleAlreadyExistsException e) {
            log.info("{} already exists.", DEFAULT_ADMIN_ROLE);
        }

        var userRoles = new HashSet<>(
            CollectionUtils.union(
                CollectionUtils.emptyIfNull(administrator.roles()),
                Set.of(defaultAdminRole())
            )
        );

        registerUserService.patch(administrator.uuid(), administrator.toBuilder()
            .roles(userRoles).build());

        log.info("{} added to Administrator user, admin user updated", DEFAULT_ADMIN_ROLE);
    }

    private void registerNewAdminUser() {
        log.info(
            "No default Administrator user found and none registered yet; creating "
                + "default");
        try {
            registerUserRoleService.register(defaultAdminRole());
            log.info("Default Administrator user successfully created.");
        } catch (ConnectorUserAlreadyExistsException e) {
            log.info("Default Administrator role already exists.");
        }
        var defaultAdminUser = ConnectorUser.defaultAdminUser();
        registerUserService.register(defaultAdminUser);
    }

    private void initializeAdminUser(ConnectorAdminUserProperties properties) {
        if (properties.getRole() == null || properties.getRole().isBlank()
            || properties.getUsername() == null || properties.getUsername().isBlank()) {
            registerFallbackAdminUser();
            return;
        }

        log.info("Initializing connector Administrator user with username: {}",
            properties.getUsername());
        var adminRole = builder().name(properties.getRole()).build();
        try {
            adminRole = registerUserRoleService.register(adminRole);
            log.info("Default Administrator {} successfully registered.", properties.getRole());
        } catch (ConnectorRoleAlreadyExistsException e) {
            log.info("Default Administrator {} already registered.", properties.getRole());
            adminRole = retrieveUserRoleService.getByName(properties.getRole());
        }

        try {
            registerUserService.register(createAdminUser(properties, adminRole));
            log.info("Administrator user successfully registered.");
        } catch (ConnectorUserAlreadyExistsException e) {
            log.info("Administrator user already registered");
        }
    }

    private ConnectorUser createAdminUser(ConnectorAdminUserProperties properties,
                                          ConnectorRole adminRole) {
        return ConnectorUser
            .builder()
            .username(properties.getUsername())
            .password(properties.getPassword())
            .email(properties.getEmail())
            .enabled(Boolean.TRUE)
            .roles(Set.of(adminRole))
            .build();
    }

}
