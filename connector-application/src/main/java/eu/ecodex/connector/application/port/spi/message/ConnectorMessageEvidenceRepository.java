/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.message;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Repository (port) for persisting and querying {@link ConnectorMessageEvidence} objects.
 */
public interface ConnectorMessageEvidenceRepository {
    /**
     * Persists the given evidence and associates it with the given message.
     *
     * @param evidence          the evidence describing the outcome of the message processing (must
     *                          not be null)
     * @param messageIdentifier the unique identifier of the connector message the evidence belongs
     *                          to (must not be null)
     *
     * @return the persisted {@link ConnectorMessageEvidence}, potentially enriched with
     *     persistence-related information (for example generated identifiers)
     */
    ConnectorMessageEvidence save(
        @Nonnull ConnectorMessageEvidence evidence,
        @Nonnull String messageIdentifier);

    /**
     * Retrieves an evidence by its unique UUID.
     *
     * @param uuid the unique identifier of the evidence to retrieve (must not be null)
     *
     * @return the {@link ConnectorMessageEvidence} matching the given UUID, or {@code null} if no
     *     such evidence exists
     */
    ConnectorMessageEvidence findByUuid(@Nonnull String uuid);

    /**
     * Records that the evidence identified by the given UUID has been delivered to the link
     * partner, by setting its delivery timestamp.
     *
     * @param uuid the unique identifier of the evidence that has been delivered to the link partner
     *             (must not be null)
     */
    void setDeliveredToLinkPartnerAt(@Nonnull String uuid);
}
