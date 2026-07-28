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

/**
 * Represents a role assigned to a user in the Connector system.
 * <p>
 * This record provides metadata about the role, including a unique identifier,
 * the role's name, and its creation and last updated timestamps.
 * It is an immutable data structure designed to store and share
 * role-specific information across the Connector system.
 * <p>
 * The class supports the builder pattern, offering a nested {@code Builder} class
 * that provides a flexible API for incrementally constructing instances of {@code ConnectorUserRole}.
 * Additionally, it includes a method to create a pre-populated builder
 * from an existing object.
 * <p>
 * This record is used as a field in other classes, such as {@code ConnectorUser},
 * to represent the roles associated with a user.
 */
public record ConnectorUserRole(String uuid,
                                String name,
                                Instant createdAt,
                                Instant updatedAt) {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@code Builder} instance pre-populated with the current state
     * of this {@code ConnectorUserRole} instance.
     *
     * @return a {@code Builder} instance containing the fields of the current
     *         {@code ConnectorUserRole} object.
     */
    public Builder toBuilder() {
        return new Builder()
                .uuid(this.uuid)
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);

    }

    /**
     * Builder class for constructing instances of {@code ConnectorUserRole}.
     * <p>
     * This builder implements a fluent API for incrementally setting the properties
     * of a {@code ConnectorUserRole} object and constructing a new immutable instance.
     * The builder is used to ensure that the resulting object is created in a
     * controlled, consistent manner.
     * <p>
     * Various methods are provided to set the individual fields of the builder.
     * Each setter method returns the builder itself, enabling method chaining.
     */
    public static class Builder {
        String uuid;
        String name;
        Instant createdAt;
        Instant updatedAt;

        private Builder() {

        }

        public Builder uuid(String identifier) {
            this.uuid = identifier;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public ConnectorUserRole build() {
            return new ConnectorUserRole(uuid, name, createdAt, updatedAt);
        }
    }

}
