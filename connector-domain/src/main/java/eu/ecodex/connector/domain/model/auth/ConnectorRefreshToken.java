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
import java.time.Instant;

/**
 * Represents a refresh token used for managing user authentication sessions.
 * <p>
 * This record encapsulates essential information about a refresh token:
 * - A unique identifier (`uuid`) for the token instance.
 * - The user (`ConnectorUser`) associated with the refresh token.
 * - The expiration timestamp (`expiresAt`) indicating when the token becomes invalid.
 * - The creation timestamp (`createdAt`) indicating when the token was issued.
 * <p>
 * The `RefreshToken` class serves as an immutable data structure to securely and
 * consistently handle refresh token details within the system.
 */
public record ConnectorRefreshToken(
        String uuid,
        ConnectorUser user,
        Instant expiresAt,
        Instant createdAt,
        boolean revoked
) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .uuid(uuid)
                .user(user)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .revoked(revoked);
    }

    public static class Builder {
        private String uuid;
        private ConnectorUser user;
        private boolean revoked;
        private Instant expiresAt;
        private Instant createdAt;

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder user(ConnectorUser user) {
            this.user = user;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public ConnectorRefreshToken build() {
            return new ConnectorRefreshToken(uuid, user, expiresAt, createdAt, revoked);
        }
    }
}
