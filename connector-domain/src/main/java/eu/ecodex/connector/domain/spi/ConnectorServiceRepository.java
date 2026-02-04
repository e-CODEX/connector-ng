/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.pmode.ConnectorService;

/**
 * Defines the contract for managing connector services within a specified business domain in the
 * connector system.
 */
public interface ConnectorServiceRepository {
    /**
     * Retrieves a {@link ConnectorService} based on its name and the associated business domain
     * identifier.
     *
     * @param serviceName              the name of the service; must not be null or blank.
     * @param businessDomainIdentifier the identifier representing the business domain with which
     *                                 the service is associated; must not be null.
     *
     * @return the {@link ConnectorService} matching the specified service name and business domain
     *         identifier, or null if no such service exists.
     */
    ConnectorService findByNameAndBusinessDomain(
            String serviceName, String businessDomainIdentifier);
}
