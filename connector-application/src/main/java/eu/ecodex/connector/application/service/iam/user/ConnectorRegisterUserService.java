/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service implementation for managing the registration and updates of {@link ConnectorUser} entities.
 * This class provides methods for creating, updating, and partially updating user information while ensuring
 * data integrity and validation.
 * <p>
 * It handles the following operations:
 * - Validating user data before registration or updates.
 * - Enforcing uniqueness constraints on usernames and email addresses.
 * - Performing full or partial updates on existing users.
 * <p>
 * The class is annotated with {@code @Component} to indicate that it's a Spring-managed bean and
 * {@code @Slf4j} for logging purposes. It requires {@link ConnectorUserRepository} as a dependency
 * to perform data access operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRegisterUserService implements ConnectorRegisterUser {

    ConnectorUserRepository repository;

    @Override
    public ConnectorUser register(ConnectorUser user) {
        if (user.uuid() != null) {
            throw new ConnectorUserBadRequestException("Connector user id should be blank");
        }
        checkEmail(null, user);
        checkUsername(null, user);
        return repository.save(user);
    }

    @Override
    public ConnectorUser update(String identifier, ConnectorUser user) {
        var existingUser = getExistingUser(identifier, user);

        if (existingUser.hasSameContent(user)) {
            log.info("Nothing to update");
            return existingUser;
        }
        var userBuilder = existingUser.toBuilder();
        userBuilder.username(user.username());
        userBuilder.password(user.password());
        userBuilder.email(user.email());
        userBuilder.enabled(user.enabled());

        return repository.save(userBuilder.build());
    }

    @Override
    public ConnectorUser patch(String identifier, ConnectorUser user) {
        var existingUser = getExistingUser(identifier, user);

        var userBuilder = existingUser.toBuilder();
        if (user.email() != null) {
            userBuilder.email(user.email());
        }

        if (user.username() != null) {
            userBuilder.username(user.username());
        }

        if (user.password() != null) {
            userBuilder.password(user.password());
        }

        if (user.enabled() != null) {
            userBuilder.enabled(user.enabled());
        }

        return repository.save(userBuilder.build());
    }

    private ConnectorUser getExistingUser(String identifier, ConnectorUser user) {
        if (identifier == null && user.uuid() == null) {
            throw new ConnectorUserBadRequestException("Connector user id should not be blank");
        }

        var existingUser = repository.findByUuId(identifier)
                .orElseThrow(() -> new ConnectorUserNotFoundException(
                        "No existing user found with id " + identifier));

        checkUsername(identifier, user);

        checkEmail(identifier, user);

        return existingUser;
    }

    /**
     * Validates whether a username is unique among existing users in the system.
     * If a user with the same username already exists and their UUID differs from the provided ID,
     * an exception is thrown to indicate that the username is already taken.
     *
     * @param identifier the unique identifier of the user being checked; used to verify if the username belongs
     *           to the same existing user or a different one
     * @param user the ConnectorUser object containing the username to be validated
     * @throws ConnectorUserAlreadyExistsException if a user with the same username exists, and
     *                                             their UUID is different from the provided ID
     */

    private void checkUsername(String identifier, ConnectorUser user) {
        var existingUser = repository.findByUsername(user.username());

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().uuid(), identifier)) {
            throw new ConnectorUserAlreadyExistsException(
                    "Username '%s' already exists".formatted(user.username())
            );
        }
    }

    /**
     * Validates whether the email associated with a given user is unique among existing users in the system.
     * If a user with the same email already exists and their UUID differs from the provided ID, an exception is thrown
     * to indicate that the email is already taken.
     *
     * @param identifier the unique identifier of the user being checked; used to verify if the email belongs to
     *           the same existing user or a different one
     * @param user the ConnectorUser object containing the email to be validated
     * @throws ConnectorUserAlreadyExistsException if a user with the same email exists,
     *                                             and their UUID is different from the provided ID
     */
    private void checkEmail(String identifier, ConnectorUser user) {
        if (user.email() != null) {
            var existingUser = repository.findByEmail(user.email());

            if (existingUser.isPresent() && !Objects.equals(existingUser.get().uuid(), identifier)) {
                throw new ConnectorUserAlreadyExistsException(
                        "User email '%s' already exists".formatted(user.email())
                );
            }
        }
    }
}
