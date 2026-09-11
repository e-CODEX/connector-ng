/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.auth;

import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations and query methods on
 * {@link ConnectorUserEntity} using JPA. This interface is responsible for managing the
 * persistence
 * and retrieval of ConnectorUserEntity instances
 * from the database.
 *
 * <p>The interface includes queries to retrieve user data by various unique identifiers (UUID,
 * username, email) and to check for their existence within the database. Additionally, it uses
 * EntityGraph annotations to optimize fetching associated user roles.
 *
 * <p>Inheritance:
 * - Extends {@link JpaRepository}, which provides basic CRUD functionality
 * and query method support.
 */
@Repository
public interface ConnectorUserJpaRepository extends JpaRepository<ConnectorUserEntity, Long> {
    /**
     * Retrieves all instances of the ConnectorUserEntity from the database,
     * including their associated roles as specified by the defined entity graph.
     *
     * @return a list of ConnectorUserEntity objects along with their associated roles.
     */
    @Override
    @EntityGraph(attributePaths = "roles")
    @NonNull
    List<ConnectorUserEntity> findAll();

    /**
     * Retrieves a {@link ConnectorUserEntity} by its unique UUID, along with the associated roles.
     * This method uses an {@link EntityGraph} to fetch the "roles" relationship eagerly.
     *
     * @param uuid the unique identifier of the user; must not be null.
     *
     * @return an {@link Optional} containing the {@link ConnectorUserEntity} if found, or an empty
     *     {@link Optional} if no user is found with the given UUID.
     */
    @EntityGraph(attributePaths = {"roles"})
    Optional<ConnectorUserEntity> findByUuid(@Nonnull String uuid);

    /**
     * Retrieves a {@link ConnectorUserEntity} by its username along with the associated roles.
     * This method uses an {@link EntityGraph} to fetch the "roles" relationship eagerly.
     *
     * @param username the username of the user to be retrieved; must not be null.
     *
     * @return an {@link Optional} containing the {@link ConnectorUserEntity} if found, or an empty
     *     {@link Optional}
     *     if no user is found with the given username.
     */
    @EntityGraph(attributePaths = {"roles"})
    Optional<ConnectorUserEntity> findByUsername(@Nonnull String username);

    /**
     * Retrieves an active {@link ConnectorUserEntity} by its username along with the associated
     * roles.
     * This method uses an {@link EntityGraph} to fetch the "roles" relationship eagerly.
     *
     * @param username the username of the user to be retrieved; must not be null.
     *
     * @return an {@link Optional} containing the {@link ConnectorUserEntity} if found, or an empty
     */
    @EntityGraph(attributePaths = {"roles"})
    Optional<ConnectorUserEntity> findByUsernameAndEnabled(@Nonnull String username,
                                                           boolean enabled);

    /**
     * Checks whether an entity with the specified UUID exists in the database.
     *
     * @param uuid the unique identifier of the entity to check; must not be null.
     *
     * @return true if an entity with the given UUID exists, false otherwise.
     */
    boolean existsByUuid(@Nonnull String uuid);

    /**
     * Checks whether a user with the specified username exists in the database.
     *
     * @param username the username of the user to check; must not be null.
     *
     * @return true if a user with the given username exists, false otherwise.
     */
    boolean existsByUsername(@Nonnull String username);

    /**
     * Checks whether a user with the specified email exists in the database.
     *
     * @param email the email of the user to check; must not be null.
     *
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsByEmail(@Nonnull String email);

    /**
     * Checks whether a user with the specified username exists in the database,
     * excluding the user with the provided UUID.
     *
     * @param username the username to check; must not be null.
     * @param uuid     the UUID to exclude from the search; must not be null.
     *
     * @return true if a user with the given username exists but has a different UUID, false
     *     otherwise.
     */
    boolean existsByUsernameAndUuidNot(@Nonnull String username, @Nonnull String uuid);

    /**
     * Checks whether a user with the specified email exists in the database,
     * excluding the user with the provided UUID.
     *
     * @param email the email to check; must not be null.
     * @param uuid  the UUID to exclude from the search; must not be null.
     *
     * @return true if a user with the given email exists but has a different UUID, false otherwise.
     */
    boolean existsByEmailAndUuidNot(@Nonnull String email, @Nonnull String uuid);
}
