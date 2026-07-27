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
public record ConnectorRefreshToken(
        String token,
        ConnectorUser user,
        Instant expiresAt,
        Instant createdAt,
        boolean revoked
) {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} instance pre-populated with the current state
     * of this {@link ConnectorRefreshToken} instance.
     *
     * @return a {@link Builder} instance initialized with the field values of this
     *         {@link ConnectorRefreshToken}.
     */
    public Builder toBuilder() {
        return new Builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .revoked(revoked);
    }

    /**
     * A builder class for constructing instances of {@link ConnectorRefreshToken}.
     *
     * <p>This builder provides a fluent interface for configuring and creating instances
     * of the {@link ConnectorRefreshToken} class. It allows setting up various
     * properties such as the unique identifier, associated user, expiration timestamp,
     * creation timestamp, and revocation status.
     *
     * <p>The builder ensures that a properly configured {@link ConnectorRefreshToken}
     * instance can be created with the desired state.
     */
    public static class Builder {
        private String token;
        private ConnectorUser user;
        private boolean revoked;
        private Instant expiresAt;
        private Instant createdAt;

        public Builder token(String token) {
            this.token = token;
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
            return new ConnectorRefreshToken(token, user, expiresAt, createdAt, revoked);
        }
    }
}
