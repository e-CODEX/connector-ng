/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.role;

import eu.ecodex.connector.application.exception.ConnectorUserRoleAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserRoleBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserRoleNotFoundException;
import eu.ecodex.connector.application.port.api.iam.role.ConnectorRegisterUserRole;
import eu.ecodex.connector.application.port.spi.iam.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service responsible for managing the registration and updating of user roles within
 * the Connector system. This implementation interacts with the underlying persistence
 * layer through the {@link ConnectorRoleRepository}.
 * <p>
 * The primary goals of this service include:
 * - Ensuring new user roles are correctly registered in the persistence layer.
 * - Updating existing user roles while validating role uniqueness and integrity.
 * - Logging relevant actions and ensuring the system's constraints are respected.
 * <p>
 * Constraints:
 * - A new user role must not have its identifier field set during registration.
 * - Role names must remain unique within the system.
 * - An existing user role must be identified either by its unique identifier or name
 * during updates.
 * <p>
 * Exception Handling:
 * - Throws {@link ConnectorUserRoleBadRequestException} for invalid input, such as a non-blank
 * identifier during registration.
 * - Throws {@link ConnectorUserRoleNotFoundException} if a user role to update is not found.
 * - Throws {@link ConnectorUserRoleAlreadyExistsException} when a duplicate role name is detected.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRegisterUserRoleService implements ConnectorRegisterUserRole {

    ConnectorRoleRepository repository;

    @Override
    public ConnectorUserRole register(ConnectorUserRole userRole) {
        if (userRole.uuid() != null) {
            throw new ConnectorUserRoleBadRequestException(
                    "Connector user role id should be blank");
        }
        checkRoleName(null, userRole);
        return repository.save(userRole);
    }

    @Override
    public ConnectorUserRole update(String identifier, ConnectorUserRole userRole) {
        if (identifier == null && userRole.uuid() == null) {
            throw new ConnectorUserRoleBadRequestException(
                    "Connector user role id should not be blank");
        }

        var existingUserRole = repository.findByUuid(identifier)
                .orElseThrow(() -> new ConnectorUserRoleNotFoundException(
                        "No existing user role found with id " + identifier));

        checkRoleName(identifier, userRole);

        if (existingUserRole.name().equalsIgnoreCase(userRole.name())) {
            log.info("Nothing to update");
            return existingUserRole;
        }
        var userBuilder = existingUserRole.toBuilder();
        userBuilder.name(userRole.name());

        return repository.save(userBuilder.build());
    }

    private void checkRoleName(String identifier, ConnectorUserRole userRole) {
        var existingUser = repository.findByName(userRole.name());

        if (existingUser.isPresent() &&
                !Objects.equals(existingUser.get().uuid(), identifier)) {
            throw new ConnectorUserRoleAlreadyExistsException(
                    "Role name '%s' already exists".formatted(userRole.name())
            );
        }

    }

}
