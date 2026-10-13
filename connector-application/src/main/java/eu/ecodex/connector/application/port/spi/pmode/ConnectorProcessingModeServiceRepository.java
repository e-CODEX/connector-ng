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
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeService;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for persisting and querying {@link ConnectorProcessingModeService} objects,
 * which are scoped to a business domain.
 */
public interface ConnectorProcessingModeServiceRepository {
    /**
     * Persists the given services for the given business domain.
     *
     * @param services                 the services to save (must not be null)
     * @param businessDomainIdentifier the identifier of the business domain the services belong to
     *                                 (must not be null)
     *
     * @return the persisted {@link ConnectorProcessingModeService} objects, reflecting the state
     *     stored in the repository
     */
    List<ConnectorProcessingModeService> saveAll(
        @Nonnull List<ConnectorProcessingModeService> services,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a service by its name and business domain.
     *
     * @param name                     the name of the service (must not be null or blank)
     * @param businessDomainIdentifier the identifier of the business domain the service belongs to
     *                                 (must not be null)
     *
     * @return the {@link ConnectorProcessingModeService} matching the given name and business
     *     domain, or {@code null} if no such service exists
     */
    ConnectorProcessingModeService findByNameAndBusinessDomain(
        @Nonnull String name,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves all services belonging to the given business domain.
     *
     * @param identifier the identifier of the business domain (must not be null)
     *
     * @return the {@link ConnectorProcessingModeService} objects of the business domain; never
     *     {@code null}, and empty if there are none
     */
    List<ConnectorProcessingModeService> findAllByBusinessDomainIdentifier(
        @Nonnull ConnectorBusinessDomainIdentifier identifier);
}
