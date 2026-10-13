/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import java.util.List;

/**
 * Repository (port) for persisting and querying {@link ConnectorBusinessDomain} objects.
 */
public interface ConnectorBusinessDomainRepository {
    /**
     * Persists the given business domain.
     *
     * <p>The business domain encapsulates information such as the UUID, description, activation
     * status, associated properties and configuration source.
     *
     * @param businessDomain the business domain to save (must not be null)
     *
     * @return the persisted {@link ConnectorBusinessDomain}, reflecting the state stored in the
     *     repository
     */
    ConnectorBusinessDomain save(ConnectorBusinessDomain businessDomain);

    /**
     * Retrieves a business domain by its identifier.
     *
     * @param identifier the identifier of the business domain to retrieve (must not be null)
     *
     * @return the {@link ConnectorBusinessDomain} associated with the given identifier, or
     *     {@code null} if no matching business domain exists
     */
    ConnectorBusinessDomain findByIdentifier(ConnectorBusinessDomainIdentifier identifier);

    /**
     * Retrieves all business domains stored in the repository.
     *
     * @return a list of all configured {@link ConnectorBusinessDomain} objects; never {@code null},
     *     and empty if none are found
     */
    List<ConnectorBusinessDomain> findAll();
}
