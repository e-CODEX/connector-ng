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
 * Defines the contract for receiving inbound business messages from the gateway.
 *
 * <p>Implementations are responsible for handling the processing of inbound business messages,
 * such as verification, validation, and any necessary transformations.
 */
public interface ConnectorInboundBusinessMessageReceiver {
    /**
     * Processes an inbound business message described by the given command.
     *
     * <p>The method is responsible for all operations required to process and validate the
     * message according to the system's functional requirements. It returns normally if the message
     * was processed successfully. If processing fails, the failure is reported by an exception.
     *
     * @param command the {@link ConnectorInboundBusinessMessageCommand} encapsulating the details
     *                of the inbound business message to process (must not be null)
     */
    void execute(@Nonnull ConnectorInboundBusinessMessageCommand command);
}
