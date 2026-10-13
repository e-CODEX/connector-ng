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
 * Use case for listing the roles available in the connector system.
 */
public interface ConnectorListRole {
    /**
     * Retrieves all {@link ConnectorRole} instances stored in the underlying data source.
     *
     * @return a list of {@link ConnectorRole} objects representing all available user roles; never
     *     {@code null}, and empty if no roles are defined
     */
    List<ConnectorRole> execute();
}
