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

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Use case for listing the transport steps of the messages of a backend that are pending
 * retrieval.
 */
public interface ConnectorListPendingTransportSteps {

    /**
     * Retrieves the pending {@link ConnectorMessageTransportStep} objects of the given backend.
     *
     * <p>Each transport step carries the message to be retrieved by the backend (see
     * {@link ConnectorMessageTransportStep#transportedMessage()}).
     *
     * @param backendName name of the backend whose pending transport steps are listed (must not
     *                    be null)
     *
     * @return a list of {@link ConnectorMessageTransportStep} objects pending for the given
     *     backend; never {@code null}, and empty if nothing is pending
     */
    List<ConnectorMessageTransportStep> execute(@Nonnull String backendName);
}
