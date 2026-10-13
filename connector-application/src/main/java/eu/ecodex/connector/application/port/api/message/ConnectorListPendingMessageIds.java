/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import java.util.List;

/**
 * Use case for listing the identifiers of the messages of a backend that are pending retrieval.
 */
public interface ConnectorListPendingMessageIds {
    /**
     * Retrieves the identifiers of the pending messages addressed to the given backend.
     *
     * @param backendName name of the backend whose pending message identifiers are listed
     *
     * @return a list of message identifiers pending for the given backend; never {@code null},
     *     and empty if no messages are pending
     */
    List<String> execute(String backendName);
}
