/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.auth.accesstoken;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;

/**
 * Provider (port) responsible for creating and validating the authentication tokens (for example
 * access tokens) of users of the connector system.
 */
public interface ConnectorAuthenticationTokenProvider {
    /**
     * Generates an authentication token based on the given user details.
     *
     * @param user the user details from which the token will be generated. It should provide the
     *             necessary information such as username and authorities required for token
     *             creation.
     *
     * @return the generated authentication token as a {@code String}.
     */
    String generateAccessToken(@Nonnull ConnectorUser user);

    /**
     * Retrieves the duration for which an access token remains valid. This duration determines the
     * token's expiration time, after which it will no longer be accepted for authentication
     * purposes.
     *
     * @return the expiration duration of the access token in seconds.
     */
    Duration getAccessTokenExpiresIn();

    /**
     * Retrieves the duration in seconds for which a refresh token remains valid. This duration
     * determines the token's expiration time, after which it will no longer be accepted for
     * authentication or token renewal purposes.
     *
     * @return the expiration duration of the refresh token in seconds.
     */
    Duration getRefreshTokenExpiresIn();

    /**
     * Checks if the given JWT token has expired.
     *
     * @param token accessToken to check
     *
     * @return true if expired, false otherwise
     */
    boolean isAccessTokenExpired(@Nonnull String token);

    /**
     * Get the expiration date of the access token.
     *
     * @param token the token to check
     *
     * @return the expiration date of the access token
     */
    Instant getAccessTokenExpirationDate(@Nonnull String token);

    /**
     * Retrieve the username from the token.
     *
     * @param token the token to check
     *
     * @return the username
     */
    String getUsernameFromToken(@Nonnull String token);

    /**
     * Get refresh tokens cleanup cron expression.
     *
     * @return cron expression
     */
    String getRefreshTokenCleanupCron();
}
