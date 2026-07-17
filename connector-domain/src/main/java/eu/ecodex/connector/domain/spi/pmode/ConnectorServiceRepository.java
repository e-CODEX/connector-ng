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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Defines the contract for managing connector services within a specified business domain in the
 * connector system.
 */
public interface ConnectorServiceRepository {
    List<ConnectorService> saveAll(
        @Nonnull List<ConnectorService> services,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorService} based on its name and the associated business domain
     * identifier.
     *
     * @param name                     the name of the service; must not be null or blank.
     * @param businessDomainIdentifier the identifier representing the business domain with which
     *                                 the service is associated; must not be null.
     *
     * @return the {@link ConnectorService} matching the specified service name and business domain
     *     identifier, or null if no such service exists.
     */
    ConnectorService findByNameAndBusinessDomain(
        @Nonnull String name,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a list of {@link ConnectorService} objects associated with the specified business
     * domain identifier.
     *
     * @param identifier the {@link ConnectorBusinessDomainIdentifier} representing the unique
     *                   identifier of the business domain; must not be null.
     *
     * @return a {@link List} of {@link ConnectorService} instances associated with the given
     *     business domain identifier.
     */
    List<ConnectorService> findAllByBusinessDomainIdentifier(
        @Nonnull ConnectorBusinessDomainIdentifier identifier);
}
