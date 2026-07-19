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
 * Defines a contract for retrieving a list of pending message IDs associated with a specified
 * backend.
 *
 * <p>Implementations of this interface are expected to query and return a list of message
 * identifiers that are currently pending and belong to the backend identified by the provided
 * backend name.
 */
public interface ConnectorListPendingMessageIds {
    /**
     * Executes the process to retrieve a list of pending message transport IDs associated with the
     * specified backend.
     *
     * @param backendName the name of the backend for which pending message IDs are requested. Must
     *                    not be null or empty.
     *
     * @return a list of strings representing the IDs of the pending messages associated with the
     *     specified backend. The list may be empty if no pending messages are available.
     */
    List<String> execute(String backendName);
}
