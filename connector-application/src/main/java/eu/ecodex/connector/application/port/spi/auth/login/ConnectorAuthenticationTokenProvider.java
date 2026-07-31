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

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Duration;

/**
 * A provider interface for managing authentication tokens used in the
 * Connector application. This interface defines methods for creating,
 * validating, and parsing authentication tokens, as well as extracting
 * user-related information.
 *
 * <p>
 * Responsibilities:
 * - Generate authentication tokens based on user details.
 * - Validate tokens based on predefined criteria, such as matching user
 * details and expiration.
 * - Extract the username encoded within a token.
 *
 * <p>
 * Thread-safety:
 * Implementations of this interface must ensure appropriate thread-safety
 * for use in concurrent environments.
 *
 * <p>
 * Expected Implementations:
 * Classes implementing this interface should define the token generation
 * and validation mechanisms appropriate for the application's chosen
 * authentication and authorization framework, such as JSON Web Tokens (JWT).
 */
public interface ConnectorAuthenticationTokenProvider {
    /**
     * Generates an authentication token based on the given user details.
     *
     * @param user the user details from which the token will be generated.
     *             It should provide the necessary information such as username
     *             and authorities required for token creation.
     * @return the generated authentication token as a {@code String}.
     */
    String generateToken(ConnectorUser user);

    /**
     * Retrieves the duration in seconds for which an access token remains valid.
     * This duration determines the token's expiration time, after which it will
     * no longer be accepted for authentication purposes.
     *
     * @return the expiration duration of the access token in seconds.
     */
    long accessTokenExpiresInSeconds();

    /**
     * Retrieves the duration in seconds for which a refresh token remains valid.
     * This duration determines the token's expiration time, after which it will
     * no longer be accepted for authentication or token renewal purposes.
     *
     * @return the expiration duration of the refresh token in seconds.
     */
    Duration refreshTokenExpires();

}
