/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.auth.user;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;
import java.util.Optional;

/**
 * Interface for managing ConnectorUser entities in the persistence layer.
 *
 * <p>This interface provides methods for creating, retrieving, updating,
 * and deleting instances of the ConnectorUser entity, as well as additional methods
 * for querying such as finding by username, email, or a combination of username and email.
 * It serves as an abstraction layer to handle database operations related to users
 * in the Connector system.
 */
public interface ConnectorUserRepository {

    /**
     * Persists a ConnectorUser entity in the persistence layer.
     * The method saves the provided ConnectorUser object and returns the
     * saved instance, which may contain modifications such as assigned
     * identifiers or updated timestamps.
     *
     * @param user the ConnectorUser entity to be saved; must not be null
     *
     * @return the saved ConnectorUser entity
     */
    ConnectorUser save(ConnectorUser user);

    /**
     * Retrieves a ConnectorUser entity by its unique identifier.
     *
     * <p>This method queries the persistence layer to find and return a ConnectorUser
     * wrapped in an Optional. If no user with the given identifier is found, the method
     * returns an empty Optional.
     *
     * @param id the unique identifier of the ConnectorUser to retrieve; must not be null
     *
     * @return an Optional containing the ConnectorUser if found, or an empty Optional if no user
     *         exists with the given identifier
     */
    Optional<ConnectorUser> findById(Long id);

    /**
     * Retrieves a {@code ConnectorUser} entity by its UUID.
     *
     * <p>This method queries the persistence layer to find and return a {@code ConnectorUser}
     * wrapped in an {@code Optional}. If no user with the given UUID is found, the method
     * returns an empty {@code Optional}.
     *
     * @param identifier the UUID of the {@code ConnectorUser} to retrieve; must not be null
     *
     * @return an {@code Optional} containing the {@code ConnectorUser} if found, or an empty
     *         {@code Optional} if no user exists with the given UUID
     */
    Optional<ConnectorUser> findByUuid(String identifier);

    /**
     * Retrieves a ConnectorUser entity based on the provided username.
     *
     * <p>This method queries the persistence layer to find and return a ConnectorUser
     * wrapped in an Optional. If no user with the given username is found, the method
     * returns an empty Optional.
     *
     * @param username the username of the ConnectorUser to retrieve; must not be null
     *
     * @return an Optional containing the ConnectorUser if found, or an empty Optional if no user
     *         exists with the given username
     */
    Optional<ConnectorUser> findByUsername(String username);

    /**
     * Retrieves a ConnectorUser entity based on the provided email.
     *
     * <p>This method queries the persistence layer to find and return a ConnectorUser
     * wrapped in an Optional. If no user with the given email is found, the method
     * returns an empty Optional.
     *
     * @param email the email address of the ConnectorUser to retrieve; must not be null
     *
     * @return an Optional containing the ConnectorUser if found, or an empty Optional if no user
     *         exists with the given email
     */
    Optional<ConnectorUser> findByEmail(String email);

    /**
     * Retrieves a {@code ConnectorUser} entity based on the provided username and email.
     *
     * <p>This method queries the persistence layer to find and return a {@code ConnectorUser}
     * wrapped in an {@code Optional}. If no user exists with the given username and email,
     * the method returns an empty {@code Optional}.
     *
     * @param username the username of the {@code ConnectorUser} to retrieve; must not be null
     * @param email    the email address of the {@code ConnectorUser} to retrieve; must not be null
     *
     * @return an {@code Optional} containing the {@code ConnectorUser} if found, or an empty
     *         {@code Optional} if no user exists with the given username and email
     */
    Optional<ConnectorUser> findByUsernameAndEmail(String username, String email);

    /**
     * Retrieves a list of all {@code ConnectorUser} entities.
     *
     * <p>This method fetches all the {@code ConnectorUser} records from the persistence layer
     * and returns them as a {@code List}. If no users are found, the method will return an
     * empty {@code List}.
     *
     * @return a {@code List} of all {@code ConnectorUser} entities, or an empty {@code List} if no
     *         users exist
     */
    List<ConnectorUser> findAll();

    /**
     * Deletes the ConnectorUser entity with the specified unique identifier.
     *
     * <p>This method removes the corresponding record from the persistence layer.
     * If no user exists with the given identifier, an exception may be thrown
     * depending on the implementation of the calling service methods.
     *
     * @param identifier the unique identifier of the ConnectorUser to delete; must not be null
     */
    void deleteByUuid(String identifier);


    /**
     * Checks if a {@code ConnectorUser} entity exists with the given UUID.
     *
     * @param uuid the UUID of the {@code ConnectorUser} to check for existence; must not be null
     *
     * @return {@code true} if a {@code ConnectorUser} with the specified UUID exists, {@code false}
     *         otherwise
     */
    boolean existsByUuid(String uuid);

    /**
     * Checks if a {@code ConnectorUser} entity exists with the specified username.
     *
     * @param username the username of the {@code ConnectorUser} to check for existence; must not be
     *                 null
     *
     * @return {@code true} if a {@code ConnectorUser} with the specified username exists,
     *         {@code false} otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a {@code ConnectorUser} entity exists with the specified email.
     *
     * @param email the email address of the {@code ConnectorUser} to check for existence; must not
     *              be null
     *
     * @return {@code true} if a {@code ConnectorUser} with the specified email exists,
     *         {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a {@code ConnectorUser} entity exists with the given email
     * and a UUID that is not equal to the provided UUID.
     *
     * @param email the email address of the {@code ConnectorUser} to check for existence; must not
     *              be null
     * @param uuid  the UUID that should not match the {@code ConnectorUser}'s UUID; must not be
     *              null
     *
     * @return {@code true} if a {@code ConnectorUser} with the specified email exists and has a
     *         different UUID
     *         than the provided UUID, {@code false} otherwise
     */
    boolean existsByEmailAndUuidNot(String email, String uuid);

    /**
     * Checks if a {@code ConnectorUser} entity exists with the specified username
     * and a UUID that is not equal to the provided UUID.
     *
     * @param username the username of the {@code ConnectorUser} to check for existence; must not be
     *                 null
     * @param uuid     the UUID that should not match the {@code ConnectorUser}'s UUID; must not be
     *                 null
     *
     * @return {@code true} if a {@code ConnectorUser} with the specified username exists and has a
     *         different UUID
     *         than the provided UUID, {@code false} otherwise
     */
    boolean existsByUsernameAndUuidNot(String username, String uuid);

}
