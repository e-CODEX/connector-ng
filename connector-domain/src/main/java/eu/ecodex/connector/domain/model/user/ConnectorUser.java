package eu.ecodex.connector.domain.model.user;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public record ConnectorUser(
        Long identifier,
        String username,
        String password,
        String email,
        Boolean enabled,
        Set<ConnectorUserRole> roles,
        Instant createdAt,
        Instant updatedAt
) {

    public boolean hasSameContent(ConnectorUser connectorUser) {
        return Objects.equals(this.identifier, connectorUser.identifier())
                && Objects.equals(this.username, connectorUser.username())
                && Objects.equals(this.password, connectorUser.password())
                && Objects.equals(this.email, connectorUser.email())
                && Objects.equals(this.enabled, connectorUser.enabled());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .identifier(this.identifier)
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .enabled(this.enabled)
                .roles(this.roles)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);

    }

    public static class Builder {
        Long identifier;
        String username;
        String password;
        String email;
        Boolean enabled;
        Set<ConnectorUserRole> roles;
        Instant createdAt;
        Instant updatedAt;

        private Builder() {

        }

        public Builder identifier(Long identifier) {
            this.identifier = identifier;
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

        public Builder roles(Set<ConnectorUserRole> roles) {
            this.roles = roles;
            return this;
        }

        public ConnectorUser build() {
            return new ConnectorUser(identifier, username, password, email, enabled, roles, createdAt,
                    updatedAt);
        }
    }
}
