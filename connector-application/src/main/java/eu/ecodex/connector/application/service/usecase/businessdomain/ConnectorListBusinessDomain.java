/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.businessdomain;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import java.util.List;

/**
 * Service interface for listing all {@link ConnectorBusinessDomain} entities managed by the system.
 */
public interface ConnectorListBusinessDomain {
    /**
     * Retrieves all {@link ConnectorBusinessDomain} entities managed by the system.
     *
     * @return a list of {@link ConnectorBusinessDomain} objects representing all business domains
     *         available in the connector environment. The list is empty if no business domains are
     *         registered.
     */
    List<ConnectorBusinessDomain> execute();
}
