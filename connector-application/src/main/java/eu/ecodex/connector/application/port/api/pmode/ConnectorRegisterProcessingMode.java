/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import jakarta.annotation.Nonnull;

/**
 * Service interface for registering a new {@link ConnectorProcessingMode} entity into the system.
 */
public interface ConnectorRegisterProcessingMode {
    /**
     * Registers a new {@link ConnectorProcessingMode} for a specific business domain.
     *
     * @param businessDomainIdentifier the identifier of the business domain to which the processing
     *                                 mode belongs. Must not be null.
     * @param mode                     the processing mode configuration to be registered. Must not
     *                                 be null.
     *
     * @return the registered {@link ConnectorProcessingMode} instance.
     */
    ConnectorProcessingMode execute(
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
        @Nonnull ConnectorProcessingMode mode);
}
