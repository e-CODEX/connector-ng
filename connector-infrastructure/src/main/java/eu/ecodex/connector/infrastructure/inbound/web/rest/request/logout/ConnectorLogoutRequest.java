/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.logout;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Represents a request to log out a user of the connector system.
 *
 * @param refreshToken the refresh token to invalidate (must not be null)
 */
@Builder
public record ConnectorLogoutRequest(
    @NotBlank(message = "The [refreshToken] must not be blank")
    String refreshToken
) {
}
