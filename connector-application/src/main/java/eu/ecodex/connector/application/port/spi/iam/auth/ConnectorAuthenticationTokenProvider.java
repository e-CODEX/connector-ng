/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.iam.auth;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A provider interface for managing authentication tokens used in the
 * Connector application. This interface defines methods for creating,
 * validating, and parsing authentication tokens, as well as extracting
 * user-related information.
 * <p>
 * Responsibilities:
 * - Generate authentication tokens based on user details.
 * - Validate tokens based on predefined criteria, such as matching user
 *   details and expiration.
 * - Extract the username encoded within a token.
 * - Extract user authorities from a token for role-based access control.
 * <p>
 * Thread-safety:
 * Implementations of this interface must ensure appropriate thread-safety
 * for use in concurrent environments.
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
    String generateToken(UserDetails user);

    /**
     * Extracts a list of granted authorities encoded within the specified token.
     * The authorities represent the roles or permissions assigned to the user.
     *
     * @param token the authentication token from which the authorities will be extracted.
     *              It is expected to contain the authority information within a specific
     *              claim, such as "roles".
     * @return a list of granted authorities extracted from the token. If no authority
     *         information is present, an empty list is returned.
     */
    List<? extends GrantedAuthority> extractAuthorities(String token);

    /**
     * Validates the specified authentication token based on the provided user details.
     * The token is considered valid if it matches the user's username and has not expired.
     *
     * @param token the authentication token to validate. It is expected to represent
     *              information such as the username and expiration date.
     * @param user the user details against which the token will be verified. It contains
     *             the username that should match the one encoded in the token.
     * @return {@code true} if the token is valid and satisfies the expected criteria;
     *         {@code false} otherwise.
     */
    boolean isValidToken(String token, UserDetails user);

    /**
     * Extracts the username encoded within the given authentication token.
     *
     * @param token the authentication token containing encoded user information.
     *              It must represent a valid and correctly formatted token.
     * @return the username extracted from the specified token. If the token is
     *         invalid or does not contain a username, returns {@code null} or a
     *         default value based on the implementation.
     */
    String extractUsername(String token);
}
