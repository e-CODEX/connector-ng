/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.login;

/**
 * Represents a request to refresh a connector's access token using a provided refresh token.
 * This request is typically used in authentication flows to obtain a new access token
 * without requiring the user to re-authenticate.
 *
 * @param refreshToken The token used to refresh the authentication state.
 */
public record ConnectorRefreshRequest(String refreshToken) {
}
