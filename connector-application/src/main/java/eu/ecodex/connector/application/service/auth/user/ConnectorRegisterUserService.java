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

import eu.ecodex.connector.application.exception.ConnectorUserIdMismatchException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;
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
public class ConnectorRegisterUserService implements ConnectorRegisterUser {
    private final ConnectorUserRepository repository;
    private final ConnectorVerifyUniqueUser verifyUniqueUser;
    private final ConnectorUserPasswordEncoder passwordEncoder;

    /**
     * Constructs an instance of {@code ConnectorRegisterUserService} with the specified
     * dependencies.
     *
     * @param repository       the {@code ConnectorUserRepository} used for storing and managing
     *                         user data. Must not be null.
     * @param verifyUniqueUser the {@code ConnectorVerifyUniqueUser} used for verifying the
     *                         uniqueness
     *                         of user attributes like username and email. Must not be null.
     * @param passwordEncoder  the {@code ConnectorUserPasswordEncoder} used for securing user
     *                         passwords
     *                         by encoding them. Must not be null.
     */
    public ConnectorRegisterUserService(ConnectorUserRepository repository,
                                        ConnectorVerifyUniqueUser verifyUniqueUser,
                                        ConnectorUserPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.verifyUniqueUser = verifyUniqueUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ConnectorUser execute(@Nonnull ConnectorUser user) {
        if (user.uuid() != null) {
            throw new ConnectorUserIdMismatchException("Connector user id should be blank");
        }
        verifyUniqueUser.execute(user);
        user = passwordEncoder.encodePassword(user);
        return repository.save(user);
    }
}
