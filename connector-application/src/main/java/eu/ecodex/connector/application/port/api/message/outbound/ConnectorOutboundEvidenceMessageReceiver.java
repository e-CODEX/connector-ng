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

import eu.ecodex.connector.application.port.api.message.ConnectorTriggeredEvidenceMessageVerifier;
import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for receiving outbound evidence trigger messages submitted by a backend.
 *
 * <p>Implementations are responsible for turning an outbound evidence message command into a
 * {@link ConnectorTriggeredEvidenceMessage}, which can then be verified and processed (see
 * {@link ConnectorTriggeredEvidenceMessageVerifier} and
 * {@link ConnectorOutboundEvidenceMessageProcessor}).
 */
public interface ConnectorOutboundEvidenceMessageReceiver {

    /**
     * Processes the given command to create and return a
     * {@link ConnectorTriggeredEvidenceMessage}.
     *
     * @param command the outbound evidence message command describing the evidence requested by the
     *                backend (must not be null)
     *
     * @return the {@link ConnectorTriggeredEvidenceMessage} created from the command
     */
    ConnectorTriggeredEvidenceMessage execute(
        @Nonnull ConnectorOutboundEvidenceMessageCommand command);
}
