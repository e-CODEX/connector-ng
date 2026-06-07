/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.evidence;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

/**
 * The Connector Evidence internally represents the evidences for a message.
 *
 * @param uuid                     The UUID of the evidence.
 * @param type                     The type of the evidence.
 * @param content                  The content of the evidence.
 * @param createdAt                The creation date of the evidence.
 * @param updatedAt                The last update date of the evidence.
 * @param deliveredToLinkPartnerAt The timestamp indicating when the evidence was successfully
 *                                 delivered to the gateway or to the national system.
 */
@Builder(toBuilder = true)
public record ConnectorMessageEvidence(
        @Nullable String uuid,
        @Nonnull ConnectorEvidenceType type,
        @Nullable byte[] content,
        @Nullable Instant createdAt,
        @Nullable Instant updatedAt,
        @Nullable Instant deliveredToLinkPartnerAt
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{uuid=%s, type=%s}", uuid, type);
    }
}
