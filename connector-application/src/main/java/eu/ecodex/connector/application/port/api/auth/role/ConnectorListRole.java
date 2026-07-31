/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.role;

import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.List;

/**
 * Represents a contract for listing all user roles associated with the Connector system.
 * Implementations of this interface are responsible for retrieving all instances of
 * {@link ConnectorRole}.
 *
 * <p>
 * The primary use case of this interface is to provide a mechanism to fetch a collection
 * of user roles from the underlying data source, which could be a database or any other
 * persistence mechanism.
 */
public interface ConnectorListRole {
    /**
     * Retrieves a list of all user roles associated with the Connector system.
     *
     * @return a list of {@link ConnectorRole} representing all user roles stored
     *         in the underlying data source.
     */
    List<ConnectorRole> findAll();
}
