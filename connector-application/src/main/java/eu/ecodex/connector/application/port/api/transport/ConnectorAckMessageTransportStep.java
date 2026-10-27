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

import eu.ecodex.connector.application.port.api.transport.command.UpdateMessageTransportCommand;
import jakarta.annotation.Nonnull;

/**
 * Use case for acknowledging a transport step of a connector message, by updating its transport
 * status.
 */
public interface ConnectorAckMessageTransportStep {
    /**
     * Updates the transport status of a message with the information carried by the given command.
     *
     * <p>The message is located by its connector identifier or by the identifier assigned by the
     * remote system (the EBMS ID).
     *
     * @param stepOrMessageOrRemoteSystemIdentifier identifier of the transport step, of the
     *                                              message, or of the message as known by the
     *                                              remote system (must not be null)
     * @param command                               the {@link UpdateMessageTransportCommand}
     *                                              containing the new transport status and any
     *                                              associated errors; must not be null
     */
    void execute(
        @Nonnull String stepOrMessageOrRemoteSystemIdentifier,
        @Nonnull UpdateMessageTransportCommand command);
}
