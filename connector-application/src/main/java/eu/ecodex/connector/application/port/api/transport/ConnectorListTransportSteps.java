/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.transport;

import eu.ecodex.connector.domain.model.filter.ConnectorTransportStepsListFilter;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;

/**
 * Service interface for listing transport steps for a specific connector message.
 */
public interface ConnectorListTransportSteps {
    /**
     * Executes the transport step listing operation.
     *
     * @param filter the pagination parameters and filter criteria used to retrieve the transport
     *               steps (must not be null)
     *
     * @return a {@link ConnectorPageResult} containing a page of
     *     {@link ConnectorMessageTransportStep} objects matching the given filters
     *
     * @throws IllegalArgumentException if {@code pageRequest} is invalid
     */
    ConnectorPageResult<ConnectorMessageTransportStep> execute(
        @Nonnull ConnectorTransportStepsListFilter filter);
}
