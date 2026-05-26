/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.message;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Repository responsible for persisting {@link ConnectorMessageEvidence} instances.
 *
 * <p>This component abstracts the storage mechanism used to store evidence generated during the
 * processing of connector messages. Implementations may persist the evidence in a database, event
 * store, or any other persistence layer.
 */
public interface ConnectorMessageEvidenceRepository {
    /**
     * Persists the provided {@link ConnectorMessageEvidence} and associates it with the given
     * message identifier.
     *
     * @param evidence          the evidence object describing the outcome of message processing
     * @param messageIdentifier the unique identifier of the connector message the evidence belongs
     *                          to
     *
     * @return the persisted {@link ConnectorMessageEvidence}, potentially enriched with
     *         persistence-related information (e.g., generated identifiers)
     */
    ConnectorMessageEvidence save(
            @Nonnull ConnectorMessageEvidence evidence, @Nonnull String messageIdentifier);
}
