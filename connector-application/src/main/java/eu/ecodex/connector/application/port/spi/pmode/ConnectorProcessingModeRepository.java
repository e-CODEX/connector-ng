/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for persisting and querying {@link ConnectorProcessingMode} objects.
 *
 * <p>A processing mode is associated with a business domain, which is identified by a
 * {@link ConnectorBusinessDomainIdentifier}.
 */
public interface ConnectorProcessingModeRepository {
    /**
     * Persists the given processing mode for the given business domain.
     *
     * @param processingMode           the processing mode to save (must not be null)
     * @param businessDomainIdentifier the identifier of the business domain the processing mode is
     *                                 associated with (must not be null)
     *
     * @return the persisted {@link ConnectorProcessingMode}, which may include additional metadata
     *     such as timestamps or identifiers
     */
    ConnectorProcessingMode save(
        @Nonnull ConnectorProcessingMode processingMode,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier
    );

    /**
     * Updates the truststore of the processing mode identified by the given UUID.
     *
     * @param pmodeUuid  the unique identifier of the processing mode whose truststore is updated
     *                   (must not be null)
     * @param truststore the new {@link ConnectorTruststore} to associate with the processing mode
     *                   (must not be null)
     *
     * @return the updated {@link ConnectorProcessingMode}
     */
    ConnectorProcessingMode updateTruststore(
        @Nonnull String pmodeUuid,
        @Nonnull ConnectorTruststore truststore
    );

    /**
     * Retrieves a processing mode by its unique UUID.
     *
     * @param uuid the unique identifier of the processing mode to retrieve (must not be null)
     *
     * @return the {@link ConnectorProcessingMode} matching the given UUID, or {@code null} if no
     *     such processing mode exists
     */
    ConnectorProcessingMode findByUuid(@Nonnull String uuid);

    /**
     * Retrieves the processing mode associated with the given business domain.
     *
     * @param identifier the identifier of the business domain whose processing mode is retrieved
     *                   (must not be null)
     *
     * @return the {@link ConnectorProcessingMode} associated with the given business domain, or
     *     {@code null} if no matching processing mode exists
     */
    ConnectorProcessingMode findByBusinessDomainIdentifier(
        @Nonnull ConnectorBusinessDomainIdentifier identifier);

    /**
     * Retrieves all processing modes stored in the repository.
     *
     * @return a list of all defined {@link ConnectorProcessingMode} objects; never {@code null},
     *     and empty if none are available
     */
    List<ConnectorProcessingMode> findAll();
}
