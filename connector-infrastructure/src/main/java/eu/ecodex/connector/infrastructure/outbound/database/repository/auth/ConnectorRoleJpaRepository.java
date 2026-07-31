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

import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations and custom queries on
 * {@link ConnectorRoleEntity} objects within the database.
 *
 * <p>
 * Extends the {@link JpaRepository} to inherit common JPA-based data access methods.
 * Provides additional methods for retrieving entities based on specific attributes.
 *
 * <p>
 * Key functionalities:
 * - Enables retrieval of roles by their unique name.
 * - Supports fetching roles based on their UUID.
 * - Allows finding multiple roles by a set of names.
 *
 * <p>
 * Methods:
 * - {@code findByName}: Finds a role by its unique name.
 * - {@code findByUuid}: Finds a role by its UUID.
 * - {@code findByNameIn}: Retrieves a set of roles matching the provided set of names.
 */
public interface ConnectorRoleJpaRepository
        extends JpaRepository<ConnectorRoleEntity, Long> {
    /**
     * Finds a role entity by its unique name.
     *
     * @param username the unique name of the role to be retrieved
     *
     * @return an {@code Optional} containing the {@code ConnectorRoleEntity} if found,
     *         or empty if not found
     */
    Optional<ConnectorRoleEntity> findByName(String username);

    /**
     * Finds a role entity by its unique UUID.
     *
     * @param uuid the unique UUID of the role to be retrieved
     *
     * @return an {@code Optional} containing the {@code ConnectorRoleEntity} if found,
     *         or an empty {@code Optional} if no role with the given UUID exists
     */
    Optional<ConnectorRoleEntity> findByUuid(String uuid);

    /**
     * Retrieves a set of {@link ConnectorRoleEntity} objects that have names matching the provided
     * set of names.
     *
     * @param names a set of role names to search for
     *
     * @return a set of {@link ConnectorRoleEntity} objects whose names are present in the provided
     *         set, or an empty set if no matching roles are found
     */
    Set<ConnectorRoleEntity> findByNameIn(Set<String> names);
}
