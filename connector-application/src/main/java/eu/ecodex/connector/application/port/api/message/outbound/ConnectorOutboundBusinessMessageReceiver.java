/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for receiving outbound business messages submitted by a backend.
 *
 * <p>Implementations are responsible for turning an outbound message command into a
 * {@link ConnectorBusinessMessage} that is ready to be dispatched to an external party. Processing
 * may include tasks such as:
 * <ul>
 *     <li>Enrichment of message metadata</li>
 *     <li>Validation and verification</li>
 *     <li>Transformation or normalization</li>
 * </ul>
 */
public interface ConnectorOutboundBusinessMessageReceiver {
    /**
     * Processes the given command to create and return a {@link ConnectorBusinessMessage}.
     *
     * <p>The command encapsulates the details of the outbound business message. Processing it may
     * involve transformations, validations, and enrichment of the message details.
     *
     * @param command the outbound business message command to process (must not be null)
     *
     * @return the {@link ConnectorBusinessMessage} created from the command, ready for dispatch
     *
     * @throws NullPointerException if {@code command} is {@code null}
     */
    ConnectorBusinessMessage execute(@Nonnull ConnectorOutboundBusinessMessageCommand command);
}
