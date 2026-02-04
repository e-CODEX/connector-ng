/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;

/**
 * Defines the contract for managing and querying business domains within the connector system.
 */
public interface ConnectorBusinessDomainRepository {
    /**
     * Persists the given ConnectorBusinessDomain in the repository.
     *
     * @param businessDomain the business domain to be saved; must not be null. It encapsulates
     *                       information such as the uuid, description, activation status,
     *                       associated properties, and configuration source of a business domain.
     * @return the persisted instance of ConnectorBusinessDomain, which reflects the state stored
     *         in the repository.
     */
    ConnectorBusinessDomain save(ConnectorBusinessDomain businessDomain);

    /**
     * Retrieves a {@code ConnectorBusinessDomain} by its unique uuid.
     *
     * @param identifier the unique uuid of the business domain to retrieve; must not be null.
     *                   The uuid encapsulates details such as the message routing lane
     *                   for the business domain.
     * @return the {@code ConnectorBusinessDomain} associated with the given uuid, or null if
     *         no matching business domain is found in the repository.
     */
    ConnectorBusinessDomain findByIdentifier(ConnectorBusinessDomainIdentifier identifier);
}
