/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.ConnectorPublishable;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents an evidence message triggered by a connector as part of its interactions with the
 * backend system or gateway.
 *
 * @param identifier            A unique identifier for this evidence message.
 * @param backendName           The name of the associated backend system.
 * @param direction             The {@link ConnectorMessageDirection} denoting the flow of the
 *                              message.
 * @param evidenceType          The {@link ConnectorEvidenceType} specifying the type of evidence
 *                              being reported.
 * @param referenceToIdentifier An optional reference to another identifier for correlation
 *                              purposes.
 */
@Builder(toBuilder = true)
public record ConnectorTriggeredEvidenceMessage(
    @NonNull String identifier,
    @NonNull String backendName,
    @NonNull ConnectorMessageDirection direction,
    @NonNull ConnectorEvidenceType evidenceType,
    @Nullable String referenceToIdentifier
) implements Serializable, ConnectorPublishable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
            "{identifier=%s, referenceToIdentifier=%s, backendName=%s, direction=%s}",
            identifier, referenceToIdentifier, backendName, direction
        );
    }
}
