/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.inbound;

import jakarta.annotation.Nonnull;

/**
 * Defines the contract for receiving inbound evidence messages from the gateway.
 *
 * <p>Implementations handle the logic specific to evidence messages, which provide proof of the
 * handling of a business message (for example its delivery or rejection). Once received, an
 * evidence message can be processed by {@link ConnectorInboundEvidenceMessageProcessor}.
 */
public interface ConnectorInboundEvidenceMessageReceiver {
    /**
     * Processes an inbound evidence message described by the given command.
     *
     * <p>The method returns normally if the evidence message was processed successfully. If
     * processing fails, the failure is reported by an exception.
     *
     * @param command the {@link ConnectorInboundEvidenceMessageCommand} encapsulating the details
     *                of the inbound evidence message (must not be null)
     */
    void execute(@Nonnull ConnectorInboundEvidenceMessageCommand command);
}
