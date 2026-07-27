/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.user;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Service implementation for managing the registration and updates of {@link ConnectorUser}
 * entities.
 * This class provides methods for creating, updating, and partially updating user information while
 * ensuring
 * data integrity and validation.
 *
 * <p>It handles the following operations:
 * - Validating user data before registration or updates.
 * - Enforcing uniqueness constraints on usernames and email addresses.
 * - Performing full or partial updates on existing users.
 *
 * <p>The class is annotated with {@code @Component} to indicate that it's a Spring-managed bean and
 * {@code @Slf4j} for logging purposes. It requires {@link ConnectorUserRepository} as a dependency
 * to perform data access operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRegisterUserService implements ConnectorRegisterUser {

    ConnectorUserRepository repository;
    ConnectorUserPasswordEncoder passwordEncoder;

    @Override
    public ConnectorUser register(ConnectorUser user) {
        if (user.uuid() != null) {
            throw new ConnectorUserBadRequestException("Connector user id should be blank");
        }
        checkEmail(Optional.empty(), user);
        checkUsername(Optional.empty(), user);

        user = secureUserPassword(user);
        return repository.save(user);
    }

    /**
     * Updates the information of an existing {@link ConnectorUser} entity.
     * except the roles
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to be updated; must not
     *                   be null
     * @param user       the {@link ConnectorUser} object containing the updated information; must
     *                   not be null
     *
     * @return updated {@link ConnectorUser} object
     */
    @Override
    public ConnectorUser update(String identifier, ConnectorUser user) {
        var existingUser = getExistingUser(identifier, user);

        if (hasSameContent(existingUser, user, identifier)) {
            log.info("Nothing to update");
            return existingUser;
        }

        var userBuilder = existingUser.toBuilder();
        var encodePassword = passwordEncoder.encodePassword(user.password());
        userBuilder.password(encodePassword);
        userBuilder.username(user.username());
        userBuilder.email(user.email());
        userBuilder.enabled(user.enabled());

        return repository.save(userBuilder.build());
    }


    /**
     * Patches the specified {@link ConnectorUser} with the provided fields.
     * The roles field cannot be patched.
     *
     * @param identifier the unique identifier of the {@link ConnectorUser} to be patched; must not
     *                   be null
     * @param user       the {@link ConnectorUser} object containing the fields to update; must not
     *                   be null
     *
     * @return patched {@link ConnectorUser} object
     */
    @Override
    public ConnectorUser patch(String identifier, ConnectorUser user) {
        var existingUser = getExistingUser(identifier, user);
        var userBuilder = existingUser.toBuilder();

        if (StringUtils.hasText(user.email())) {
            userBuilder.email(user.email());
        }

        if (StringUtils.hasText(user.username())) {
            userBuilder.username(user.username());
        }

        if (StringUtils.hasText(user.password())) {
            if (passwordEncoder.matches(user.password(), existingUser.password())) {
                log.warn("New password matches existing");
            } else {
                var encodedPassword = passwordEncoder.encodePassword(user.password());
                userBuilder.password(encodedPassword);
            }
        }

        if (user.enabled() != null) {
            userBuilder.enabled(user.enabled());
        }
        return repository.save(userBuilder.build());
    }

    private ConnectorUser getExistingUser(String identifier, ConnectorUser user) {
        if (identifier == null) {
            throw new ConnectorUserBadRequestException("Connector user id should not be blank");
        }

        var existingUser = repository
            .findByUuid(identifier)
            .orElseThrow(() -> new ConnectorUserNotFoundException(
                "No existing user found with id " + identifier));

        checkUsername(Optional.of(identifier), user);
        checkEmail(Optional.of(identifier), user);

        return existingUser;
    }

    /**
     * Secures the password of the given {@code ConnectorUser} by encoding it. If the password is
     * blank or null, an exception is thrown.
     *
     * @param user the {@code ConnectorUser} object whose password is to be secured. Must not be
     *             null
     *             and must contain a non-blank password.
     *
     * @return the {@code ConnectorUser} object with its password encoded.
     *
     * @throws ConnectorUserBadRequestException if the password is null or blank.
     */
    private ConnectorUser secureUserPassword(ConnectorUser user) {
        if (isEmpty(user.password()) || isBlank(user.password())) {
            throw new ConnectorUserBadRequestException(
                "Connector user password should not be blank");
        }
        return passwordEncoder.encodePassword(user);
    }

    /**
     * Validates whether a username is unique among existing users in the system.
     * If a user with the same username already exists and their UUID differs from the provided ID,
     * an exception is thrown to indicate that the username is already taken.
     *
     * @param identifier the unique identifier of the user being checked; used to verify if the
     *                   username belongs
     *                   to the same existing user or a different one
     * @param user       the ConnectorUser object containing the username to be validated
     *
     * @throws ConnectorUserAlreadyExistsException if a user with the same username exists, and
     *                                             their UUID is different from the provided ID
     */

    private void checkUsername(Optional<String> identifier, ConnectorUser user) {
        boolean exists;

        exists = identifier
            .map(id -> repository.existsByUsernameAndUuidNot(user.username(), id))
            .orElseGet(() -> repository.existsByUsername(user.username()));

        if (exists) {
            throw new ConnectorUserAlreadyExistsException(
                "Username '%s' already exists".formatted(user.username())
            );
        }
    }

    /**
     * Validates whether the email associated with a given user is unique among existing users in
     * the system.
     * If a user with the same email already exists and their UUID differs from the provided ID, an
     * exception is thrown
     * to indicate that the email is already taken.
     *
     * @param identifier the unique identifier of the user being checked; used to verify if the
     *                   email belongs to
     *                   the same existing user or a different one
     * @param user       the ConnectorUser object containing the email to be validated
     *
     * @throws ConnectorUserAlreadyExistsException if a user with the same email exists,
     *                                             and their UUID is different from the provided ID
     */
    private void checkEmail(Optional<String> identifier, ConnectorUser user) {
        if (user.email() == null) {
            return;
        }
        boolean exists;

        exists = identifier
            .map(id -> repository.existsByEmailAndUuidNot(user.email(), id))
            .orElseGet(() -> repository.existsByEmail(user.email()));

        if (exists) {
            throw new ConnectorUserAlreadyExistsException(
                "User email '%s' already exists".formatted(user.email())
            );
        }
    }

    /**
     * Compares the connectorUser {@code ConnectorUser} object with the existing
     * {@code ConnectorUser}
     * object
     * to determine if they have identical content. The comparison is based on the values of the
     * fields:
     * {@code uuid}, {@code username}, {@code password}, {@code email}, and {@code enabled}.
     *
     * @param existingUser  the {@code ConnectorUser} object to compare with. Must not be null.
     * @param connectorUser the {@code ConnectorUser} object to be compared against the existing.
     *                      Must not be null.
     * @param identifier    the identifier of user to update
     *
     * @return {@code true} if all compared fields have the same values in both objects;
     *     {@code false} otherwise.
     */
    private boolean hasSameContent(ConnectorUser existingUser,
                                   ConnectorUser connectorUser,
                                   String identifier) {

        return Objects.equals(identifier, existingUser.uuid())
            && Objects.equals(connectorUser.username(), existingUser.username())
            && Objects.equals(connectorUser.email(), existingUser.email())
            && Objects.equals(connectorUser.enabled(), existingUser.enabled())
            && passwordEncoder.matches(connectorUser.password(), existingUser.password());
    }

}
