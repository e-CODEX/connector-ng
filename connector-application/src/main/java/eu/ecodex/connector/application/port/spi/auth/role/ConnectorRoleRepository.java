/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.auth.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing ConnectorUserRole entities.
 * Provides methods for CRUD operations and specific queries related to user roles.
 */
public interface ConnectorRoleRepository {

    /**
     * Persists or updates a ConnectorUserRole entity in the repository.
     * If the entity already exists, it will be updated. Otherwise, a new entity will be created.
     *
     * @param user the ConnectorUserRole entity to be saved or updated
     * @return the saved ConnectorUserRole entity, including any modifications or generated identifiers
     */
    ConnectorUserRole save(ConnectorUserRole user);

    /**
     * Retrieves a ConnectorUserRole entity by its unique UUID.
     *
     * @param id the unique identifier (UUID) of the ConnectorUserRole entity to retrieve
     * @return an Optional containing the ConnectorUserRole entity if found, or an empty Optional if not found
     */
    Optional<ConnectorUserRole> findByUuid(String id);

    /**
     * Searches for a ConnectorUserRole entity by its name.
     *
     * @param name the name of the ConnectorUserRole to search for
     * @return an Optional containing the found ConnectorUserRole if it exists, or an empty Optional if no entity is found
     */
    Optional<ConnectorUserRole> findByName(String name);

    /**
     * Retrieves all ConnectorUserRole entities from the repository.
     *
     * @return a list of all ConnectorUserRole entities
     */
    List<ConnectorUserRole> findAll();

    /**
     * Deletes a user role from the repository based on its unique UUID.
     *
     * @param identifier the unique identifier (UUID) of the user role to be deleted
     */
    void deleteByUuid(String identifier);

    /**
     * Retrieves a set of {@code ConnectorUserRole} entities whose names match any
     * of the provided names.
     *
     * @param names a set of role names to search for
     * @return a set of {@code ConnectorUserRole} entities matching the provided names
     */
    Set<ConnectorUserRole> findByNameIn(Set<String> names);
}
