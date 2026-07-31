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

import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRefreshTokenEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing and accessing {@link ConnectorRefreshTokenEntity} entities
 * in the database.
 * This interface extends {@link JpaRepository}, providing methods for CRUD operations and custom
 * queries.
 *
 * <p>
 * The {@link ConnectorRefreshTokenEntity} represents refresh tokens used for authentication
 * and authorization in the system.
 *
 * <p>
 * Key Responsibilities:
 * - Retrieve a refresh token by its unique token value using {@link #findByToken(String)}.
 *
 * <p>
 * Methods:
 * - {@link #findByToken(String)}: Fetches an optional refresh token entity based on the token
 * string.
 *
 * <p>
 * Dependencies:
 * - Utilizes {@link ConnectorRefreshTokenEntity}, which contains details like the token,
 * expiration, and revocation status.
 * - Works within the persistence context provided by Spring Data JPA.
 */
public interface ConnectorRefreshTokenJpaRepository
        extends JpaRepository<ConnectorRefreshTokenEntity, Long> {

    /**
     * Retrieves a {@link ConnectorRefreshTokenEntity} instance matching the specified token.
     * This method is used to look up refresh token entities in the database by their unique token
     * value.
     *
     * @param token the unique token string used to identify the {@link ConnectorRefreshTokenEntity}
     *
     * @return an {@link Optional} containing the {@link ConnectorRefreshTokenEntity} if found, or
     *         an empty {@link Optional} if no entity matches the provided token
     */
    Optional<ConnectorRefreshTokenEntity> findByToken(String token);

    /**
     * Retrieves a list of {@link ConnectorRefreshTokenEntity} instances based on the specified
     * user's unique identifier
     * (UUID) and revocation status.
     *
     * @param userUuid the unique identifier (UUID) of the user whose refresh token entities are to
     *                 be retrieved
     * @param revoked  the revocation status used to filter the returned refresh token entities
     *
     * @return a list of {@link ConnectorRefreshTokenEntity} instances matching the provided user
     *         UUID and revocation status;
     *         if no entities match, an empty list is returned
     */
    List<ConnectorRefreshTokenEntity> findByUser_UuidAndRevoked(String userUuid, boolean revoked);

    @Modifying
    @Transactional
    @Query("""
            update ConnectorRefreshTokenEntity rt
               set rt.revoked = true
             where rt.user.uuid = :userUuid
               and rt.revoked = false
            """)
    int revokeAllByUserUuid(@Param("userUuid") String userUuid);
}
