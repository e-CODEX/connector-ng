/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.token;

/**
 * Interface for cleaning up connector refresh tokens.
 */
public interface ConnectorCleanupUserRefreshToken {
    /**
     * Executes the cleanup operation for connector refresh tokens.
     * This method is responsible for performing cleanup tasks related to
     * deleting refresh tokens in the connector system.
     * It ensures that outdated or unused tokens are appropriately handled,
     * enhancing security and maintaining the integrity of the authentication system.
     */
    void execute();
}
