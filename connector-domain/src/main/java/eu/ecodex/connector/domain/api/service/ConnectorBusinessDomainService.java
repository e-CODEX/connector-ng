/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.service;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import jakarta.annotation.Nonnull;

/**
 * Service interface for managing and persisting {@link ConnectorBusinessDomain} entities.
 *
 * <p>This interface defines the contract for operations aimed at handling business domains
 * in the connector environment.
 *
 * <p>The implementation of this interface is expected to ensure that business domain data
 * integrity is maintained, including validation of unique identifiers to prevent duplication of
 * business domains.
 */
public interface ConnectorBusinessDomainService {
    /**
     * Registers a new {@link ConnectorBusinessDomain} into the system. This method ensures that the
     * provided business domain is persisted and managed within the connector environment.
     *
     * @param businessDomain the {@link ConnectorBusinessDomain} object to be registered. It must be
     *                       a non-null value that includes the unique identifier, description, and
     *                       other configurations of the business domain.
     *
     * @return the registered {@link ConnectorBusinessDomain} entity, potentially enriched with
     *         system-generated or updated metadata after registration.
     */
    ConnectorBusinessDomain register(@Nonnull ConnectorBusinessDomain businessDomain);

    /**
     * Retrieves a {@link ConnectorBusinessDomain} entity based on the provided uuid.
     *
     * @param identifier the unique uuid of the {@link ConnectorBusinessDomain} to search for. It
     *                   must be a non-null value representing a valid uuid.
     *
     * @return the {@link ConnectorBusinessDomain} entity associated with the specified uuid, or
     *         {@code null} if no matching entity is found.
     */
    ConnectorBusinessDomain findByIdentifier(@Nonnull ConnectorBusinessDomainIdentifier identifier);
}
