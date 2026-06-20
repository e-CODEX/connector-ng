/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Defines the contract for managing and querying processing modes associated with a specific
 * business domain uuid within the connector system.
 */
public interface ConnectorProcessingModeRepository {
    /**
     * Persists a given {@link ConnectorProcessingMode} for a specific
     * {@link ConnectorBusinessDomain}.
     *
     * @param processingMode           the processing mode to be saved; must not be null.
     * @param businessDomainIdentifier the business domain identifier associated with the processing
     *                                 mode; must not be null.
     *
     * @return the persisted {@link ConnectorProcessingMode}, which may include additional metadata
     *         such as timestamps or identifiers.
     */
    ConnectorProcessingMode save(
            @Nonnull ConnectorProcessingMode processingMode,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier
    );

    /**
     * Retrieves a {@link ConnectorProcessingMode} identified by its unique UUID.
     *
     * @param uuid the unique identifier of the {@link ConnectorProcessingMode} to retrieve; must
     *             not be null.
     *
     * @return the {@link ConnectorProcessingMode} matching the specified UUID, or null if no such
     *         processing mode exists.
     */
    ConnectorProcessingMode findByUuid(@Nonnull String uuid);

    /**
     * Retrieves a {@link ConnectorProcessingMode} associated with the specified
     * {@link ConnectorBusinessDomainIdentifier}.
     *
     * @param identifier the {@link ConnectorBusinessDomainIdentifier} representing the business
     *                   domain for which the processing mode is to be retrieved; must not be null.
     *
     * @return the {@link ConnectorProcessingMode} associated with the specified identifier, or null
     *         if no matching processing mode exists.
     */
    ConnectorProcessingMode findByBusinessDomainIdentifier(
            @Nonnull ConnectorBusinessDomainIdentifier identifier);

    /**
     * Retrieves all available {@link ConnectorProcessingMode} entries.
     *
     * @return a list of {@link ConnectorProcessingMode} instances representing all defined
     *         processing modes, or an empty list if no processing modes are available.
     */
    List<ConnectorProcessingMode> findAll();
}
