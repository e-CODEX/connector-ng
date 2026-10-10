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

import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import jakarta.annotation.Nonnull;

/**
 * This contract permits to log in a connector user into the system.
 */
public interface ConnectorLoginUser {
    /**
     * Executes user login operation.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     *
     * @return the authentication result
     */
    ConnectorUserAuthenticationResult execute(@Nonnull String username, @Nonnull String password);
}
