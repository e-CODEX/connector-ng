/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Represents the response resulting from a successful user login operation.
 * This record encapsulates the authentication token details that are generated
 * and returned to the client upon successful authentication.
 *
 * <p>Fields:
 * - accessToken: The token issued to the authenticated user, used for
 * authorizing subsequent requests.
 * - expiresIn: The duration (in seconds) for which the token remains valid.
 * - refreshToken: An optional refresh token that can be used to obtain new
 *
 * <p>This class is immutable and is designed to securely transport token-related
 * data to ensure a proper authentication and authorization workflow within the
 * system.
 */
@Builder
public record ConnectorUserAuthenticationResult(
        @NotBlank String accessToken,
        @NotBlank String refreshToken,
        long expiresIn,
        long refreshExpiresIn
) {
}
