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

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for listing transport steps for a specific connector message.
 */
public interface ConnectorListTransportSteps {
    /**
     * Executes the transport step listing operation.
     *
     * @param pageRequest                     the pagination parameters used to retrieve the
     *                                        transport steps (must not be null)
     * @param messageOrRemoteSystemIdentifier identifier of the message, or of the message as known
     *                                        by the remote system, that the transport steps belong
     *                                        to; ignored if {@code null} or empty
     * @param linkPartnerName                 name of the link partner involved in the transport
     *                                        step; ignored if {@code null} or empty
     * @param statuses                        transport step statuses
     *                                        ({@link ConnectorMessageTransportStatus}) to match;
     *                                        ignored if {@code null} or empty
     *
     * @return a {@link ConnectorPageResult} containing a page of
     *     {@link ConnectorMessageTransportStep} objects matching the given filters
     *
     * @throws IllegalArgumentException if {@code pageRequest} is invalid
     */
    ConnectorPageResult<ConnectorMessageTransportStep> execute(
        @Nonnull ConnectorPageRequest pageRequest,
        String messageOrRemoteSystemIdentifier,
        String linkPartnerName,
        List<ConnectorMessageTransportStatus> statuses
    );
}
