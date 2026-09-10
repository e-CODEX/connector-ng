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

import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import jakarta.annotation.Nonnull;

/**
 * Defines methods verifying refresh tokens in the Connector system.
 */
public interface ConnectorVerifyUserRefreshToken {
    /**
     * Verifies the authenticity and validity of a given refresh token within the Connector system.
     * This method checks whether the provided token is valid, not expired, and has not been
     * revoked.
     *
     * @param token the refresh token to be verified
     *
     */
    ConnectorRefreshToken execute(@Nonnull String token);
}
