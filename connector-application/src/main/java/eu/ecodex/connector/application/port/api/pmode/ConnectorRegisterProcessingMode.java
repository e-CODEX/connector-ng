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
 * Use case for registering a new {@link ConnectorProcessingMode} in a business domain.
 */
public interface ConnectorRegisterProcessingMode {
    /**
     * Registers a new {@link ConnectorProcessingMode} for the given business domain.
     *
     * @param businessDomainIdentifier identifier of the business domain the processing mode belongs
     *                                 to (must not be null)
     * @param processingMode           the processing mode configuration to register (must not be
     *                                 null)
     *
     * @return the registered {@link ConnectorProcessingMode}, as persisted
     */
    ConnectorProcessingMode execute(
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
        @Nonnull ConnectorProcessingMode processingMode);
}
