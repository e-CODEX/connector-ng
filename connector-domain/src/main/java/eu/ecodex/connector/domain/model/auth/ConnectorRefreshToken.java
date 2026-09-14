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

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Builder;

/**
 * Represents a refresh token used for managing user authentication sessions.
 *
 * <p>This record encapsulates essential information about a refresh token:
 * - A unique identifier (`uuid`) for the token instance.
 * - The user (`ConnectorUser`) associated with the refresh token.
 * - The expiration timestamp (`expiresAt`) indicating when the token becomes invalid.
 * - The creation timestamp (`createdAt`) indicating when the token was issued.
 *
 * <p>The `RefreshToken` class serves as an immutable data structure to securely and
 * consistently handle refresh token details within the system.
 */
@Builder(toBuilder = true)
public record ConnectorRefreshToken(
    @NotBlank String token,
    @NotNull ConnectorUser user,
    Instant expiresAt,
    Instant createdAt,
    boolean revoked
) {

}
