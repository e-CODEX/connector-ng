/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.auth.login;

import eu.ecodex.connector.application.port.api.auth.login.ConnectorRefreshUserToken;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link ConnectorRefreshToken} entities.
 *
 * <p>Provides methods for performing CRUD operations on refresh tokens, such as
 * finding a token by its value, saving a new token, and deleting an existing token.
 *
 * <p>Responsibilities:
 * - Retrieve a refresh token based on its unique string value.
 * - Persist a refresh token entity in the persistence layer.
 * - Delete a refresh token from the persistence layer.
 *
 * <p>This interface is designed to interact with the underlying data storage
 * and handle persistence operations for {@link ConnectorRefreshToken} entities,
 * ensuring proper data integrity and management.
 */
public interface ConnectorRefreshTokenRepository {
    /**
     * Retrieves a {@link ConnectorRefreshToken} entity based on the provided token value.
     *
     * @param token the unique string value of the refresh token to be retrieved.
     *              This value is used as a key to identify and fetch the corresponding
     *              token entity from the persistence layer.
     *
     * @return an {@link Optional} containing the {@link ConnectorRefreshToken} if it exists;
     *     an empty {@link Optional} if no matching token is found.
     */
    Optional<ConnectorRefreshToken> findByToken(String token);

    /**
     * Persists the provided {@link ConnectorRefreshToken} entity into the persistence layer.
     * Saves the refresh token and returns a {@link ConnectorRefreshUserToken} instance
     * representing the successfully persisted token.
     *
     * @param refreshToken the {@link ConnectorRefreshToken} entity to be stored.
     *                     Contains details about the token such as its unique identifier,
     *                     associated user, expiration timestamp, and creation timestamp.
     *
     * @return a {@link ConnectorRefreshUserToken} instance representing the persisted token.
     */
    ConnectorRefreshToken save(ConnectorRefreshToken refreshToken);

    /**
     * Deletes the specified {@link ConnectorRefreshToken} from the persistence layer.
     * This operation removes the refresh token entity to ensure it is no longer available
     * for authentication or other operations.
     *
     * @param refreshToken the {@link ConnectorRefreshToken} entity to delete.
     *                     It contains information about the token to be removed,
     *                     including its unique identifier and associated details.
     */
    void delete(ConnectorRefreshToken refreshToken);

    /**
     * Retrieves a list of {@link ConnectorRefreshToken} entities based on the provided user UUID
     * and revocation status.
     *
     * @param uuid    the unique identifier of the user whose refresh tokens are to be retrieved.
     *                It is used to filter the tokens associated with the specified user.
     * @param revoked a boolean flag indicating the revocation status of the tokens to be
     *                retrieved.
     *                If true, only revoked tokens will be returned; if false, only non-revoked
     *                tokens
     *                will be returned.
     *
     * @return a list of {@link ConnectorRefreshToken} entities matching the provided user UUID and
     *     revocation status. The list may be empty if no matching tokens are found.
     */
    List<ConnectorRefreshToken> findByUserUuidAndRevoked(String uuid, boolean revoked);

    /**
     * Revokes all refresh tokens associated with the specified user UUID.
     * This operation marks all tokens belonging to the user as invalid,
     * ensuring they cannot be used for authentication or token renewal.
     *
     * @param uuid the unique identifier of the user whose refresh tokens are to be revoked.
     *             It acts as a key to find and mark all associated tokens as revoked.
     *
     * @return the number of refresh tokens that were successfully revoked.
     */
    int revokeByUserUuid(String uuid);

    /**
     * Deletes all refresh tokens associated with the specified user UUID.
     *
     * @param uuid the unique identifier of the user whose refresh tokens are to be revoked.
     *             It acts as a key to find and mark all associated tokens as revoked.
     *
     * @return the number of refresh tokens that were successfully revoked.
     */
    int deleteByUserUuid(String uuid);

    /**
     * Delete all expired refresh tokens before the specified date.
     *
     * @param expiryDate shall be before this date
     *
     * @return the number of refresh tokens that were successfully deleted
     */
    int deleteByExpiryDateBefore(Instant expiryDate);

    /**
     * Delete all revoked refresh tokens before the specified date.
     *
     * @param expiryDate shall be before this date
     *
     * @return the number of refresh tokens that were successfully deleted
     */
    int deleteByRevokedAndExpiryDateBefore(Instant expiryDate);
}
