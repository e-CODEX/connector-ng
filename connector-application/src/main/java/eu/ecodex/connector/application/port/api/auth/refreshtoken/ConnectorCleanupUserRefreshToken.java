/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.refreshtoken;

/**
 * Use case for cleaning up the refresh tokens of users in the connector system.
 */
public interface ConnectorCleanupUserRefreshToken {
    /**
     * Deletes the refresh tokens that are no longer needed, such as expired or revoked tokens.
     *
     * <p>Removing these tokens keeps the token store small and improves the security and integrity
     * of the authentication system. The method takes no argument and processes all users.
     */
    void execute();
}
