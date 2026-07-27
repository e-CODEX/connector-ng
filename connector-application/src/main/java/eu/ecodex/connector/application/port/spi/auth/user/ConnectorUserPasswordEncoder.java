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

/**
 * Interface for encoding passwords of {@link ConnectorUser} entities.
 */
public interface ConnectorUserPasswordEncoder {

    /**
     * Encodes the password of the specified {@code ConnectorUser} and returns a new
     * {@code ConnectorUser} instance with the encoded password.
     *
     * @param user the {@code ConnectorUser} whose password needs to be encoded
     *
     * @return a new {@code ConnectorUser} instance with the encoded password
     */
    ConnectorUser encodePassword(ConnectorUser user);

    /**
     * Encodes the provided raw password and returns the encoded version.
     *
     * @param password the raw password to encode
     *
     * @return the encoded password
     */
    String encodePassword(String password);

    /**
     * Verifies if the provided password matches the password of the specified user.
     *
     * @param encodedPassword the {@code ConnectorUser} encoded password to be compared.
     * @param rawPassword     the raw password to be checked against the user's encoded password.
     *
     * @return {@code true} if the raw password matches the user's encoded password, otherwise
     *     {@code false}.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
