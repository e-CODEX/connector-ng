/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.user;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a data structure for a user in the Connector system.
 *
 * <p>This class provides information about the user, such as an identifier, username,
 * password, email, and roles, and includes metadata such as enabled status, creation
 * time, and last updated time.
 * It also encapsulates behavior for content comparison
 * and builder for creating immutable instances of the class.
 *
 */
public record ConnectorUser(
    String uuid,
    String username,
    String password,
    String email,
    Boolean enabled,
    Set<ConnectorRole> roles,
    Instant createdAt,
    Instant updatedAt
) {

    public static final String DEFAULT_ADMIN_USER_NAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "123456";

    /**
     * Create default administrator user.
     *
     * @return Default administrator
     */
    public static ConnectorUser defaultAdminUser() {
        return ConnectorUser
            .builder()
            .username(DEFAULT_ADMIN_USER_NAME)
            .password(DEFAULT_ADMIN_PASSWORD)
            .enabled(true)
            .roles(Set.of(ConnectorRole.defaultAdminRole()))
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Check if the user is the default admin.
     *
     * @return true if the user is the default admin.
     */
    public boolean isDefaultAdmin() {
        return DEFAULT_ADMIN_USER_NAME.equals(username) && roles != null && roles
            .stream()
            .anyMatch(ConnectorRole::isDefaultAdminRole);
    }

    /**
     * Add a new role to the current user.
     *
     * @param role new role to add
     *
     * @return updated user
     */
    public ConnectorUser addRole(ConnectorRole role) {
        var updatedRoles = roles == null
            ? new HashSet<ConnectorRole>()
            : new HashSet<>(roles);

        boolean added = updatedRoles.add(role);
        return added ? toBuilder().roles(updatedRoles).build() : this;
    }

    /**
     * Remove a role from the current user.
     *
     * @param role role to remove
     *
     * @return updated user
     */
    public ConnectorUser removeRole(ConnectorRole role) {
        if (roles == null) {
            return this;
        }
        var updatedRoles = new HashSet<>(roles);
        boolean removed = updatedRoles.remove(role);

        return removed ? toBuilder().roles(updatedRoles).build() : this;
    }

    /**
     * Creates a new {@code Builder} instance pre-populated with the current state of the
     * {@code ConnectorUser} object.
     *
     * @return a {@code Builder} instance containing the fields of the current {@code ConnectorUser}
     *     object.
     */
    public Builder toBuilder() {
        return new Builder()
            .uuid(this.uuid)
            .username(this.username)
            .password(this.password)
            .email(this.email)
            .enabled(this.enabled)
            .roles(this.roles)
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt);

    }

    /**
     * Builder class for constructing instances of {@code ConnectorUser}.
     *
     * <p>This builder pattern enables the creation of immutable {@code ConnectorUser}
     * objects by providing methods to set various fields incrementally and
     * eventually constructing a fully populated instance.
     */
    public static class Builder {
        String uuid;
        String username;
        String password;
        String email;
        Boolean enabled;
        Set<ConnectorRole> roles;
        Instant createdAt;
        Instant updatedAt;

        private Builder() {

        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder roles(Set<ConnectorRole> roles) {
            this.roles = roles;
            return this;
        }

        public ConnectorUser build() {
            return new ConnectorUser(uuid, username, password, email, enabled, roles, createdAt,
                updatedAt);
        }
    }
}
