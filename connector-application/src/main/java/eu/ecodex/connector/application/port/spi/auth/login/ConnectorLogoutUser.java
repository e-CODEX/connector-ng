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

import jakarta.annotation.Nonnull;

/**
 * Interface for handling user logout functionality in the connector system.
 */
public interface ConnectorLogoutUser {
    /**
     * Executes the logout process for a specific user.
     * This method handles operations related to invalidating the refresh token
     * associated with the given user to ensure secure termination of the user's session.
     *
     * @param userId       the identifier of the user initiating the logout
     * @param refreshToken the refresh token to be invalidated
     */
    void execute(@Nonnull String userId, @Nonnull String refreshToken);
}
