/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.iam.role;

import eu.ecodex.connector.domain.model.user.ConnectorUserRole;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing ConnectorUserRole entities.
 * Provides methods for CRUD operations and specific queries related to user roles.
 */
public interface ConnectorUserRoleRepository {

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
     * Deletes a ConnectorUserRole entity by its unique identifier.
     * If the entity with the provided identifier exists, it will be removed from the repository.
     * Otherwise, an exception will be thrown indicating that no entity was found.
     *
     * @param identifier the unique identifier of the ConnectorUserRole entity to delete
     * @throws ConnectorUserNotFoundException if no entity is found with the provided identifier
     */
    void deleteByUuid(String identifier);
}
