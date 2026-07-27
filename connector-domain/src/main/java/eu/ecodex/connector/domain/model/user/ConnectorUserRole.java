package eu.ecodex.connector.domain.model.user;

import java.time.Instant;

public record ConnectorUserRole(Long identifier,
                                String name,
                                Instant createdAt,
                                Instant updatedAt) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .identifier(this.identifier)
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);

    }

    public static class Builder {
        Long identifier;
        String name;
        Instant createdAt;
        Instant updatedAt;

        private Builder() {

        }

        public Builder identifier(Long identifier) {
            this.identifier = identifier;
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
            return new ConnectorUserRole(identifier, name, createdAt, updatedAt);
        }
    }

}
