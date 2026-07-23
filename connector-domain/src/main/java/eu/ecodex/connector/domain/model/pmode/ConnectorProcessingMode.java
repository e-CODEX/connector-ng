/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;

/**
 * The ConnectorProcessingMode represents a set of PMode configurations for a Connector.
 *
 * @param uuid           The UUID of the PMode.
 * @param description    The description of the PMode.
 * @param content        The businessContent of the PMode.
 * @param filename       The filename of the PMode.
 * @param businessDomain The business domain of the PMode.
 * @param truststore     The truststore of the PMode.
 * @param parties        The parties of the PMode.
 * @param services       The services of the PMode.
 * @param actions        The actions of the PMode.
 * @param createdAt      The creation date of the PMode.
 * @param updatedAt      The last update date of the PMode.
 */
@Builder(toBuilder = true)
public record ConnectorProcessingMode(
    String uuid,
    @Nullable String description,
    @Nonnull String content,
    @Nonnull String filename,
    @Nullable ConnectorBusinessDomain businessDomain,
    ConnectorTruststore truststore,
    @Nullable Set<ConnectorParty> parties,
    @Nullable Set<ConnectorService> services,
    @Nullable Set<ConnectorAction> actions,
    Instant createdAt,
    Instant updatedAt
) implements Serializable {
    /**
     * Constructor.
     */
    public ConnectorProcessingMode {
        parties = parties == null ? Set.of() : parties;
        services = services == null ? Set.of() : services;
        actions = actions == null ? Set.of() : actions;
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format(
            "{uuid=%s, description=%s}", uuid, description
        );
    }
}
