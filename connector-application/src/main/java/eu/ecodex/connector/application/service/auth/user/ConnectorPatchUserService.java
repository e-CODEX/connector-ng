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

import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;
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
public class ConnectorPatchUserService implements ConnectorPatchUser {
    private final ConnectorUserRepository repository;
    private final ConnectorVerifyUniqueUser verifyUniqueUser;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorUserPasswordEncoder passwordEncoder;

    /**
     * Constructs an instance of {@code ConnectorPatchUserService} with the required dependencies.
     *
     * @param repository               the repository used for user data operations; must not be
     *                                 null
     * @param verifyUniqueUser         the service to verify user uniqueness; must not be null
     * @param retrieveUserByIdentifier the service to retrieve a user by its unique identifier; must
     *                                 not be null
     * @param passwordEncoder          the password encoder used for encoding user passwords; must
     *                                 not be null
     */
    public ConnectorPatchUserService(ConnectorUserRepository repository,
                                     ConnectorVerifyUniqueUser verifyUniqueUser,
                                     ConnectorRetrieveUserByIdentifierService
                                         retrieveUserByIdentifier,
                                     ConnectorUserPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.verifyUniqueUser = verifyUniqueUser;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
        this.passwordEncoder = passwordEncoder;
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
    public ConnectorUser execute(@Nonnull String identifier, @Nonnull ConnectorUser user) {
        var existingUser = retrieveUserByIdentifier.execute(identifier);
        verifyUniqueUser.execute(identifier, user);

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
}
