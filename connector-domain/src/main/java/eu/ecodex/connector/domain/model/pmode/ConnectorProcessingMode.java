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
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import jakarta.annotation.Nonnull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;

/**
 * The ConnectorProcessingMode represents a set of PMode configurations for a Connector.
 *
 * @param uuid                     The UUID of the PMode.
 * @param description              The description of the PMode.
 * @param content                  The content of the PMode.
 * @param businessDomain The business domain of the PMode.
 * @param truststore               The truststore of the PMode.
 * @param homeParty                The home party of the PMode.
 * @param parties                  The parties of the PMode.
 * @param services                 The services of the PMode.
 * @param actions                  The actions of the PMode.
 * @param createdAt                The creation date of the PMode.
 * @param updatedAt                The last update date of the PMode.
 */
@Builder
public record ConnectorProcessingMode(
        String uuid,
        @Nonnull String description,
        @Nonnull byte[] content,
        @Nonnull ConnectorBusinessDomain businessDomain,
        @Nonnull ConnectorKeystore truststore,
        @Nonnull ConnectorParty homeParty,
        @Nonnull Set<ConnectorParty> parties,
        @Nonnull Set<ConnectorService> services,
        @Nonnull Set<ConnectorAction> actions,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{uuid=%s, description=%s, homeParty=%s, truststore=%s}",
                uuid, description, homeParty, truststore
        );
    }
}
