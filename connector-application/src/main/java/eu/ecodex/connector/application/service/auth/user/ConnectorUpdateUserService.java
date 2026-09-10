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

import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class ConnectorUpdateUserService implements ConnectorUpdateUser {
    private final ConnectorUserRepository repository;
    private final ConnectorVerifyUniqueUser verifyUniqueUser;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorUserPasswordEncoder passwordEncoder;

    /**
     * Constructs a new instance of the {@code ConnectorUpdateUserService} class.
     * This service is responsible for updating user data in the system by utilizing the provided
     * dependencies.
     *
     * @param repository               the {@code ConnectorUserRepository} instance used for
     *                                 accessing and persisting user data; must not be null.
     * @param verifyUniqueUser         the {@code ConnectorVerifyUniqueUser} instance used for
     *                                 ensuring the uniqueness of a user; must not be null.
     * @param retrieveUserByIdentifier the {@code ConnectorRetrieveUserByIdentifier} instance used
     *                                 for fetching a user by its unique identifier; must not be
     *                                 null.
     * @param passwordEncoder          the {@code ConnectorUserPasswordEncoder} instance used for
     *                                 encoding user passwords; must not be null.
     */
    public ConnectorUpdateUserService(ConnectorUserRepository repository,
                                      ConnectorVerifyUniqueUser verifyUniqueUser,
                                      ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier,
                                      ConnectorUserPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.verifyUniqueUser = verifyUniqueUser;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
        this.passwordEncoder = passwordEncoder;
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
    public ConnectorUser execute(@Nonnull String identifier, @Nonnull ConnectorUser user) {
        var existingUser = retrieveUserByIdentifier.execute(identifier);
        verifyUniqueUser.execute(identifier, user);

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
