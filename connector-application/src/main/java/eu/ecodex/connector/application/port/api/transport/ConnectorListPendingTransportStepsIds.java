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

import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Use case for listing the identifiers of the pending transport steps of a backend.
 */
public interface ConnectorListPendingTransportStepsIds {

    /**
     * Retrieves the identifiers of the pending transport steps of the given backend.
     *
     * <p>Each identifier can be used to retrieve the corresponding transport step or message, for
     * example through {@link ConnectorRetrieveTransportStepByTransportId} and
     * {@link ConnectorRetrieveMessageByTransportId}.
     *
     * @param backendName name of the backend whose pending transport step identifiers are listed
     *                    (must not be null)
     *
     * @return a list of identifiers of the transport steps pending for the given backend; never
     *     {@code null}, and empty if nothing is pending
     */
    List<String> execute(@Nonnull String backendName);
}
