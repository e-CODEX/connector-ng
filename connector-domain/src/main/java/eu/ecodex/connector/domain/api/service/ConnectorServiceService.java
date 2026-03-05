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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for managing {@link ConnectorService} entities.
 */
// TODO to be removed
public interface ConnectorServiceService {
    List<ConnectorService> persistAll(
            @Nonnull List<ConnectorService> services,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorService} by its name and associated business domain identifier.
     *
     * @param serviceName              the name of the service to find. Must not be {@code null}.
     * @param businessDomainIdentifier the unique identifier of the business domain in which the
     *                                 service resides. Must not be {@code null}.
     *
     * @return the {@link ConnectorService} matching the specified name and business domain
     *         identifier, or {@code null} if no matching service is found.
     */
    ConnectorService findByNameAndBusinessDomain(
            @Nonnull String serviceName,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
