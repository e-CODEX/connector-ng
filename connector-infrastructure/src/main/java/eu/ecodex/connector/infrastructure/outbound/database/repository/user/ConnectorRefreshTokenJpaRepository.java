/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.user;

import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRefreshTokenEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing and accessing {@link ConnectorRefreshTokenEntity} entities in the database.
 * This interface extends {@link JpaRepository}, providing methods for CRUD operations and custom queries.
 * <p>
 * The {@link ConnectorRefreshTokenEntity} represents refresh tokens used for authentication
 * and authorization in the system.
 * <p>
 * Key Responsibilities:
 * - Retrieve a refresh token by its unique token value using {@link #findByToken(String)}.
 * <p>
 * Methods:
 * - {@link #findByToken(String)}: Fetches an optional refresh token entity based on the token string.
 * <p>
 * Dependencies:
 * - Utilizes {@link ConnectorRefreshTokenEntity}, which contains details like the token, expiration, and revocation status.
 * - Works within the persistence context provided by Spring Data JPA.
 */
public interface ConnectorRefreshTokenJpaRepository extends JpaRepository<ConnectorRefreshTokenEntity, Long> {

    /**
     * Retrieves a {@link ConnectorRefreshTokenEntity} instance matching the specified token.
     * This method is used to look up refresh token entities in the database by their unique token value.
     *
     * @param token the unique token string used to identify the {@link ConnectorRefreshTokenEntity}
     * @return an {@link Optional} containing the {@link ConnectorRefreshTokenEntity} if found, or an empty {@link Optional} if no entity matches the provided token
     */
    Optional<ConnectorRefreshTokenEntity> findByToken(String token);

}
