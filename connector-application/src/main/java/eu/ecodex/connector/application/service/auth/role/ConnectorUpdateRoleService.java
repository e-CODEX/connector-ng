/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.role;

import eu.ecodex.connector.application.exception.ConnectorRoleAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorRoleBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorUpdateRole;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Service responsible for managing the registration and updating of user roles within
 * the Connector system. This implementation interacts with the underlying persistence
 * layer through the {@link ConnectorRoleRepository}.
 *
 * <p>The primary goals of this service include:
 * - Ensuring new user roles are correctly registered in the persistence layer.
 * - Updating existing user roles while validating role uniqueness and integrity.
 * - Logging relevant actions and ensuring the system's constraints are respected.
 *
 * <p>Constraints:
 * - A new user role must not have its identifier field set during registration.
 * - Role names must remain unique within the system.
 * - An existing user role must be identified either by its unique identifier or name
 * during updates.
 *
 * <p>Exception Handling:
 * - Throws {@link ConnectorRoleBadRequestException} for invalid input, such as a non-blank
 * identifier during registration.
 * - Throws {@link ConnectorRoleNotFoundException} if a user role to update is not found.
 * - Throws {@link ConnectorRoleAlreadyExistsException} when a duplicate role name is detected.
 */
@Slf4j
@Component
public class ConnectorUpdateRoleService implements ConnectorUpdateRole {
    private final ConnectorRoleRepository repository;

    public ConnectorUpdateRoleService(ConnectorRoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConnectorRole execute(@NonNull String identifier, @NonNull ConnectorRole userRole) {
        var existingUserRole = repository.findByUuid(identifier)
            .orElseThrow(() -> new ConnectorRoleNotFoundException(
                "No existing user role found with id " + identifier));

        validateRoleName(identifier, userRole);

        if (existingUserRole.name().equalsIgnoreCase(userRole.name())) {
            log.info("Nothing to update");
            return existingUserRole;
        }
        var userBuilder = existingUserRole.toBuilder();
        userBuilder.name(userRole.name());

        return repository.save(userBuilder.build());
    }

    private void validateRoleName(@NonNull String identifier, @NonNull ConnectorRole userRole) {
        var existingUser = repository.findByName(userRole.name());

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().uuid(), identifier)) {
            throw new ConnectorRoleAlreadyExistsException(
                "Role name '%s' already exists".formatted(userRole.name())
            );
        }
    }
}
