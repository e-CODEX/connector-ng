/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.keystore;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

/**
 * Represents a keystore used by the Domibus connector.
 *
 * @param uuid        The UUID of the keystore.
 * @param content     The content of the keystore.
 * @param password    The password of the keystore (plain text.
 * @param description The description of the keystore.
 * @param type        The type of the keystore.
 * @param filename    The filename of the keystore.
 * @param createdAt   The creation date of the keystore.
 * @param updatedAt   The last update date of the keystore.
 */
@Builder
public record ConnectorKeystore(
        @NotBlank String uuid,
        @Nonnull byte[] content,
        @NotBlank String password,
        @NotBlank String description,
        @Nonnull ConnectorKeystoreType type,
        @Nonnull String filename,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
    @Override
    public @Nonnull String toString() {
        return String.format("{uuid=%s, type=%s, description=%s}", uuid, type, description);
    }
}
