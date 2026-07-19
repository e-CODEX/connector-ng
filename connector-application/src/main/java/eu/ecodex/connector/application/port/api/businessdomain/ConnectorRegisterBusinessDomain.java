/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.businessdomain;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import jakarta.annotation.Nonnull;

/**
 * Service interface for registering a new {@link ConnectorBusinessDomain} entity into the system.
 */
public interface ConnectorRegisterBusinessDomain {
    /**
     * Registers a new {@link ConnectorBusinessDomain} into the system. This method ensures that the
     * provided business domain is persisted and managed within the connector environment.
     *
     * @param businessDomain the {@link ConnectorBusinessDomain} object to be registered. It must be
     *                       a non-null value that includes the unique identifier, description, and
     *                       other configurations of the business domain.
     *
     * @return the registered {@link ConnectorBusinessDomain} entity, potentially enriched with
     *     system-generated or updated metadata after registration.
     */
    ConnectorBusinessDomain execute(@Nonnull ConnectorBusinessDomain businessDomain);
}
