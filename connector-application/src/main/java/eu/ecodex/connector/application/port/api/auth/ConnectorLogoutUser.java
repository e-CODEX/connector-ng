/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth;

import jakarta.annotation.Nonnull;

/**
 * This contract permits to log out a connector user from the system.
 */
public interface ConnectorLogoutUser {
    /**
     * Execute user logout operation.
     *
     * @param uuid         the identifier of the user initiating the logout
     * @param refreshToken the refresh token to be invalidated
     */
    void execute(@Nonnull String uuid, @Nonnull String refreshToken);
}
